package com.devale.stock.model;

public class Financial {
    private Long id;
    private Long stockId;
    private Integer year;
    private Double revenue;
    private Double netIncome;
    private Double eps;
    private Double marketCap;

    public Financial(){}
    public Financial(Long id, Long stockId, Integer year, Double revenue, Double netIncome, Double eps, Double marketCap) {
        this.id = id;
        this.stockId = stockId;
        this.year = year;
        this.revenue = revenue;
        this.netIncome = netIncome;
        this.eps = eps;
        this.marketCap = marketCap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(Double netIncome) {
        this.netIncome = netIncome;
    }

    public Double getEps() {
        return eps;
    }

    public void setEps(Double eps) {
        this.eps = eps;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }
}
