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

/**
 * Controller class for handling the login functionality.
 * Validates user credentials and loads the appropriate dashboard based on the user's role.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessageLabel;


    @FXML
    private Label messageLabel;

    /**
     * Handles the login action when the login button is clicked.
     * Validates the username and password against the database and loads the appropriate dashboard.
     *
     * @param event the action event triggered by the login button
     */
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Check if the username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Username or password cannot be empty.");
            return;
        }

        // SQL query to validate user credentials
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Get the user's role from the database
                String role = resultSet.getString("Role");
                loginMessageLabel.setText("Login successful.");
                loadDashboard(role); // Load the appropriate dashboard based on the role
            } else {
                loginMessageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            loginMessageLabel.setText("Error occurred during login. Please try again.");
        }
    }

    /**
     * Loads the appropriate dashboard based on the user's role.
     *
     * @param role the role of the user (e.g., "admin", "cashier")
     */
    private void loadDashboard(String role) {
        try {
            // Get the current stage from the username field's scene
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader;

            // Load the FXML file based on the user's role
            if ("admin".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/cinemaapp/AdminDashboard.fxml"));
            } else if ("cashier".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/cinemaapp/CashierDashboard.fxml"));
            } else {
                loginMessageLabel.setText("Unknown role. Cannot load dashboard.");
                return;
            }

            // Set the new scene and update the stage title
            Scene scene = new Scene(loader.load());
            currentStage.setScene(scene);
            currentStage.setTitle("Cinema App - Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Failed to load dashboard.");
        }
    }
}
