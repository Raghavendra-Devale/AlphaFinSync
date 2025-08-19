package com.devale.stock.service;

import com.devale.stock.dao.StockDao;
import com.devale.stock.dto.CompanyOverview;
import com.devale.stock.model.Stock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    private final StockDao stockDao;
    ObjectMapper objectMapper = new ObjectMapper();

    public ExternalApiService(RestTemplate restTemplate,
                              @Value("${alphavantage.api.key}") String apiKey,
                              @Value("${alphavantage.base-url}") String baseUrl,StockDao stockDao) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.stockDao = stockDao;
    }


    public String fetchDailyRaw(String symbol, String outputSize) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("outputsize", (outputSize == null ? "compact" : outputSize))
                .queryParam("apikey", apiKey);

        return restTemplate.getForObject(b.toUriString(), String.class);
    }

    public void syncCompanyOverview(String symbol) {

        String url = "https://www.alphavantage.co/query?function=OVERVIEW&symbol="
                + symbol + "&apikey=" + apiKey;
        JsonNode overview = restTemplate.getForObject(url, JsonNode.class);

        if (overview == null || overview.isEmpty()) {
            throw new RuntimeException("No overview data for " + symbol);
        }

        String name = overview.path("Name").asText(symbol);
        String sector = overview.path("Sector").asText("UNKNOWN");
        long sharesOutstanding = overview.path("SharesOutstanding").asLong(0);

        Stock stock = stockDao.findBySymbol(symbol);
        if (stock == null) {
            stock = new Stock();
            stock.setSymbol(symbol);
        }
        stock.setName(name);
        stock.setSector(sector);
        stock.setSharesOutstanding(sharesOutstanding);

        System.out.println("Overview from API for " + symbol + ": " + overview);
        System.out.println("Mapped stock: " + stock);

        stockDao.upsertStock(stock);
    }
    public CompanyOverview fetchCompanyOverview(String symbol) {
        String url = baseUrl + "/query?function=OVERVIEW&symbol=" + symbol + "&apikey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);

            CompanyOverview overview = new CompanyOverview();
            overview.setSymbol(root.path("Symbol").asText(symbol));
            overview.setName(root.path("Name").asText(symbol));
            overview.setSector(root.path("Sector").asText("UNKNOWN"));
            overview.setSharesOutstanding(root.path("SharesOutstanding").asLong(0));

            return overview;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse company overview for " + symbol, e);
        }
    }
    public String getInsiderTransactions(String symbol){
        String url = baseUrl+"?function=INSIDER_TRANSACTIONS&symbol="+symbol+"&apikey="+apiKey;
        return restTemplate.getForObject(url,String.class);
    }

    public JsonNode fetchIncomeStatement(String symbol) {
        String url = baseUrl + "/query?function=INCOME_STATEMENT&symbol=" + symbol + "&apikey=" + apiKey;
        return restTemplate.getForObject(url, JsonNode.class);
    }

}
