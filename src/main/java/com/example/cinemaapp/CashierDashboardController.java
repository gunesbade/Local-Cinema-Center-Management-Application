package com.example.cinemaapp;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashierDashboardController {

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
    private Label searchMessageLabel;

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private ComboBox<String> sessionComboBox;

    @FXML
    private ComboBox<String> hallComboBox;

    @FXML
    private Label sessionMessageLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TabPane tabPane; // FXML ile bağlayın

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie;
    private SessionDetails selectedSessionDetails;


    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        summaryColumn.setCellValueFactory(data -> data.getValue().summaryProperty());
        posterColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPoster()));

        movieTable.setItems(movieList);

        // Tıklama olayını ekleyin
        movieTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleMovieSelection(newValue);
            }
        });
    }


    @FXML
    private void handleSearchByGenre(ActionEvent event) {
        String genre = searchField.getText().trim();
        if (genre.isEmpty()) {
            searchMessageLabel.setText("Please enter a genre.");
            return;
        }
        searchMovies("Genre", "%" + genre + "%");
    }
    @FXML
    private void handleSearchByPartialName(ActionEvent event) {
        String partialName = searchField.getText().trim();
        if (partialName.isEmpty()) {
            searchMessageLabel.setText("Please enter a partial name.");
            return;
        }
        searchMovies("Title", "%" + partialName + "%");
    }

    private void searchMovies(String column, String value) {
        movieList.clear();

        String query = "SELECT MovieID, Title, Genre, Summary, Poster FROM Movies WHERE " + column + " LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, value);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // MovieID'yi int olarak alın
                int id = resultSet.getInt("MovieID");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String summary = resultSet.getString("Summary");
                String posterPath = resultSet.getString("Poster");

                // Poster için ImageView oluşturma
                ImageView posterImageView = new ImageView("file:" + posterPath);
                posterImageView.setFitHeight(100);
                posterImageView.setFitWidth(100);

                // Movie nesnesini tabloya ekleyin
                movieList.add(new Movie(id, title, genre, summary, posterImageView));
            }

            if (movieList.isEmpty()) {
                searchMessageLabel.setText("No movies found.");
            } else {
                searchMessageLabel.setText("Movies loaded successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            searchMessageLabel.setText("Error occurred while loading movies.");
        }
    }


    @FXML
    private void handleSearchByFullName(ActionEvent event) {
        String fullName = searchField.getText().trim();
        if (fullName.isEmpty()) {
            searchMessageLabel.setText("Please enter a full name.");
            return;
        }
        searchMovies("Title", fullName);
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("User logged out.");
        messageLabel.setText("Logout function not implemented yet.");
    }


    @FXML
    private Label selectedMovieTitleLabel;

    @FXML
    private Label selectedMovieGenreLabel;

    @FXML
    private Label selectedMovieSummaryLabel;
    @FXML
    private ImageView selectedMoviePoster;






    private void handleMovieSelection(Movie movie) {
        selectedMovie = movie; // Seçilen filmi kaydet

        // Bilgileri güncelle
        selectedMovieTitleLabel.setText("Title: " + movie.getTitle());
        selectedMovieGenreLabel.setText("Genre: " + movie.getGenre());
        selectedMovieSummaryLabel.setText("Summary: " + movie.getSummary());
        selectedMoviePoster.setImage(movie.getPoster().getImage());
    }

    @FXML

    private void loadSessionDetails() {
        if (selectedMovie == null) {
            sessionMessageLabel.setText("Please select a movie first.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL sorgusu SessionDate ve StartTime'ı çekiyor
            String query = "SELECT SessionDate, StartTime, HallID FROM Sessions WHERE MovieID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedMovie.getId());

            ResultSet resultSet = statement.executeQuery();

            ObservableList<String> days = FXCollections.observableArrayList();
            ObservableList<String> times = FXCollections.observableArrayList();
            ObservableList<String> halls = FXCollections.observableArrayList();

            while (resultSet.next()) {
                days.add(resultSet.getDate("SessionDate").toString());
                times.add(resultSet.getTime("StartTime").toString());
                halls.add("Hall " + resultSet.getInt("HallID"));
            }

            dayComboBox.setItems(days);
            sessionComboBox.setItems(times);
            hallComboBox.setItems(halls);

            sessionMessageLabel.setText("Session details loaded successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error occurred while loading session details.");
        }
    }


    @FXML
    private void handleNextToSeatSelection(ActionEvent event) {
        String selectedDay = dayComboBox.getValue();
        String selectedSession = sessionComboBox.getValue();
        String selectedHall = hallComboBox.getValue();

        if (selectedDay == null || selectedSession == null || selectedHall == null) {
            sessionMessageLabel.setText("Please select day, session, and hall.");
            return;
        }

        selectedSessionDetails = new SessionDetails(selectedDay, selectedSession, selectedHall);
        sessionMessageLabel.setText("Session selected. Proceeding to seat selection...");
        tabPane.getSelectionModel().selectNext();
    }
}
