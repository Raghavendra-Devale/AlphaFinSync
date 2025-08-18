package com.devale.stock.service;

import com.devale.stock.dao.StockDao;
import com.devale.stock.model.Stock;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final StockDao stockDao;

    public StockService(StockDao stockDao){
        this.stockDao = stockDao;
    }

    public int getStocksCount() {
        return stockDao.getStocksCount();
    }

    public int addStock(Stock stock){
        return stockDao.insertStock(stock);
    }

    public Stock getStockBySymbol(String symbol){
        return stockDao.getStockBySymbol(symbol);
    }

    public List<Stock> getAllStocks() { return stockDao.getAllStocks(); }

    public List<Stock> getStockBySector(String sector) {return stockDao.getStocksBySector(sector); }

    public void upsertStock(Stock stock){ stockDao.upsertStock(stock);}
    
}
