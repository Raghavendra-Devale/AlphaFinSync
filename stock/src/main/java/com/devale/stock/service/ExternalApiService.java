package com.devale.stock.service;

import com.devale.stock.dao.StockDao;
import com.devale.stock.model.Stock;
import com.fasterxml.jackson.databind.JsonNode;
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

    public ExternalApiService(RestTemplate restTemplate,
                              @Value("${alphavantage.api.key}") String apiKey,
                              @Value("${alphavantage.base-url}") String baseUrl,StockDao stockDao) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.stockDao = stockDao;
    }

    // Free daily candles (OHLC + volume). Use outputsize=full for long history.
    public String fetchDailyRaw(String symbol, String outputSize) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("outputsize", (outputSize == null ? "compact" : outputSize))
                .queryParam("apikey", apiKey);

        return restTemplate.getForObject(b.toUriString(), String.class);
    }

    // Service
    public void syncCompanyOverview(String symbol) {
        // call external API
        String url = "https://www.alphavantage.co/query?function=OVERVIEW&symbol="
                + symbol + "&apikey=" + apiKey;
        JsonNode overview = restTemplate.getForObject(url, JsonNode.class);

        if (overview == null || overview.isEmpty()) {
            throw new RuntimeException("No overview data for " + symbol);
        }

        String name = overview.path("Name").asText(symbol);
        String sector = overview.path("Sector").asText("UNKNOWN");
        long sharesOutstanding = overview.path("SharesOutstanding").asLong(0);

        Stock stock = stockDao.getStockBySymbol(symbol);
        if (stock == null) {
            stock = new Stock();
            stock.setSymbol(symbol);
        }
        stock.setName(name);
        stock.setSector(sector);
        stock.setSharesOutstanding(sharesOutstanding);

        stockDao.upsertStock(stock);
    }

}
