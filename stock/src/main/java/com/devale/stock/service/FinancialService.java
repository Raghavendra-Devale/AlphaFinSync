package com.devale.stock.service;

import com.devale.stock.dao.FinancialDao;
import com.devale.stock.dao.StockDao;
import com.devale.stock.model.Financial;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialService {

    private final FinancialDao financialDao;
    private final StockDao stockDao;
    private final ExternalApiService externalApiService;

    public FinancialService(FinancialDao financialDao, StockDao stockDao,ExternalApiService externalApiService) {
        this.financialDao = financialDao;
        this.stockDao = stockDao;
        this.externalApiService = externalApiService;
    }

    private Double parseNumericOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return null;
        if (trimmed.equalsIgnoreCase("none") || trimmed.equalsIgnoreCase("null") || trimmed.equalsIgnoreCase("nan")) return null;
        try {
            return Double.valueOf(trimmed);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public int insertFinancial(String symbol, Financial financial){
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        financial.setStockId(stockId);
        return financialDao.insertFinancial(financial);
    }

    public int update(Financial financial){
        return financialDao.update(financial);
    }

    public Financial findByStockAndYear(String symbol, int year){
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        return financialDao.findByStockAndYear(stockId, year);
    }

    public List<Financial> findByStock(String symbol) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        return financialDao.findByStock(stockId);
    }

    public String upsert(String symbol, Financial financial) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        financial.setStockId(stockId);
        financialDao.upsert(financial);
        return "Updated financials for " + symbol + " successfully";
    }

    public void syncFinancialsFromApi(String symbol) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }

        JsonNode incomeJson = externalApiService.fetchIncomeStatement(symbol);
        JsonNode overviewJson = externalApiService.fetchCompanyOverviewRaw(symbol);
        JsonNode earningsJson = externalApiService.fetchEarnings(symbol);

        if (incomeJson == null || !incomeJson.has("annualReports")) {
            throw new RuntimeException("No income statement data for " + symbol);
        }

        // Build a lookup for annual EPS from the EARNINGS endpoint
        java.util.Map<Integer, Double> yearToEps = new java.util.HashMap<>();
        if (earningsJson != null && earningsJson.has("annualEarnings")) {
            for (JsonNode ae : earningsJson.get("annualEarnings")) {
                String fiscalDate = ae.path("fiscalDateEnding").asText("");
                if (fiscalDate.length() >= 4) {
                    int y = Integer.parseInt(fiscalDate.substring(0, 4));
                    Double epsVal = parseNumericOrNull(ae.path("reportedEPS").asText(null));
                    yearToEps.put(y, epsVal);
                }
            }
        }

        for (JsonNode report : incomeJson.get("annualReports")) {
            int year = Integer.parseInt(report.path("fiscalDateEnding").asText().substring(0, 4));

            Double revenue = report.path("totalRevenue").asText().isEmpty() ? null :
                    report.path("totalRevenue").asDouble();

            Double netIncome = report.path("netIncome").asText().isEmpty() ? null :
                    report.path("netIncome").asDouble();

            Double eps = yearToEps.getOrDefault(year, null);

            String mcStr = overviewJson.path("MarketCapitalization").asText(null);
            Double marketCap = (mcStr != null && !mcStr.isEmpty()) ? Double.valueOf(mcStr) : null;

            Financial financial = new Financial();
            financial.setStockId(stockId);
            financial.setYear(year);
            financial.setRevenue(revenue);
            financial.setNetIncome(netIncome);
            financial.setEps(eps);
            financial.setMarketCap(marketCap);

            financialDao.upsert(financial);
        }
    }

    public void syncFinancialsFromApiQuarter(String symbol) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            try {
                externalApiService.syncCompanyOverview(symbol);
                stockId = stockDao.findIdBySymbol(symbol);
            } catch (Exception ignored) {}
            if (stockId == null) {
                return; // nothing to do if stock still not present
            }
        }

        // Try quarterly earnings for EPS first
        JsonNode earningsJson = null;
        try {
            earningsJson = externalApiService.fetchEarnings(symbol);
        } catch (Exception ignored) {}
        boolean processed = false;
        if (earningsJson != null && earningsJson.has("quarterlyEarnings") && earningsJson.get("quarterlyEarnings").isArray()) {
            for (JsonNode report : earningsJson.get("quarterlyEarnings")) {
                String date = report.path("fiscalDateEnding").asText("");
                if (date.length() < 4) {
                    continue;
                }
                int year = Integer.parseInt(date.substring(0, 4));

                Double eps = parseNumericOrNull(report.path("reportedEPS").asText(null));

                if (eps != null) {
                    Financial existing = financialDao.findByStockAndYear(stockId, year);
                    if (existing != null) {
                        existing.setEps(eps);
                        financialDao.update(existing);
                    } else {
                        // Ensure annual financial rows exist, then retry the update for that year
                        try { syncFinancialsFromApi(symbol); } catch (Exception ignored) {}
                        existing = financialDao.findByStockAndYear(stockId, year);
                        if (existing != null) {
                            existing.setEps(eps);
                            financialDao.update(existing);
                        }
                    }
                }
            }
            processed = true;
        }

        // Fallback to INCOME_STATEMENT quarterly reports if EARNINGS is unavailable or empty
        if (!processed) {
            JsonNode incomeJson = null;
            try {
                incomeJson = externalApiService.fetchIncomeStatement(symbol);
            } catch (Exception ignored) {}
            if (incomeJson != null && incomeJson.has("quarterlyReports") && incomeJson.get("quarterlyReports").isArray()) {
                for (JsonNode report : incomeJson.get("quarterlyReports")) {
                    String date = report.path("fiscalDateEnding").asText("");
                    if (date.length() < 4) {
                        continue;
                    }
                    int year = Integer.parseInt(date.substring(0, 4));

                    Double eps = parseNumericOrNull(report.path("reportedEPS").asText(null));
                    if (eps != null) {
                        Financial existing = financialDao.findByStockAndYear(stockId, year);
                        if (existing != null) {
                            existing.setEps(eps);
                            financialDao.update(existing);
                        } else {
                            // Ensure annual rows exist first, then retry
                            try { syncFinancialsFromApi(symbol); } catch (Exception ignored) {}
                            existing = financialDao.findByStockAndYear(stockId, year);
                            if (existing != null) {
                                existing.setEps(eps);
                                financialDao.update(existing);
                            }
                        }
                    }
                }
            }
        }
    }



}

