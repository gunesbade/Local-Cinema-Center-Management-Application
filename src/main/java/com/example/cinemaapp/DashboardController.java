package com.example.cinemaapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label userInfoLabel; // Kullanıcı bilgilerini göstermek için

    public void setUserInfo(String username, String role) {
        if (userInfoLabel != null) {
            userInfoLabel.setText("Logged in as: " + username + " (" + role + ")");
        }
    }
}
