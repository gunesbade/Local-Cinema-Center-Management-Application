package com.example.cinemaapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/CinemaApp"; // Database URL
    private static final String USER = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = "Iremnaz.1"; // MySQL şifreniz

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
