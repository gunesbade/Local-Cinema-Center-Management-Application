package com.example.cinemaapp;

import java.math.BigDecimal;

public class CustomerInfo {
    private String name;
    private String surname;
    private BigDecimal discountRate; // 0.00 or 0.50

    public CustomerInfo() {
        this.name = "";
        this.surname = "";
        this.discountRate = BigDecimal.ZERO;
    }

    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String s) {
        this.surname = s;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }
    public void setDiscountRate(BigDecimal d) {
        this.discountRate = d;
    }
}
