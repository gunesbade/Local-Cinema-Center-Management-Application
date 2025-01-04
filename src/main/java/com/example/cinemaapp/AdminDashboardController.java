package com.example.cinemaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Label usernameLabel;

    public void setUsername(String username) {
        usernameLabel.setText("Welcome, " + username);
    }
}
