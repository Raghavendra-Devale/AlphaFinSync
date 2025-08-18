package com.devale.stock.controller;

import com.devale.stock.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExternalApiController {
    @Autowired
    private ExternalApiService externalAPIService;

    @GetMapping("/stock/{symbol}")
    public String getStockData(@PathVariable String symbol){ //march 25 to today
        System.out.println("hereeee ");
        return externalAPIService.fetchDailyRaw(symbol,null);
    }

}

