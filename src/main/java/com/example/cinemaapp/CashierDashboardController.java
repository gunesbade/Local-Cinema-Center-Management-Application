package com.example.cinemaapp;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;








import javafx.scene.Node;
import javafx.scene.layout.VBox;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class CashierDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private Label selectedMovieTitleLabel;

    @FXML
    private Label selectedMovieGenreLabel;

    @FXML
    private Label selectedMovieSummaryLabel;

    @FXML
    private ImageView selectedMoviePoster;

    @FXML
    private Label sessionMessageLabel;



    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private ComboBox<String> sessionComboBox;

    @FXML
    private ComboBox<String> hallComboBox;


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

    private int selectedSessionID; // Seçilen seansın ID'sini tutar

    private Map<String, CustomerInfo> customerInfoMap = new HashMap<>();






    @FXML
    private Label messageLabel;

    @FXML
    private TabPane tabPane;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie;
    private final List<CustomerInfo> customerInfoList = new ArrayList<>();

    @FXML
    public void initialize() {
        // TableView sütunlarını bağlama
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        summaryColumn.setCellValueFactory(data -> data.getValue().summaryProperty());
        posterColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPoster()));
        productNameColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductName()));
        productPriceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));
        productStockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStockQuantity()));


        movieTable.setItems(movieList);

        // Movie selection listener
        movieTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleMovieSelection(newValue);
            }
        });

        // TabPane dinleyicisi ekleyin
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                String tabText = newTab.getText();
                System.out.println("Current tab: " + tabText);

                if (tabText.equals("Seat Selection")) {
                    System.out.println("Loading seats for the selected session...");
                    loadSeats();
                }
            }
        });
    }


    private void searchMovies(String queryType, String value) {
        movieList.clear();

        String query = "SELECT MovieID, Title, Genre, Summary, Poster FROM Movies WHERE " + queryType + " LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + value + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("MovieID");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String summary = resultSet.getString("Summary");
                String posterPath = resultSet.getString("Poster");

                ImageView posterImageView = new ImageView("file:" + posterPath);
                posterImageView.setFitHeight(100);
                posterImageView.setFitWidth(100);

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
    private void loadSessionDetails() {
        if (selectedMovie == null) {
            sessionMessageLabel.setText("Please select a movie first.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT s.SessionDate, s.StartTime, h.HallName " +
                    "FROM Sessions s " +
                    "JOIN Halls h ON s.HallID = h.HallID " +
                    "WHERE s.MovieID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedMovie.getId());
            ResultSet resultSet = statement.executeQuery();

            // ComboBox'ları temizle
            clearComboBoxes();

            ObservableList<String> days = FXCollections.observableArrayList();
            ObservableList<String> sessions = FXCollections.observableArrayList();
            ObservableList<String> halls = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String day = resultSet.getString("SessionDate");
                String time = resultSet.getString("StartTime");
                String hall = resultSet.getString("HallName");

                if (!days.contains(day)) {
                    days.add(day);
                }
                if (!sessions.contains(time)) {
                    sessions.add(time);
                }
                if (!halls.contains(hall)) {
                    halls.add(hall);
                }
            }

            // ComboBox'lara verileri ekle
            dayComboBox.setItems(days);
            sessionComboBox.setItems(sessions);
            hallComboBox.setItems(halls);

            // Mesajı ayarla
            if (days.isEmpty() && sessions.isEmpty() && halls.isEmpty()) {
                sessionMessageLabel.setText("No available sessions for the selected movie.");
            } else {
                sessionMessageLabel.setText("Session details loaded successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error occurred while loading session details.");
        }
    }


    @FXML
    private void backToSessionSelection() {
        // TabPane'de bir önceki sekmeye geçiş yap
        tabPane.getSelectionModel().selectPrevious();
        System.out.println("Navigated back to session selection.");
    }

    private void clearComboBoxes() {
        dayComboBox.getItems().clear();
        sessionComboBox.getItems().clear();
        hallComboBox.getItems().clear();
    }

    @FXML
    private void confirmSessionSelection() {
        String selectedDay = dayComboBox.getValue();
        String selectedSession = sessionComboBox.getValue();
        String selectedHall = hallComboBox.getValue();

        if (selectedDay == null || selectedSession == null || selectedHall == null) {
            sessionMessageLabel.setText("Please select day, session, and hall.");
            return;
        }

        // Seçilen seans ID'sini belirleyin
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT SessionID FROM Sessions WHERE SessionDate = ? AND StartTime = ? AND HallID = (SELECT HallID FROM Halls WHERE HallName = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, selectedDay);
            statement.setString(2, selectedSession);
            statement.setString(3, selectedHall);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                selectedSessionID = resultSet.getInt("SessionID");
                System.out.println("Session ID set: " + selectedSessionID);
            } else {
                sessionMessageLabel.setText("No matching session found.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error occurred while confirming session.");
            return;
        }

        System.out.println("Session confirmed: Day: " + selectedDay + ", Session: " + selectedSession + ", Hall: " + selectedHall);
        tabPane.getSelectionModel().selectNext();
    }


    @FXML
    private void handleSearchByGenre(ActionEvent event) {
        String genre = searchField.getText().trim();
        if (genre.isEmpty()) {
            searchMessageLabel.setText("Please enter a genre.");
            return;
        }
        searchMovies("Genre", genre);
    }

    @FXML
    private void handleSearchByPartialName(ActionEvent event) {
        String partialName = searchField.getText().trim();
        if (partialName.isEmpty()) {
            searchMessageLabel.setText("Please enter a search term.");
            return;
        }
        searchMovies("Title", partialName);
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
        System.exit(0);
    }

    private void handleMovieSelection(Movie movie) {
        selectedMovie = movie;

        selectedMovieTitleLabel.setText("Title: " + movie.getTitle());
        selectedMovieGenreLabel.setText("Genre: " + movie.getGenre());
        selectedMovieSummaryLabel.setText("Summary: " + movie.getSummary());
        selectedMoviePoster.setImage(movie.getPoster().getImage());

        sessionMessageLabel.setText("Movie selected: " + movie.getTitle());
    }


    @FXML
    private void handleNextToSeatSelection(ActionEvent event) {
        System.out.println("Next button clicked!");
        // Tab değiştirme işlemi gibi işlemleri buraya ekleyebilirsiniz
        tabPane.getSelectionModel().selectNext();
    }

    private int customerId; // Müşterinin ID'sini saklamak için

    @FXML
    private GridPane seatGrid;

    @FXML
    private Label cartMessageLabel;

    private final Set<String> selectedSeats = new HashSet<>();


    private final Map<Integer, Set<String>> sessionSelectedSeats = new HashMap<>();


    @FXML
    private Label cartTotalLabel;


    private void loadSeats() {
        if (dayComboBox.getValue() == null || sessionComboBox.getValue() == null || hallComboBox.getValue() == null) {
            cartMessageLabel.setText("Please select day, session, and hall first.");
            return;
        }

        seatGrid.getChildren().clear();
        int rows = hallComboBox.getValue().equals("Hall_A") ? 4 : 6;
        int cols = hallComboBox.getValue().equals("Hall_A") ? 4 : 8;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT SeatNumber FROM Tickets WHERE SessionID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedSessionID); // Seçilen seans ID'sini kullanın
            ResultSet resultSet = statement.executeQuery();

            Set<String> reservedSeats = new HashSet<>();
            while (resultSet.next()) {
                reservedSeats.add(resultSet.getString("SeatNumber"));
            }

            // Önceki seçimleri geri yükle
            Set<String> previousSelectedSeats = sessionSelectedSeats.getOrDefault(selectedSessionID, new HashSet<>());

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    String seatNumber = (row + 1) + "-" + (col + 1);
                    Button seatButton = new Button(seatNumber);
                    seatButton.setMinSize(40, 40);

                    if (reservedSeats.contains(seatNumber)) {
                        seatButton.setDisable(true);
                        seatButton.setStyle("-fx-background-color: red;");
                    } else if (previousSelectedSeats.contains(seatNumber)) {
                        seatButton.setStyle("-fx-background-color: yellow;");
                        seatButton.setOnAction(e -> handleSeatSelection(seatNumber, seatButton));
                    } else {
                        seatButton.setStyle("-fx-background-color: green;");
                        seatButton.setOnAction(e -> handleSeatSelection(seatNumber, seatButton));
                    }

                    seatGrid.add(seatButton, col, row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            cartMessageLabel.setText("Error occurred while loading seats.");
        }
    }




    private void handleSeatSelection(String seatNumber, Button seatButton) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            seatButton.setStyle("-fx-background-color: green;");
        } else {
            selectedSeats.add(seatNumber);
            seatButton.setStyle("-fx-background-color: yellow;");
        }
        cartMessageLabel.setText("Selected seats: " + selectedSeats);
    }




    private BigDecimal fetchBasePriceFromDatabase(int movieId) {
        BigDecimal basePrice = BigDecimal.ZERO; // Varsayılan fiyat

        String query = "SELECT BasePrice FROM Movies WHERE MovieID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, movieId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                basePrice = resultSet.getBigDecimal("BasePrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return basePrice;
    }


    @FXML
    private void addToCart() {
        if (selectedSeats.isEmpty()) {
            cartMessageLabel.setText("Lütfen en az bir koltuk seçin.");
            return;
        }

        // Seçili filmin fiyatını veritabanından alın
        BigDecimal basePrice = fetchBasePriceFromDatabase(selectedMovie.getId());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Tickets (SessionID, SeatNumber, CustomerName, BasePrice) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            for (String seat : selectedSeats) {
                statement.setInt(1, selectedSessionID);
                statement.setString(2, seat);
                statement.setString(3, "Default Name"); // Varsayılan müşteri adı
                statement.setBigDecimal(4, basePrice);  // BasePrice alanına değer ekleniyor
                statement.addBatch();
            }

            statement.executeBatch();
            cartMessageLabel.setText("Koltuklar sepete eklendi!");
            tabPane.getSelectionModel().selectNext(); // Diğer sekmeye geçiş

        } catch (SQLException e) {
            e.printStackTrace();
            cartMessageLabel.setText("Koltuklar eklenirken bir hata oluştu.");
        }
    }







    @FXML
    private TextField customerFirstNameField;

    @FXML
    private TextField customerLastNameField;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Integer> productStockColumn;


    @FXML
    private TableColumn<Product, BigDecimal> productPriceColumn;





    @FXML
    private Label discountMessageLabel;


    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ProductID, ProductName, Price, StockQuantity FROM Products";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ProductID");
                String name = resultSet.getString("ProductName");
                BigDecimal price = resultSet.getBigDecimal("Price");
                int stock = resultSet.getInt("StockQuantity");
                products.add(new Product(id, name, price, stock));
            }

            productTable.setItems(products);
        } catch (SQLException e) {
            e.printStackTrace();
            discountMessageLabel.setText("Error loading products.");
        }
    }
    private ObservableList<Product> selectedProducts = FXCollections.observableArrayList();

    @FXML
    private void addSelectedProductsToCart() {
        selectedProducts = productTable.getSelectionModel().getSelectedItems();
        BigDecimal totalExtrasPrice = BigDecimal.ZERO;

        for (Product product : selectedProducts) {
            totalExtrasPrice = totalExtrasPrice.add(product.getPrice());
            System.out.println("Added product: " + product.getProductName() + " - Price: $" + product.getPrice());
        }

        cartTotalLabel.setText("Total Price (Including Extras): $" + totalExtrasPrice);
    }



    private BigDecimal calculateDiscount(String birthDate) {
        int age = calculateAge(birthDate);
        if (age < 18 || age > 60) {
            return new BigDecimal("0.50"); // %50 indirim
        }
        return new BigDecimal("0.00"); // İndirim yok
    }

    private int calculateAge(String birthDate) {
        LocalDate dob = LocalDate.parse(birthDate);
        LocalDate now = LocalDate.now();
        return Period.between(dob, now).getYears();
    }


    @FXML
    private void proceedToFinalStage() {
        String firstName = customerFirstNameField.getText().trim();
        String lastName = customerLastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            discountMessageLabel.setText("Please enter customer details.");
            return;
        }

        // Tüm bilgiler tamamlandıysa bir sonraki sekmeye geç
        tabPane.getSelectionModel().selectNext();
        System.out.println("Proceeding to the final stage...");
    }
    @FXML
    private void backToSeatSelection() {
        tabPane.getSelectionModel().selectPrevious();
        System.out.println("Navigated back to seat selection.");
    }



    @FXML
    private void saveTicketsToDatabase() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Tickets (SessionID, CustomerName, SeatNumber, BasePrice, DiscountRate, FinalPrice) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            BigDecimal basePrice = new BigDecimal("50.00"); // Örnek baz fiyat
            BigDecimal discountRate = new BigDecimal("0.20"); // Örnek indirim oranı
            BigDecimal finalPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate)); // Nihai fiyat

            int i = 0; // Koltuk numarası için sayaç
            for (CustomerInfo info : customerInfoList) {
                String seatNumber = new ArrayList<>(selectedSeats).get(i); // Seçilen koltuk numaralarını sırayla al

                statement.setInt(1, selectedSessionID);
                statement.setString(2, info.getName());
                statement.setString(3, seatNumber);
                statement.setBigDecimal(4, basePrice);
                statement.setBigDecimal(5, discountRate);
                statement.setBigDecimal(6, finalPrice);
                statement.addBatch();
                i++;
            }

            statement.executeBatch();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Tickets successfully saved!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error occurred while saving tickets.");
            alert.showAndWait();
        }
    }
    private BigDecimal basePrice;


    @FXML
    private void finalizeReservation() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Tickets (SessionID, CustomerName, SeatNumber, BasePrice, DiscountRate, FinalPrice) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            for (String seat : selectedSeats) {
                CustomerInfo info = customerInfoMap.get(seat);
                BigDecimal discountRate = calculateDiscount(info.getBirthDate());
                BigDecimal finalPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));

                statement.setInt(1, selectedSessionID);
                statement.setString(2, info.getName());
                statement.setString(3, seat);
                statement.setBigDecimal(4, basePrice);
                statement.setBigDecimal(5, discountRate);
                statement.setBigDecimal(6, finalPrice);
                statement.addBatch();
            }

            statement.executeBatch();
            cartMessageLabel.setText("Rezervasyon tamamlandı!");
        } catch (SQLException e) {
            e.printStackTrace();
            cartMessageLabel.setText("Rezervasyon sırasında hata oluştu.");
        }
    }

    @FXML
    private VBox customerInfoContainer; // FXML dosyasındaki container'ı bağlayın




    @FXML
    private void loadCustomerInfoForm() {
        customerInfoContainer.getChildren().clear(); // Önceki bilgileri temizle
        customerInfoMap.clear(); // Önceki bilgileri temizle

        for (String seat : selectedSeats) {
            Label nameLabel = new Label("Koltuk: " + seat + " için izleyici ismini girin:");
            TextField nameField = new TextField();
            nameField.setPromptText("İsim");

            Label ageLabel = new Label("Koltuk: " + seat + " için yaşını girin:");
            TextField ageField = new TextField();
            ageField.setPromptText("Yaş");

            VBox customerForm = new VBox(10, nameLabel, nameField, ageLabel, ageField);
            customerInfoContainer.getChildren().add(customerForm);

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfoMap.put(seat, customerInfo);

            // Dinamik veri bağlantısı
            nameField.textProperty().addListener((observable, oldValue, newValue) -> customerInfo.setName(newValue));
            ageField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    customerInfo.setAge(Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    ageField.setStyle("-fx-border-color: red;");
                }
            });
        }
    }


    // Doğum tarihi formatını doğrulamak için bir metot
    private boolean validateDate(String date) {
        try {
            LocalDate.parse(date); // YYYY-MM-DD formatında bir tarih bekleniyor
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }



    @FXML
    private void calculateTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Koltuk fiyatlarını ekle
        for (String seat : selectedSeats) {
            CustomerInfo info = customerInfoMap.get(seat);
            BigDecimal basePrice = fetchBasePriceFromDatabase(selectedMovie.getId());
            BigDecimal discount = basePrice.multiply(calculateDiscount(info.getBirthDate()));
            BigDecimal finalPrice = basePrice.subtract(discount);
            totalPrice = totalPrice.add(finalPrice);
        }

        // Ekstra ürün fiyatlarını ekle
        for (Product product : selectedProducts) {
            totalPrice = totalPrice.add(product.getPrice());
        }

        cartTotalLabel.setText("Toplam Fiyat: " + totalPrice + " TL");
    }




    @FXML
    private void proceedToCustomerInfoForm() {
        tabPane.getSelectionModel().selectNext(); // Sonraki sekmeye geç
        loadCustomerInfoForm(); // Müşteri bilgisi formunu yükle

    }




}