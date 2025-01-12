package com.example.cinemaapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing the database connection for the Cinema App.
 * Provides a method to establish a connection to the MySQL database.
 */
public class DatabaseConnection {

    // Database URL, username, and password for connecting to the MySQL database
    private static final String URL = "jdbc:mysql://localhost:3306/CinemaApp"; // Database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "Iremnaz.1"; // MySQL password

    /**
     * Establishes a connection to the database using the configured URL, username, and password.
     *
     * @return a {@link Connection} object representing the database connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}