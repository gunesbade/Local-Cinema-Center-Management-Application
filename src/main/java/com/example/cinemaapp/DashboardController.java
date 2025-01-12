package com.example.cinemaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for managing the dashboard screen.
 * Displays user information such as username and role.
 */
public class DashboardController {

    @FXML
    private Label userInfoLabel; // Label to display user information

    /**
     * Sets the user information in the dashboard.
     * Updates the {@link Label} with the logged-in user's username and role.
     *
     * @param username the username of the logged-in user
     * @param role     the role of the logged-in user (e.g., "admin", "cashier")
     */
    public void setUserInfo(String username, String role) {
        if (userInfoLabel != null) {
            // Update the label with the user information
            userInfoLabel.setText("Logged in as: " + username + " (" + role + ")");
        }
    }
}