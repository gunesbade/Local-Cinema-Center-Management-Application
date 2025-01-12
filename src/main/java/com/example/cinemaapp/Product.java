package com.example.cinemaapp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.math.BigDecimal;

/**
 * Represents a product in the cinema application, including details such as
 * product ID, name, price, stock quantity, and the quantity selected for purchase.
 */
public class Product {

    private int productID;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    // This property is added for table binding to show selected quantity.
    private IntegerProperty quantity = new SimpleIntegerProperty(0);

    /**
     * Constructs a new {@code Product} object with the specified details.
     *
     * @param productID      the unique identifier for the product
     * @param productName    the name of the product
     * @param price          the price of the product
     * @param stockQuantity  the available stock quantity of the product
     */
    public Product(int productID, String productName, BigDecimal price, int stockQuantity) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.quantity.set(0); // 0 adet ile başlıyor ürünler!!
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return the product ID
     */
    public int getProductID() {
        return productID;
    }

    /**
     * Returns the name of the product.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Returns the price of the product.
     *
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Returns the stock quantity of the product.
     *
     * @return the stock quantity
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Returns the currently selected quantity for the product.
     *
     * @return the selected quantity
     */
    public int getQuantity() {
        return quantity.get();
    }

    /**
     * Sets the selected quantity for the product.
     *
     * @param q the quantity to set
     */
    public void setQuantity(int q) {
        this.quantity.set(q);
    }

    /**
     * Returns the {@link IntegerProperty} representing the quantity property.
     * This is useful for binding in JavaFX applications.
     *
     * @return the quantity property
     */
    public IntegerProperty quantityProperty() {
        return quantity;
    }
}
