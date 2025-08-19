package com.devale.stock.service;

import com.devale.stock.dao.StockDao;
import com.devale.stock.dto.CompanyOverview;
import com.devale.stock.model.Stock;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final StockDao stockDao;
    private final ExternalApiService externalApiService;
    public StockService(StockDao stockDao,ExternalApiService externalApiService){
        this.stockDao = stockDao;
        this.externalApiService = externalApiService;
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
    public Stock enrichStockIfNeeded(Stock stock) {
        if (stock.getSector() == null || "UNKNOWN".equalsIgnoreCase(stock.getSector())
                || stock.getSharesOutstanding() == null || stock.getSharesOutstanding() == 0) {
            CompanyOverview overview = externalApiService.fetchCompanyOverview(stock.getSymbol());
            stock.setName(overview.getName());
            stock.setSector(overview.getSector());
            stock.setSharesOutstanding(overview.getSharesOutstanding());
            stockDao.updateStock(stock); // add an UPDATE query in StockDao
        }
        return stock;
    }


}
