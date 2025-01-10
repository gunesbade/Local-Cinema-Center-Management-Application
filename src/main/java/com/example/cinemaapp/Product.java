package com.example.cinemaapp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.math.BigDecimal;

public class Product {

    private int productID;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    // Bu property, tabloya adet (quantity) için ekledik
    private IntegerProperty quantity = new SimpleIntegerProperty(0);

    public Product(int productID, String productName, BigDecimal price, int stockQuantity) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.quantity.set(0); // 0 adet ile başlıyor ürünler!!
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Quantity
    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int q) {
        this.quantity.set(q);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
}
