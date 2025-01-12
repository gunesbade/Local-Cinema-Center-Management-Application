package com.example.cinemaapp;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Controller class for managing the order confirmation screen.
 * Displays the selected movie, seats, products, and calculates totals for the order.
 */
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

    /**
     * Handles the order confirmation action.
     * Prints a confirmation message to the console and can be extended for further actions,
     * such as redirecting the user to the home screen.
     */
    @FXML
    private void handleConfirmOrder() {
        System.out.println("Order confirmed!");
        // Display success message and redirect to the home screen
    }


    /**
     * Loads the order summary details into the UI components.
     *
     * @param movie        The selected movie information.
     * @param seats        The set of selected seat numbers.
     * @param products     The list of selected products.
     * @param ticketTotal  The total cost of the tickets.
     * @param productTotal The total cost of the products.
     * @param grandTotal   The overall total cost (tickets + products).
     */
    public void loadOrderSummary(Movie movie, Set<String> seats, ObservableList<Product> products,
                                 BigDecimal ticketTotal, BigDecimal productTotal, BigDecimal grandTotal) {
        // Display movie information
        movieTitleLabel.setText("Movie: " + movie.getTitle());

        // Display seat information
        seatNumbersLabel.setText("Seats: " + String.join(", ", seats));

        // Display product information
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

        // Display total prices
        ticketTotalLabel.setText("Ticket Total: $" + ticketTotal);
        productTotalLabel.setText("Product Total: $" + productTotal);
        grandTotalLabel.setText("Grand Total: $" + grandTotal);
    }






}
