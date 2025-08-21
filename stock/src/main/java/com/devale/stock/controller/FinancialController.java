package com.devale.stock.controller;

import com.devale.stock.model.Financial;
import com.devale.stock.service.FinancialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
    public ResponseEntity<String> insertFinancial(@PathVariable String symbol, @RequestBody Financial financial) {
        try {
            financialService.insertFinancial(symbol, financial);
            return ResponseEntity.ok("Inserted financials for " + symbol + " successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Insert failed: " + e.getMessage());
        }
    }


    @PutMapping("/{symbol}")
    public ResponseEntity<String> upsertFinancial(@PathVariable String symbol, @RequestBody Financial financial) {
        try {
            String msg = financialService.upsert(symbol, financial);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Upsert failed: " + e.getMessage());
        }
    }


    @PostMapping("/sync/{symbol}")
    public ResponseEntity<String> syncFinancials(@PathVariable String symbol) {
        try {
            financialService.syncFinancialsFromApi(symbol);
            return ResponseEntity.ok("Financials for " + symbol + " synced successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Sync failed: " + e.getMessage());
        }
    }

    @PostMapping("/sync/eps/{symbol}")
    public ResponseEntity<String> syncFinancialsQuarterlyFromApi(@PathVariable String symbol) {
        try {
            financialService.syncFinancialsFromApiQuarter(symbol);
            return ResponseEntity.ok("Quarterly EPS for " + symbol + " synced successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Quarterly EPS sync failed: " + e.getMessage());
        }
    }
}
