package com.example.cinemaapp;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.util.Set;

public class OrderConfirmationController {

    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label seatNumbersLabel;
    @FXML
    private Label productListLabel;
    @FXML
    private Label ticketTotalLabel;
    @FXML
    private Label productTotalLabel;
    @FXML
    private Label grandTotalLabel;
    @FXML
    private void handleConfirmOrder() {
        System.out.println("Order confirmed!");
        // İşlem başarılı mesajı ve ana sayfaya yönlendirme işlemleri
    }


    /**
     * Sipariş özetini yükler ve UI üzerinde gösterir.
     *
     * @param movie       Seçilen film bilgisi
     * @param seats       Seçilen koltuklar
     * @param products    Seçilen ürünler
     * @param ticketTotal Biletlerin toplam fiyatı
     * @param productTotal Ürünlerin toplam fiyatı
     * @param grandTotal  Genel toplam fiyat
     */
    public void loadOrderSummary(Movie movie, Set<String> seats, ObservableList<Product> products,
                                 BigDecimal ticketTotal, BigDecimal productTotal, BigDecimal grandTotal) {
        // Film bilgisi
        movieTitleLabel.setText("Movie: " + movie.getTitle());

        // Koltuk bilgileri
        seatNumbersLabel.setText("Seats: " + String.join(", ", seats));

        // Ürün bilgileri
        StringBuilder productDetails = new StringBuilder();
        for (Product product : products) {
            BigDecimal lineTotal = product.getPrice()
                    .multiply(new BigDecimal(product.getQuantity()))
                    .multiply(new BigDecimal("1.10")); // %10 KDV
            productDetails.append(product.getProductName())
                    .append(" x").append(product.getQuantity())
                    .append(" (Total: $").append(lineTotal).append(")\n");
        }
        productListLabel.setText(productDetails.toString());

        // Toplam fiyat bilgileri
        ticketTotalLabel.setText("Ticket Total: $" + ticketTotal);
        productTotalLabel.setText("Product Total: $" + productTotal);
        grandTotalLabel.setText("Grand Total: $" + grandTotal);
    }






}
