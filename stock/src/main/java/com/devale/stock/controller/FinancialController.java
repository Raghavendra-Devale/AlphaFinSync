package com.devale.stock.controller;

import com.devale.stock.model.Financial;
import com.devale.stock.service.FinancialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financials")
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }


    @GetMapping("/{symbol}")
    public List<Financial> getFinancialsBySymbol(@PathVariable String symbol) {
        return financialService.findByStock(symbol);
    }


    @GetMapping("/{symbol}/{year}")
    public Financial getFinancialBySymbolAndYear(@PathVariable String symbol, @PathVariable int year) {
        return financialService.findByStockAndYear(symbol, year);
    }


    @PostMapping("/{symbol}")
    public String insertFinancial(@PathVariable String symbol, @RequestBody Financial financial) {
        financialService.insertFinancial(symbol, financial);
        return "Inserted financials for " + symbol + " successfully";
    }


    @PutMapping("/{symbol}")
    public String upsertFinancial(@PathVariable String symbol, @RequestBody Financial financial) {
        return financialService.upsert(symbol, financial);
    }
}
