package com.example.cinemaapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchMovieController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private TableColumn<Movie, String> titleColumn;

    @FXML
    private TableColumn<Movie, String> genreColumn;

    @FXML
    private TableColumn<Movie, String> summaryColumn;

    @FXML
    private TableColumn<Movie, ImageView> posterColumn;

    @FXML
    private Label messageLabel;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        summaryColumn.setCellValueFactory(data -> data.getValue().summaryProperty());
    }


    @FXML
    private void handleSearchByGenre(ActionEvent event) {
        String genre = searchField.getText().trim();
        if (genre.isEmpty()) {
            messageLabel.setText("Please enter a genre.");
            return;
        }
        searchMovies("Genre", genre);
    }

    @FXML
    private void handleSearchByPartialName(ActionEvent event) {
        String partialName = searchField.getText().trim();
        if (partialName.isEmpty()) {
            messageLabel.setText("Please enter a partial title.");
            return;
        }
        searchMovies("Title", "%" + partialName + "%");
    }

    @FXML
    private void handleSearchByFullName(ActionEvent event) {
        String fullName = searchField.getText().trim();
        if (fullName.isEmpty()) {
            messageLabel.setText("Please enter a full title.");
            return;
        }
        searchMovies("Title", fullName);
    }

    private void searchMovies(String column, String value) {
        movieList.clear();

        String query = "SELECT Title, Genre, Summary, Poster FROM Movies WHERE " + column + " LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String summary = resultSet.getString("Summary");
                String posterPath = resultSet.getString("Poster");

                // Poster için ImageView oluştur
                ImageView poster = null;
                if (posterPath != null && !posterPath.isEmpty()) {
                    poster = new ImageView(new javafx.scene.image.Image("file:" + posterPath));
                    poster.setFitWidth(100); // Görüntü genişliği
                    poster.setFitHeight(150); // Görüntü yüksekliği
                }

                // Movie nesnesini oluştur ve listeye ekle
                int id = resultSet.getInt("MovieID"); // MovieID sütununu alın
                movieList.add(new Movie(id, title, genre, summary, poster));
            }

                if (movieList.isEmpty()) {
                messageLabel.setText("No movies found.");
            } else {
                messageLabel.setText("Movies loaded successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading movies.");
        }
    }


}
