package com.devale.stock.service;

import com.devale.stock.dao.FinancialDao;
import com.devale.stock.dao.StockDao;
import com.devale.stock.model.Financial;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialService {

    private final FinancialDao financialDao;
    private final StockDao stockDao;

    public FinancialService(FinancialDao financialDao, StockDao stockDao) {
        this.financialDao = financialDao;
        this.stockDao = stockDao;
    }

    public int insertFinancial(String symbol, Financial financial){
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        financial.setStockId(stockId);
        return financialDao.insertFinancial(financial);
    }

    public int update(Financial financial){
        return financialDao.update(financial);
    }

    public Financial findByStockAndYear(String symbol, int year){
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        return financialDao.findByStockAndYear(stockId, year);
    }

    public List<Financial> findByStock(String symbol) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        return financialDao.findByStock(stockId);
    }

    public String upsert(String symbol, Financial financial) {
        Long stockId = stockDao.findIdBySymbol(symbol);
        if (stockId == null) {
            throw new RuntimeException("Stock not found for symbol: " + symbol);
        }
        financial.setStockId(stockId);
        financialDao.upsert(financial);
        return "Updated financials for " + symbol + " successfully";
    }
}
