package com.devale.stock.controller;

import com.devale.stock.model.StockPrice;
import com.devale.stock.service.StockPriceService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @PostMapping("/stockPrice/addPrice")
    public ResponseEntity<?> insertStockPrice(@RequestBody StockPrice stockPrice) {
        try {
            int rowsInserted = stockPriceService.insertStockPrice(stockPrice);
            return ResponseEntity.ok("Inserted " + rowsInserted + " row(s) successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/stockPrice/getByStockId/{stcok_id}")
    public List<StockPrice> getAllStockPriceByStockId(@PathVariable int stcok_id) {
        return stockPriceService.getAllStockPriceByStockId(stcok_id);
    }

    @GetMapping("/stockPrice/getByRange/{stockId}/{startDate}/{endDate}")
    public List<StockPrice> getRange(
            @PathVariable int stockId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return stockPriceService.getRange(stockId, startDate, endDate);
    }

    @GetMapping("/stockPrice/inRange")
    public List<StockPrice> getStocksInRange(@RequestParam int stockId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return stockPriceService.getStocksInRange(stockId, startDate, endDate);
    }
}