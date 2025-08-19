package com.devale.stock.dao;

import com.devale.stock.model.Stock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockDao {
    private final JdbcTemplate jdbcTemplate;

    public StockDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate= jdbcTemplate;
    }

    public int getStocksCount() {
        String sql = "SELECT COUNT(*) FROM stocks";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int insertStock (Stock stock){
        String insertQuery = "INSERT INTO stocks(symbol, name, sector, shares_outstanding) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(
                insertQuery,
                stock.getSymbol(),
                stock.getName(),
                stock.getSector(),
                stock.getSharesOutstanding()
        );
    }

    public Stock getStockBySymbol(String symbol) {
        String fetchQuery = "SELECT * FROM stocks WHERE symbol = ?";
        return jdbcTemplate.queryForObject(
                fetchQuery,
                new Object[]{symbol},
                (rs, rowNum) -> new Stock(
                        rs.getLong("id"),
                        rs.getString("symbol"),
                        rs.getString("name"),
                        rs.getString("sector"),
                        rs.getLong("shares_outstanding")
                )
        );
    }

    public List<Stock> getAllStocks() {
        String getAllStocksQuery = "SELECT * FROM stocks";
        return jdbcTemplate.query(getAllStocksQuery, (rs, rowNum) -> {
            Stock stock = new Stock();
            stock.setId(rs.getLong("id"));
            stock.setSymbol(rs.getString("symbol"));
            stock.setName(rs.getString("name"));
            stock.setSector(rs.getString("sector"));
            stock.setSharesOutstanding(rs.getLong("shares_outstanding"));
            return stock;
        });
    }

    public List<Stock> getStocksBySector(String sector){
        String getBySectorQuery = "SELECT * FROM stocks WHERE sector = ?";
        return jdbcTemplate.query(getBySectorQuery,
                new Object[]{sector},
                (rs, rowNum) -> {
                Stock stock = new Stock();
                stock.setId(rs.getLong("id"));
                stock.setSymbol(rs.getString("symbol"));
                stock.setName(rs.getString("name"));
                stock.setSector(rs.getString("sector"));
                stock.setSharesOutstanding(rs.getLong("shares_outstanding"));
                return stock; } );
    }

    public boolean stockExists(Long stockId) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{stockId}, Integer.class);
        return count != null && count > 0;
    }

    public Long findIdBySymbol(String symbol) {
        String sql = "SELECT id FROM stocks WHERE symbol = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, symbol);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Stock findBySymbol(String symbol) {
        String sql = "SELECT * FROM stocks WHERE symbol = ?";
        List<Stock> stocks = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Stock stock = new Stock();
            stock.setId(rs.getLong("id"));
            stock.setSymbol(rs.getString("symbol"));
            stock.setName(rs.getString("name"));
            stock.setSector(rs.getString("sector"));
            stock.setSharesOutstanding(rs.getObject("shares_outstanding", Long.class));
            return stock;
        }, symbol);

        return stocks.isEmpty() ? null : stocks.get(0);
    }

    public int updateStock(Stock stock) {
        String sql = "UPDATE stocks SET name=?, sector=?, shares_outstanding=? WHERE symbol=?";
        return jdbcTemplate.update(sql,
                stock.getName(),
                stock.getSector(),
                stock.getSharesOutstanding(),
                stock.getSymbol());
    }
    public void upsertStock(Stock stock) {
        Stock existing = findBySymbol(stock.getSymbol());
        if (existing == null) {
            insertStock(stock);
        } else {
            if (stock.getName() != null) existing.setName(stock.getName());
            if (stock.getSector() != null) existing.setSector(stock.getSector());
            if (stock.getSharesOutstanding() != null) existing.setSharesOutstanding(stock.getSharesOutstanding());
            updateStock(existing);
        }
    }


}
