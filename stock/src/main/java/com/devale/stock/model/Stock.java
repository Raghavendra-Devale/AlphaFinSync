package com.devale.stock.model;

public class Stock {
    private Long id;
    private String symbol;
    private String name;
    private String sector;
    private Long sharesOutstanding;

    public Stock(){}

    public Stock(Long id, String symbol, String name, String sector, Long sharesOutstanding) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.sharesOutstanding = sharesOutstanding;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Long getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(Long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }
}
