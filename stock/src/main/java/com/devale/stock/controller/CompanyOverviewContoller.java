package com.devale.stock.controller;

import com.devale.stock.dto.CompanyOverview;
import com.devale.stock.service.ExternalApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyOverviewContoller {
    private final ExternalApiService externalApiService;
    public CompanyOverviewContoller(ExternalApiService externalApiService){
        this.externalApiService = externalApiService;
    }

    @GetMapping("/overview/{symbol}")
    public CompanyOverview fetchOverview(@PathVariable String symbol){
        return externalApiService.fetchCompanyOverview(symbol);
    }

    @PostMapping("/overview/sync/{symbol}")
    public String syncOverview(@PathVariable String symbol){
        externalApiService.syncCompanyOverview(symbol);
        return "Overview updated successfully for : "+symbol;
    }
}
