package com.example.cinemaapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessageLabel;


    @FXML
    private Label messageLabel;

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Username or password cannot be empty.");
            return;
        }

        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("Role");
                loginMessageLabel.setText("Login successful.");
                loadDashboard(role);
            } else {
                loginMessageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error occurred during login. Please try again.");
        }
    }

    private void loadDashboard(String role) {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader;

            if ("admin".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/cinemaapp/AdminDashboard.fxml"));
            } else if ("cashier".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/cinemaapp/CashierDashboard.fxml"));
            } else {
                loginMessageLabel.setText("Unknown role. Cannot load dashboard.");
                return;
            }

            Scene scene = new Scene(loader.load());
            currentStage.setScene(scene);
            currentStage.setTitle("Cinema App - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load dashboard.");
        }
    }
}
