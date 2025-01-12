package com.example.cinemaapp;

import java.math.BigDecimal;

/**
 * Represents customer information, including name, surname, and discount rate.
 * This class is used to manage customer-specific data within the Cinema App.
 */
public class CustomerInfo {
    private String name;// Customer's first name
    private String surname; // Customer's last name
    private BigDecimal discountRate; // Discount rate (e.g., 0.00 for no discount, 0.50 for 50% discount)

    /**
     * Constructs a new {@code CustomerInfo} object with default values.
     * Name and surname are empty strings, and the discount rate is set to 0.00.
     */
    public CustomerInfo() {
        this.name = "";
        this.surname = "";
        this.discountRate = BigDecimal.ZERO;
    }

    /**
     * Returns the customer's first name.
     *
     * @return the customer's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's first name.
     *
     * @param n the first name to set
     */
    public void setName(String n) {
        this.name = n;
    }

    /**
     * Returns the customer's last name.
     *
     * @return the customer's last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the customer's last name.
     *
     * @param s the last name to set
     */
    public void setSurname(String s) {
        this.surname = s;
    }

    /**
     * Returns the customer's discount rate.
     *
     * @return the discount rate as a {@link BigDecimal}
     */
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    /**
     * Sets the customer's discount rate.
     *
     * @param d the discount rate to set, as a {@link BigDecimal}
     */
    public void setDiscountRate(BigDecimal d) {
        this.discountRate = d;
    }
}
