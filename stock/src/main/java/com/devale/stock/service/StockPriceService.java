package com.devale.stock.service;

import com.devale.stock.dao.StockDao;
import com.devale.stock.dao.StockPriceDao;
import com.devale.stock.model.Stock;
import com.devale.stock.model.StockPrice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

@Service
public class StockPriceService {
    private final StockPriceDao stockPriceDao;
    private final StockDao stockDao;
    private final ExternalApiService externalApiService;

    private final ObjectMapper mapper = new ObjectMapper();

    public StockPriceService(StockPriceDao stockPriceDao, StockDao stockDao, ExternalApiService externalApiService) {
        this.stockPriceDao = stockPriceDao;
        this.stockDao = stockDao;
        this.externalApiService = externalApiService;
    }

    public int insertStockPrice(StockPrice stockPrice) {
        if (!stockDao.stockExists((long) stockPrice.getStockId())) {
            throw new IllegalArgumentException("Stock with ID " + stockPrice.getStockId() + " does not exist.");
        }
        return stockPriceDao.insertStockPrice(stockPrice);
    }

    public List<StockPrice> getAllStockPriceByStockId(int stock_id) {
        return stockPriceDao.getAllStockPriceByStockId(stock_id);
    }

    public List<StockPrice> getRange(int stockId, LocalDate start, LocalDate end) {
        return stockPriceDao.getRange(
                stockId,
                start.atStartOfDay(),
                end.atTime(23, 59, 59));
    }

    public List<StockPrice> getStocksInRange(int stockId, String startDate, String endDate) {
        LocalDate start;
        LocalDate end;

        try {
            // if we get only dates
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

            return stockPriceDao.getStockPriceInRange(
                    stockId,
                    start.atStartOfDay(),
                    end.atTime(23, 59, 59));

        } catch (Exception e) {
            // if we get full date and time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDT = LocalDateTime.parse(startDate, formatter);
            LocalDateTime endDT = LocalDateTime.parse(endDate, formatter);
            return stockPriceDao.getStockPriceInRange(stockId, startDT, endDT);
        }
    }

    // Main use-case: sync + return range
    public List<StockPrice> syncDailyAndGetRange(String symbol, LocalDate start, LocalDate end) {
        if (start == null)
            start = LocalDate.now().minusDays(30);
        if (end == null)
            end = LocalDate.now();

        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            // auto-create stock entry if not exists
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            stock.setName(symbol); // or call external API to fetch full name
            stock.setSector("UNKNOWN"); // default sector or infer later

            stockId = (long) stockDao.insertStock(stock); // returns generated ID
        }

        // 1) fetch raw JSON
        String json = externalApiService.fetchDailyRaw(symbol, "compact");

        // 2) parse and validate
        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Alpha Vantage JSON", e);
        }
        // 3) iterate and upsert (check-before-insert)
        JsonNode series = root.path("Time Series (Daily)");

        Iterator<String> it = series.fieldNames();
        while (it.hasNext()) {
            String dateStr = it.next(); // e.g., "2025-08-14"
            LocalDate d = LocalDate.parse(dateStr); // ISO date
            if (d.isBefore(start) || d.isAfter(end))
                continue;

            JsonNode day = series.get(dateStr);
            double close = day.get("4. close").asDouble();
            long volume = day.get("5. volume").asLong();

            if (!stockPriceDao.existsByStockAndDate(stockId, d)) {
                StockPrice sp = new StockPrice();
                sp.setStockId(stockId);
                sp.setPrice(close);
                sp.setVolume(volume);
                sp.setDateTime(d.atStartOfDay()); // daily candle at midnight
                stockPriceDao.insert(sp);
            }
        }

        // 4) return what we have for that range (from DB)
        return stockPriceDao.findByStockAndRange(stockId, start.atStartOfDay(), end.atTime(23, 59, 59));
    }

}
