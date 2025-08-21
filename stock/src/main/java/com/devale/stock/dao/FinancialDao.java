package com.devale.stock.dao;

import com.devale.stock.model.Financial;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;

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
                BigDecimal revenueBd = rs.getBigDecimal("revenue");
                BigDecimal netIncomeBd = rs.getBigDecimal("net_income");
                BigDecimal epsBd = rs.getBigDecimal("eps");
                BigDecimal marketCapBd = rs.getBigDecimal("market_cap");
                fin.setRevenue(revenueBd != null ? revenueBd.doubleValue() : null);
                fin.setNetIncome(netIncomeBd != null ? netIncomeBd.doubleValue() : null);
                fin.setEps(epsBd != null ? epsBd.doubleValue() : null);
                fin.setMarketCap(marketCapBd != null ? marketCapBd.doubleValue() : null);
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
            BigDecimal epsBd = rs.getBigDecimal("eps");
            BigDecimal revenueBd = rs.getBigDecimal("revenue");
            BigDecimal netIncomeBd = rs.getBigDecimal("net_income");
            BigDecimal marketCapBd = rs.getBigDecimal("market_cap");
            f.setEps(epsBd != null ? epsBd.doubleValue() : null);
            f.setRevenue(revenueBd != null ? revenueBd.doubleValue() : null);
            f.setNetIncome(netIncomeBd != null ? netIncomeBd.doubleValue() : null);
            f.setMarketCap(marketCapBd != null ? marketCapBd.doubleValue() : null);

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
