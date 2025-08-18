package com.devale.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class StockPrice {
    private int id;
    private Long stockId;
    private double price;
    @JsonProperty("date_time")
    private LocalDateTime dateTime;


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    private long volume;

    public StockPrice() {
    }

    public StockPrice(int id, Long stockId, double price, long volume) {
        this.id = id;
        this.stockId = stockId;
        this.price = price;
        this.volume = volume;
    }

    public StockPrice(int id, Long stockId, double price, long volume, LocalDateTime dateTime) {
        this.id = id;
        this.stockId = stockId;
        this.price = price;
        this.volume = volume;
        this.dateTime = dateTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
