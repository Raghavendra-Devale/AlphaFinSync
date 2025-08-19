package com.devale.stock.controller;

import com.devale.stock.model.StockPrice;
import com.devale.stock.service.StockPriceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockSyncController {

    private final StockPriceService stockPriceService;

    public StockSyncController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }


    @GetMapping("/{symbol}/sync-daily")
    public List<StockPrice> syncDaily(
            @PathVariable String symbol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return stockPriceService.syncDailyAndGetRange(symbol, start, end);
    }

}
