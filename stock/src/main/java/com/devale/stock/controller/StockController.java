package com.devale.stock.controller;

import com.devale.stock.model.Stock;
import com.devale.stock.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StockController {
    private final StockService stockService;
    public StockController(StockService stockService){
        this.stockService = stockService;
    }
    @GetMapping("/stocks/count")
    public int getStocksCount(){
        return stockService.getStocksCount();
    }

    @PostMapping("/stocks/addStock")
    public int addStock(@RequestBody Stock stock){
        return stockService.addStock(stock);
    }

    @GetMapping("/stocks/getBySymbol")
    public Stock getStockBySymbol(@RequestParam String symbol){
        return stockService.getStockBySymbol(symbol);
    }

    @GetMapping("/stocks/getAllStocks")
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }

    @GetMapping("/sector/getStocksBySector")
    public List<Stock> getStocksBySector(@RequestParam String sector) {return stockService.getStockBySector(sector);}
}
