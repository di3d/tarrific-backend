package com.tarrific.backend.dto;

import java.math.BigDecimal;

public class ExchangeResponse {
    private BigDecimal converted;
    private BigDecimal rate;
    private String to;
    private String date;

    public ExchangeResponse(BigDecimal converted, BigDecimal rate, String to, String date) {
        this.converted = converted;
        this.rate = rate;
        this.to = to;
        this.date = date;
    }

    public BigDecimal getConverted() {
        return converted;
    }

    public void setConverted(BigDecimal converted) {
        this.converted = converted;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
