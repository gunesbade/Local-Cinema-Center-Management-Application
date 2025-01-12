package com.example.cinemaapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Cinema App application.
 * Extends the {@link Application} class from JavaFX and initializes the main stage.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * Loads the initial FXML layout and sets up the primary stage.
     *
     * @param primaryStage the main stage for the JavaFX application
     */
    @Override
    public void start(Stage primaryStage) {
        try {

            // Load the FXML file for the login screen
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/cinemaapp/Login.fxml"));

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(fxmlLoader.load());

            // Set the title and scene for the primary stage
            primaryStage.setTitle("Cinema App");
            primaryStage.setScene(scene);
            primaryStage.show();   // Display the primary stage
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging in case of an error
        }
    }

    /**
     * The main method serves as the entry point of the application.
     * Calls {@link #launch(String...)} to start the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);   // Launch the JavaFX application
    }
}