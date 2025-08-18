package com.devale.stock.dao;

import com.devale.stock.model.Stock;
import com.devale.stock.model.StockPrice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockPriceDao {
    private final JdbcTemplate jdbcTemplate;

    public StockPriceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertStockPrice(StockPrice stockPrice) {


        String insertQuery = "INSERT INTO stock_prices (stock_id, price, volume, date_time) VALUES (?, ?, ?, ?)";
        LocalDateTime dateTimeToInsert =
                stockPrice.getDateTime() != null
                        ? stockPrice.getDateTime()
                        : LocalDateTime.now();

        return jdbcTemplate.update(
                insertQuery,
                stockPrice.getStockId(),
                stockPrice.getPrice(),
                stockPrice.getVolume(),
                Timestamp.valueOf(dateTimeToInsert)
        );
    }

    public List<StockPrice> getAllStockPriceByStockId(int stockId){

        String query = "select id,stock_id,price,volume,date_time from stock_prices where stock_id = ?";
        return jdbcTemplate.query(query,
                (rs,rowNum) ->
                        new StockPrice(
                                rs.getInt("id"),
                                rs.getLong("stock_id"),
                                rs.getDouble("price"),
                                rs.getLong("volume"),
                                rs.getTimestamp("date_time").toLocalDateTime()

                        ),stockId
        );
    }

    public List<StockPrice> getRange(int stockId,LocalDateTime startDate, LocalDateTime endDate){

        String sql ="SELECT * FROM stock_prices WHERE stock_id = ? AND date_time BETWEEN ? AND ? ORDER BY date_time ASC";

        return jdbcTemplate.query(sql,
                (rs,rowNum)->
                        new StockPrice(
                                rs.getInt("id"),
                                rs.getLong("stock_id"),
                                rs.getDouble("price"),
                                rs.getLong("volume"),
                                rs.getTimestamp("date_time").toLocalDateTime()
                        ),stockId,Timestamp.valueOf(startDate),Timestamp.valueOf(endDate)
                );
    }

    public List<StockPrice> getStockPriceInRange(int stockId,LocalDateTime startDate, LocalDateTime endDate){
        String sql = "SELECT * FROM stock_prices WHERE stock_id = ? AND date_time BETWEEN ? AND ? ORDER BY date_time ASC";
        return jdbcTemplate.query(sql,
                (rs,rowNum)->
                    new StockPrice(
                            rs.getInt("id"),
                            rs.getLong("stock_id"),
                            rs.getDouble("price"),
                            rs.getLong("volume"),
                            rs.getTimestamp("date_time").toLocalDateTime()
                    ),stockId,
                    Timestamp.valueOf(startDate),
                    Timestamp.valueOf(endDate) );
    }

    public boolean existsByStockAndDate(Long stockId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM stock_prices WHERE stock_id = ? AND DATE(date_time) = ?";
        Integer c = jdbcTemplate.queryForObject(sql, Integer.class, stockId, Date.valueOf(date));
        return c != null && c > 0;
    }

    public int insert(StockPrice sp) {
        String sql = "INSERT INTO stock_prices (stock_id, price, volume, date_time) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                sp.getStockId(),
                sp.getPrice(),
                sp.getVolume(),
                Timestamp.valueOf(sp.getDateTime()));
    }

    // Optional: get the rows we now have for the requested range
    public List<StockPrice> findByStockAndRange(Long stockId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM stock_prices WHERE stock_id = ? AND date_time BETWEEN ? AND ? ORDER BY date_time ASC";
        return jdbcTemplate.query(sql,
                (rs, rn) -> new StockPrice(
                        rs.getInt("id"),
                        rs.getLong("stock_id"),
                        rs.getDouble("price"),
                        rs.getLong("volume"),
                        rs.getTimestamp("date_time").toLocalDateTime()
                ),
                stockId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }



}
