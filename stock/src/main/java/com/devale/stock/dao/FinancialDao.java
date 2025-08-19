package com.devale.stock.dao;

import com.devale.stock.model.Financial;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FinancialDao {

    private final JdbcTemplate jdbcTemplate;

    public FinancialDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertFinancial(Financial financial) {
        String sql = "INSERT INTO financials (stock_id, year, revenue, net_income, eps, market_cap) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                financial.getStockId(),
                financial.getYear(),
                financial.getRevenue(),
                financial.getNetIncome(),
                financial.getEps(),
                financial.getMarketCap()
        );
    }

    public int update(Financial financial) {
        String sql = "UPDATE financials " +
                "SET revenue = ?, net_income = ?, eps = ?, market_cap = ? " +
                "WHERE stock_id = ? AND year = ?";
        return jdbcTemplate.update(sql,
                financial.getRevenue(),
                financial.getNetIncome(),
                financial.getEps(),
                financial.getMarketCap(),
                financial.getStockId(),
                financial.getYear()
        );
    }

    public Financial findByStockAndYear(Long stockId, int year) {
        String sql = "SELECT * FROM financials WHERE stock_id = ? AND year = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Financial fin = new Financial();
                fin.setId(rs.getLong("id"));
                fin.setStockId(rs.getLong("stock_id"));
                fin.setYear(rs.getInt("year"));
                fin.setRevenue(rs.getDouble("revenue"));
                fin.setNetIncome(rs.getDouble("net_income"));
                fin.setEps(rs.getDouble("eps"));
                fin.setMarketCap(rs.getDouble("market_cap"));
                return fin;
            }, stockId, year);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Financial> findByStock(Long stockId) {
        String sql = "SELECT * FROM financials WHERE stock_id = ? ORDER BY year DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Financial f = new Financial();
            f.setId(rs.getLong("id"));
            f.setStockId(rs.getLong("stock_id"));
            f.setYear(rs.getInt("year"));
            f.setRevenue(rs.getDouble("revenue"));
            f.setNetIncome(rs.getDouble("net_income"));
            f.setEps(rs.getDouble("eps"));
            f.setMarketCap(rs.getDouble("market_cap"));
            return f;
        }, stockId);
    }

    public void upsert(Financial financial) {
        Financial existing = findByStockAndYear(financial.getStockId(), financial.getYear());
        if (existing == null) {
            insertFinancial(financial);
        } else {
            update(financial);
        }
    }
}
