package com.example.cinemaapp;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Controller class for the Cashier Dashboard in the Cinema App.
 * Handles functionality such as movie search, session selection, seat
 * selection,
 * additional product selection, and order confirmation.
 */
public class CashierDashboardController {

    @FXML
    private TextField searchField; // Field for searching movies by title or genre
    @FXML
    private TableView<Movie> movieTable; // Table displaying movies
    @FXML
    private TableColumn<Movie, String> titleColumn; // Column for movie titles
    @FXML
    private TableColumn<Movie, String> genreColumn; // Column for movie genres
    @FXML
    private TableColumn<Movie, String> summaryColumn; // Column for movie summaries
    @FXML
    private TableColumn<Movie, ImageView> posterColumn; // Column for movie posters

    @FXML
    private Label selectedMovieTitleLabel; // Displays the selected movie title
    @FXML
    private Label selectedMovieGenreLabel; // Displays the selected movie genre
    @FXML
    private Label selectedMovieSummaryLabel; // Displays the selected movie summary
    @FXML
    private ImageView selectedMoviePoster; // Displays the selected movie poster

    @FXML
    private Label sessionMessageLabel; // Message label for session-related notifications
    @FXML
    private Label searchMessageLabel; // Label for search-related notifications
    @FXML
    private ComboBox<String> dayComboBox; // ComboBox for session days
    @FXML
    private ComboBox<String> sessionComboBox; // ComboBox for session times
    @FXML
    private ComboBox<String> hallComboBox; // ComboBox for halls
    @FXML
    private Label messageLabel; // General message label for notifications
    @FXML
    private TabPane tabPane; // TabPane for navigating between different sections

    // Seat Selection
    @FXML
    private GridPane seatGrid; // GridPane for displaying seat layout
    @FXML
    private Label cartMessageLabel; // Message label for cart notifications
    private final Set<String> selectedSeats = new HashSet<>(); // Set of selected seat numbers
    private final Map<Integer, Set<String>> sessionSelectedSeats = new HashMap<>(); // Map storing selected seats by
    // session ID

    // Additional Products
    @FXML
    private TableView<Product> productTable; // Table displaying products
    @FXML
    private TableColumn<Product, Integer> productIDColumn; // Column for product IDs
    @FXML
    private TableColumn<Product, String> productNameColumn; // Column for product names
    @FXML
    private TableColumn<Product, Integer> productStockColumn; // Column for product stock
    @FXML
    private TableColumn<Product, BigDecimal> productPriceColumn; // Column for product prices

    private ObservableList<Product> selectedProducts = FXCollections.observableArrayList();

    @FXML
    private Label discountMessageLabel; // Message label for discount-related notifications
    @FXML
    private TextField customerFirstNameField; // Field for entering the customer's first name
    @FXML
    private TextField customerLastNameField; // Field for entering the customer's last name
    @FXML
    private Label cartTotalLabel; // Label displaying the total cart value

    @FXML
    private VBox customerInfoContainer; // VBox for dynamically displaying customer info forms

    private ObservableList<Movie> movieList = FXCollections.observableArrayList(); // List of movies
    private Movie selectedMovie; // Currently selected movie
    private int selectedSessionID; // ID of the selected session

    private BigDecimal basePrice; // Base price for tickets

    @FXML
    private TableColumn<Product, Integer> productQuantityColumn;

    // Map storing customer info by seat number
    private final Map<String, CustomerInfo> customerInfoMap = new HashMap<>();

    /**
     * Initializes the controller and sets up the TableView columns, listeners, and
     * ComboBox items.
     * Automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Set up columns for the movie table
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        summaryColumn.setCellValueFactory(data -> data.getValue().summaryProperty());
        posterColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPoster()));

        // Populate the movie table with movie data
        movieTable.setItems(movieList);
        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleMovieSelection(newVal);
            }
        });

        // Set up columns for the product table
        productIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductID()));
        productNameColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductName()));
        productPriceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));
        productStockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStockQuantity()));

        // Enable multiple selection for products in the product table
        productTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Set up the product quantity column with a spinner for adjusting quantities
        productQuantityColumn.setCellValueFactory(c -> c.getValue().quantityProperty().asObject());
        productQuantityColumn.setCellFactory(col -> {
            TableCell<Product, Integer> cell = new TableCell<>() {
                private final Spinner<Integer> spinner = new Spinner<>(0, 100, 0);

                @Override
                protected void updateItem(Integer value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        if (value != null) {
                            spinner.getValueFactory().setValue(value);
                        }
                        setGraphic(spinner);
                        // Spinner değişince, Product nesnesine setQuantity
                        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                            Product prod = getTableView().getItems().get(getIndex());
                            prod.setQuantity(newVal);
                        });
                    }
                }
            };
            return cell;
        });

        // TabPane listener
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                String tabText = newTab.getText();
                System.out.println("Current tab: " + tabText);

                if (tabText.equals("Seat Selection")) {
                    System.out.println("Loading seats for the selected session...");
                    loadSeats();
                } else if (tabText.equals("Additional Products and Discounts")) {
                    loadProducts();
                }
            }
        });
    }

    // ========== 1) SEARCH MOVIE ==========
    /**
     * Handles searching for movies by genre.
     * If the search field is empty, displays a warning message.
     *
     * @param event the action event triggered by the search button
     */
    @FXML
    private void handleSearchByGenre(ActionEvent event) {
        String genre = searchField.getText().trim();
        if (genre.isEmpty()) {
            searchMessageLabel.setText("Please enter a genre.");
            return;
        }
        searchMovies("Genre", genre);
    }

    /**
     * Handles searching for movies by partial name.
     * If the search field is empty, displays a warning message.
     *
     * @param event the action event triggered by the search button
     */
    @FXML
    private void handleSearchByPartialName(ActionEvent event) {
        String partialName = searchField.getText().trim();
        if (partialName.isEmpty()) {
            searchMessageLabel.setText("Please enter a search term.");
            return;
        }
        searchMovies("Title", partialName);
    }

    /**
     * Handles searching for movies by full name.
     * If the search field is empty, displays a warning message.
     *
     * @param event the action event triggered by the search button
     */
    @FXML
    private void handleSearchByFullName(ActionEvent event) {
        String fullName = searchField.getText().trim();
        if (fullName.isEmpty()) {
            searchMessageLabel.setText("Please enter a full name.");
            return;
        }
        searchMovies("Title", fullName);
    }

    /**
     * Searches for movies in the database based on the given query type and value.
     * Updates the movie list and displays a success or error message.
     *
     * @param queryType the type of query (e.g., "Genre", "Title")
     * @param value     the value to search for
     */
    private void searchMovies(String queryType, String value) {
        movieList.clear(); // Clear the existing movie list
        String query = "SELECT MovieID, Title, Genre, Summary, Poster FROM Movies WHERE " + queryType + " LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            // Set the search value with wildcard characters for partial matching
            st.setString(1, "%" + value + "%");
            ResultSet rs = st.executeQuery();

            // Populate the movie list with the query results
            while (rs.next()) {
                int id = rs.getInt("MovieID");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                String summary = rs.getString("Summary");
                String posterPath = rs.getString("Poster");

                // Create an ImageView for the movie poster
                ImageView iv = new ImageView("file:" + posterPath);
                iv.setFitHeight(100);
                iv.setFitWidth(100);

                // Add the movie to the observable list
                movieList.add(new Movie(id, title, genre, summary, iv));
            }

            // Update the search message label based on the results
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

    /**
     * Handles the selection of a movie from the movie table.
     * Updates the UI with the selected movie's details and sets the session
     * message.
     *
     * @param movie the selected movie
     */
    private void handleMovieSelection(Movie movie) {
        selectedMovie = movie;
        selectedMovieTitleLabel.setText("Title: " + movie.getTitle());
        selectedMovieGenreLabel.setText("Genre: " + movie.getGenre());
        selectedMovieSummaryLabel.setText("Summary: " + movie.getSummary());
        selectedMoviePoster.setImage(movie.getPoster().getImage());

        sessionMessageLabel.setText("Movie selected: " + movie.getTitle());
    }

    /**
     * Proceeds to the next tab (seat selection) in the TabPane.
     *
     * @param event the action event triggered by the navigation button
     */
    @FXML
    private void handleNextToSeatSelection(ActionEvent event) {
        tabPane.getSelectionModel().selectNext();
    }

    // ========== 2) SEANS SEÇIMI ==========
    /**
     * Loads session details for the selected movie from the database.
     * Populates the ComboBoxes for session day, time, and hall.
     * Displays an appropriate message if no sessions are available.
     */
    @FXML
    private void loadSessionDetails() {
        // Ensure a movie is selected before loading sessions
        if (selectedMovie == null) {
            sessionMessageLabel.setText("Please select a movie first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to retrieve session details
            String query = "SELECT DISTINCT s.SessionDate, s.StartTime, h.HallName "
                    + "FROM Sessions s JOIN Halls h ON s.HallID = h.HallID "
                    + "WHERE s.MovieID = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, selectedMovie.getId());
            ResultSet rs = st.executeQuery();

            // Clear existing ComboBox items
            clearComboBoxes();

            // Prepare lists for ComboBox population
            ObservableList<String> days = FXCollections.observableArrayList();
            ObservableList<String> sessions = FXCollections.observableArrayList();
            ObservableList<String> halls = FXCollections.observableArrayList();

            // Populate lists with query results
            while (rs.next()) {
                String day = rs.getString("SessionDate");
                String time = rs.getString("StartTime");
                String hall = rs.getString("HallName");

                if (!days.contains(day))
                    days.add(day);
                if (!sessions.contains(time))
                    sessions.add(time);
                if (!halls.contains(hall))
                    halls.add(hall);
            }

            // Update ComboBoxes with retrieved data
            dayComboBox.setItems(days);
            sessionComboBox.setItems(sessions);
            hallComboBox.setItems(halls);

            // Display appropriate messages based on session availability
            if (days.isEmpty() && sessions.isEmpty() && halls.isEmpty()) {
                sessionMessageLabel.setText("No available sessions for this movie.");
            } else {
                sessionMessageLabel.setText("Session details loaded successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error loading session details.");
        }
    }

    /**
     * Clears the items in the session ComboBoxes (day, time, and hall).
     */
    private void clearComboBoxes() {
        dayComboBox.getItems().clear();
        sessionComboBox.getItems().clear();
        hallComboBox.getItems().clear();
    }

    /**
     * Confirms the session selection by validating the chosen day, time, and hall.
     * Retrieves the SessionID from the database and prepares for seat selection.
     * Displays an appropriate message based on the outcome.
     */
    @FXML
    private void confirmSessionSelection() {
        String d = dayComboBox.getValue();
        String t = sessionComboBox.getValue();
        String h = hallComboBox.getValue();

        // Validate that all session details are selected
        if (d == null || t == null || h == null) {
            sessionMessageLabel.setText("Please select day, session, and hall.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to find the SessionID based on the selected day, time, and hall
            String sql = "SELECT SessionID FROM Sessions "
                    + "WHERE SessionDate=? AND StartTime=? "
                    + "AND HallID=(SELECT HallID FROM Halls WHERE HallName=?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, d);
            ps.setString(2, t);
            ps.setString(3, h);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Query to find the SessionID based on the selected day, time, and hall
                selectedSessionID = rs.getInt("SessionID");
                sessionMessageLabel.setText("Session confirmed: " + d + " " + t + " " + h);

                selectedSeats.clear(); // Clear previously selected seats
                sessionSelectedSeats.clear();
                cartMessageLabel.setText(""); // Reset cart message

                tabPane.getSelectionModel().selectNext(); // Move to the next tab
            } else {
                sessionMessageLabel.setText("No matching session found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error occurred while confirming session.");
        }
    }

    /**
     * Navigates back to the session selection tab.
     */
    @FXML
    private void backToSessionSelection() {
        tabPane.getSelectionModel().selectPrevious();
    }

    // ========== 3) KOLTUK SEÇIMI ==========

    /**
     * Loads the seat layout for the selected session.
     * Retrieves reserved seats from the database and updates the seat grid.
     * Allows users to select available seats.
     */
    private void loadSeats() {
        System.out.println("loadSeats() called.");

        // Validate that session details are selected
        if (dayComboBox.getValue() == null || sessionComboBox.getValue() == null || hallComboBox.getValue() == null) {
            cartMessageLabel.setText("Please select day, session, and hall first.");
            System.out.println("-> aborted: comboboxes are null.");
            return;
        }
        seatGrid.getChildren().clear(); // Clear the seat grid

        // Determine the number of rows and columns based on the hall name
        String hallName = hallComboBox.getValue();
        int rows = hallName.equals("Hall_A") ? 4 : 6;
        int cols = hallName.equals("Hall_A") ? 4 : 8;
        System.out.println("rows=" + rows + ", cols=" + cols);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to get reserved seats for the selected session
            String query = "SELECT SeatNumber FROM Tickets WHERE SessionID=?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, selectedSessionID);
            ResultSet rs = st.executeQuery();

            Set<String> reservedSeats = new HashSet<>();
            while (rs.next()) {
                reservedSeats.add(rs.getString("SeatNumber")); // Add reserved seat numbers to the set
            }
            System.out.println("reserved seats in DB => " + reservedSeats);

            // Retrieve previously selected seats for the session (if any)
            Set<String> prevSel = sessionSelectedSeats.getOrDefault(selectedSessionID, new HashSet<>());
            System.out.println("prev selected => " + prevSel);

            // Populate the seat grid with buttons representing seats
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    String seatNumber = (r + 1) + "-" + (c + 1);
                    Button seatBtn = new Button(seatNumber);
                    seatBtn.setMinSize(40, 40);

                    if (reservedSeats.contains(seatNumber)) {
                        // Mark reserved seats as red and disabled
                        seatBtn.setDisable(true);
                        seatBtn.setStyle("-fx-background-color: red;");
                    } else if (prevSel.contains(seatNumber)) {
                        // Highlight previously selected seats as yellow
                        seatBtn.setStyle("-fx-background-color: yellow;");
                        seatBtn.setOnAction(e -> handleSeatSelection(seatNumber, seatBtn));
                    } else {
                        // Mark available seats as green
                        seatBtn.setStyle("-fx-background-color: green;");
                        seatBtn.setOnAction(e -> handleSeatSelection(seatNumber, seatBtn));
                    }
                    seatGrid.add(seatBtn, c, r); // Add the seat button to the grid
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            cartMessageLabel.setText("Error loading seats.");
        }
    }

    /**
     * Handles the selection or deselection of a seat.
     * Updates the seat's visual appearance and the list of selected seats.
     *
     * @param seatNumber the identifier for the selected seat (e.g., "1-1")
     * @param seatBtn    the button representing the seat
     */
    private void handleSeatSelection(String seatNumber, Button seatBtn) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);// If the seat is already selected, deselect it
            seatBtn.setStyle("-fx-background-color: green;");
        } else {
            selectedSeats.add(seatNumber); // If the seat is not selected, select it
            seatBtn.setStyle("-fx-background-color: yellow;");
        }
        // Update the cart message to display selected seats
        cartMessageLabel.setText("Selected seats: " + selectedSeats);
    }

    /**
     * Fetches the base price of a movie from the database based on its ID.
     *
     * @param movieId the ID of the selected movie
     * @return the base price as a {@link BigDecimal}, or {@code BigDecimal.ZERO} if
     *         not found
     */
    private BigDecimal fetchBasePriceFromDatabase(int movieId) {
        BigDecimal base = BigDecimal.ZERO; // Default to 0 if no base price is found
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT BasePrice FROM Movies WHERE MovieID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                base = rs.getBigDecimal("BasePrice"); // Retrieve the base price from the result set
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return base;
    }

    /**
     * Adds the selected seats to the cart.
     * If no seats are selected, displays a warning message.
     * Updates the session's selected seats and proceeds to the next tab.
     */
    @FXML
    private void addToCart() {
        if (selectedSeats.isEmpty()) {
            cartMessageLabel.setText("Please select at least one seat.");
            return;
        }
        basePrice = fetchBasePriceFromDatabase(selectedMovie.getId()); // Fetch the base price of the selected movie
        sessionSelectedSeats.put(selectedSessionID, new HashSet<>(selectedSeats)); // Store the selected seats for the
        // current session
        cartMessageLabel.setText("Seats added to cart (not in DB)."); // Display success message and move to the next
        // tab
        tabPane.getSelectionModel().selectNext();
    }

    // ========== 4) URUNLER + MUSTERI BILGISI ==========
    /**
     * Loads the list of available products from the database and populates the
     * product table.
     * Displays an error message if the products cannot be loaded.
     */
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList(); // List to hold product data

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT ProductID, ProductName, Price, StockQuantity FROM Products";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Populate the list with product data from the result set
            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String productName = rs.getString("ProductName");
                BigDecimal price = rs.getBigDecimal("Price");
                int stockQuantity = rs.getInt("StockQuantity");

                // Create a Product object and add it to the list
                Product product = new Product(productId, productName, price, stockQuantity);
                products.add(product);
            }

            // Update the product table with the loaded data
            productTable.setItems(products);
            productTable.refresh();
            System.out.println("Products loaded successfully: " + products.size());
        } catch (SQLException e) {
            e.printStackTrace();
            discountMessageLabel.setText("Error loading products.");
        }
    }

    /**
     * Adds the selected products from the product table to the cart.
     * Calculates the total price, including a 10% VAT.
     * Displays a message if no products are selected.
     */
    @FXML
    private void addSelectedProductsToCart() {
        selectedProducts.clear(); // Clear previously selected products
        ObservableList<Product> selectedItems = productTable.getItems();

        BigDecimal totalSum = BigDecimal.ZERO;

        for (Product product : selectedItems) {
            int quantity = product.getQuantity();

            if (quantity > 0) { // Only add products with a quantity greater than zero
                selectedProducts.add(product);

                // Calculate the product's total price (Quantity x Price x 10% KDV)
                BigDecimal lineTotal = product.getPrice()
                        .multiply(new BigDecimal(quantity))
                        .multiply(new BigDecimal("1.10")); // %10 KDV
                totalSum = totalSum.add(lineTotal);
            }
        }

        if (selectedProducts.isEmpty()) {
            cartTotalLabel.setText("No products selected.");
            System.out.println("No products were added to the cart.");
            return;
        }

        // Display the total price
        cartTotalLabel.setText("Total Price (+10% VAT): $" + totalSum);
        System.out.println("Products added to cart: " + selectedProducts.size());
        System.out.println("Cart Total: $" + totalSum);
    }

    /**
     * Proceeds to the customer information form tab.
     */
    @FXML
    private void proceedToCustomerInfoForm() {
        tabPane.getSelectionModel().selectNext();
        loadCustomerInfoForm();
    }

    /**
     * Navigates back to the seat selection tab.
     */
    @FXML
    private void backToSeatSelection() {
        tabPane.getSelectionModel().selectPrevious();
    }

    /**
     * Dynamically loads the customer information form based on the selected seats.
     * Creates input fields for name, surname, and age-based discounts for each
     * seat.
     */
    private void loadCustomerInfoForm() {
        System.out.println("loadCustomerInfoForm() called. selectedSeats=" + selectedSeats);

        customerInfoContainer.getChildren().clear(); // Clear existing customer forms
        customerInfoMap.clear(); // Clear the map storing customer information

        for (String seat : selectedSeats) {
            // Create UI components for customer information
            Label seatLabel = new Label("Koltuk: " + seat);

            Label nameLabel = new Label("Ad:");
            TextField nameField = new TextField();
            Label surnameLabel = new Label("Soyad:");
            TextField surnameField = new TextField();

            CheckBox under18CheckBox = new CheckBox("18'den küçük");
            CheckBox over60CheckBox = new CheckBox("60'tan büyük");

            HBox nameBox = new HBox(5, nameLabel, nameField);
            HBox surnameBox = new HBox(5, surnameLabel, surnameField);
            HBox discountBox = new HBox(10, under18CheckBox, over60CheckBox);

            VBox seatForm = new VBox(5, seatLabel, nameBox, surnameBox, discountBox);
            seatForm.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-border-radius: 5;");

            // Create and store customer information for the seat
            CustomerInfo ci = new CustomerInfo();
            customerInfoMap.put(seat, ci);

            // Add listeners for name and surname fields
            nameField.textProperty().addListener((o, oldV, newV) -> ci.setName(newV));
            surnameField.textProperty().addListener((o, oldV, newV) -> ci.setSurname(newV));

            // Add listeners for discount checkboxes
            under18CheckBox.setOnAction(e -> applyDiscountCheck(ci, under18CheckBox, over60CheckBox));
            over60CheckBox.setOnAction(e -> applyDiscountCheck(ci, under18CheckBox, over60CheckBox));

            // Add the form to the container
            customerInfoContainer.getChildren().add(seatForm);
        }
    }

    /**
     * Applies a discount rate based on the customer's age group.
     * If either "Under 18" or "Over 60" is selected, a 50% discount is applied.
     *
     * @param info    the customer information object
     * @param under18 the checkbox for the "Under 18" age group
     * @param over60  the checkbox for the "Over 60" age group
     */
    private void applyDiscountCheck(CustomerInfo info, CheckBox under18, CheckBox over60) {
        if (under18.isSelected() || over60.isSelected()) {
            info.setDiscountRate(new BigDecimal("0.50")); // Apply 50% discount
        } else {
            info.setDiscountRate(BigDecimal.ZERO); // No discount
        }
    }

    /**
     * Proceeds to the final stage of the order process after validating customer
     * information.
     * Displays a message if customer name or surname is missing.
     */
    @FXML
    private void proceedToFinalStage() {
        String fName = customerFirstNameField.getText().trim();
        String lName = customerLastNameField.getText().trim();

        if (fName.isEmpty() || lName.isEmpty()) {
            discountMessageLabel.setText("Please enter customer Name/Surname giriniz (general).");
            return;
        }
        tabPane.getSelectionModel().selectNext();
        System.out.println("Proceeding to final stage...");
    }

    /**
     * Handles user logout by exiting the application.
     *
     * @param event the action event triggered by the logout button
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("User logged out.");
        System.exit(0);
    }

    // ========== GETTERS FOR ORDERCONFIRMATION ETC. ==========

    /**
     * Retrieves the selected movie.
     *
     * @return the currently selected {@link Movie}
     */
    public Movie getSelectedMovie() {
        return selectedMovie;
    }

    /**
     * Retrieves the ComboBox for selecting the session day.
     *
     * @return the {@link ComboBox} for session day selection
     */
    public ComboBox<String> getDayComboBox() {
        return dayComboBox;
    }

    /**
     * Retrieves the ComboBox for selecting the session time.
     *
     * @return the {@link ComboBox} for session time selection
     */
    public ComboBox<String> getSessionComboBox() {
        return sessionComboBox;
    }

    /**
     * Retrieves the set of selected seats.
     *
     * @return a {@link Set} containing the selected seat numbers
     */
    public Set<String> getSelectedSeats() {
        return selectedSeats;
    }

    /**
     * Retrieves the map of customer information linked to seat numbers.
     *
     * @return a {@link Map} of seat numbers to {@link CustomerInfo}
     */
    public Map<String, CustomerInfo> getCustomerInfoMap() {
        return customerInfoMap;
    }

    /**
     * Retrieves the list of selected products.
     *
     * @return a {@link List} of selected {@link Product} items
     */
    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }

    /**
     * Calculates the total ticket price without tax.
     * Considers discounts applied to individual seats.
     *
     * @return the total ticket price as a {@link BigDecimal}
     */
    private BigDecimal calculateTicketTotal() {
        if (selectedSeats == null || selectedSeats.isEmpty() || basePrice == null) {
            return BigDecimal.ZERO; // Return 0 if there are no selected seats or base price is unavailable
        }

        BigDecimal total = BigDecimal.ZERO;

        for (String seat : selectedSeats) {
            CustomerInfo customerInfo = customerInfoMap.get(seat);
            if (customerInfo != null) {
                // Apply discount if available
                BigDecimal discountRate = customerInfo.getDiscountRate(); // Müşteri indirim oranı
                BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));
                total = total.add(discountedPrice);
            } else {
                // Add base price if no discount is available
                total = total.add(basePrice); // İndirim yoksa baz fiyatı ekle
            }
        }

        return total; // Return the calculated total ticket price
    }

    /**
     * Calculates the total ticket price with a 20% tax applied.
     * Considers discounts applied to individual seats.
     *
     * @return the total ticket price with tax as a {@link BigDecimal}
     */
    private BigDecimal calculateTicketTotalWithTax() {
        if (selectedSeats == null || selectedSeats.isEmpty() || basePrice == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (String seat : selectedSeats) {
            CustomerInfo customerInfo = customerInfoMap.get(seat);
            if (customerInfo != null) {
                // Calculate discounted ticket price
                BigDecimal discountRate = customerInfo.getDiscountRate(); // İndirim oranı
                BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));

                // Add 20% tax
                BigDecimal finalPrice = discountedPrice.multiply(new BigDecimal("1.20"));
                total = total.add(finalPrice);
            } else {
                // Add base price with 20% tax if no discount is available
                BigDecimal finalPrice = basePrice.multiply(new BigDecimal("1.20"));
                total = total.add(finalPrice);
            }
        }

        return total; // Return the total ticket price with tax
    }

    /**
     * Calculates the total price of selected products.
     * Applies a 10% VAT to each product's total price (Quantity x Price).
     *
     * @return the total price of all selected products as a {@link BigDecimal}
     */
    private BigDecimal calculateProductTotal() {
        if (selectedProducts == null || selectedProducts.isEmpty()) {
            return BigDecimal.ZERO; // Return 0 if no products are selected
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Product product : selectedProducts) {
            // Calculate the total price of the product (Quantity x Price x 10% KDV)
            BigDecimal lineTotal = product.getPrice()
                    .multiply(new BigDecimal(product.getQuantity()))
                    .multiply(new BigDecimal("1.10")); // %10 KDV
            total = total.add(lineTotal); // Add the product's total to the overall total
        }

        return total; // Return the total price of all products
    }

    /**
     * Handles the confirmation of the order by saving ticket and product details to
     * the database.
     * Generates a PDF invoice and saves it to the "invoices" table.
     * Resets filters and selections after successfully confirming the order.
     *
     * @param event the action event triggered by the confirm order button
     */
    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        if (selectedSeats.isEmpty()) {
            // Alert user if no seats are selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Seats Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one seat before confirming the order.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Start transaction
            String checkSeatSql = "SELECT 1 FROM Tickets WHERE SessionID = ? AND SeatNumber = ?";
            String insertTicketSql = "INSERT INTO Tickets (SessionID, CustomerName, SeatNumber, BasePrice, DiscountRate, FinalPrice) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement checkStmt = conn.prepareStatement(checkSeatSql);
            PreparedStatement ticketStmt = conn.prepareStatement(insertTicketSql);

            BigDecimal totalTicketCost = BigDecimal.ZERO;

            // Process each selected seat
            for (String seat : selectedSeats) {
                checkStmt.setInt(1, selectedSessionID);
                checkStmt.setString(2, seat);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Seat " + seat + " is already reserved.");
                    continue; // Skip already reserved seats
                }

                CustomerInfo customerInfo = customerInfoMap.get(seat);
                if (customerInfo == null) {
                    throw new SQLException("Customer info for seat " + seat + " is missing.");
                }

                // Calculate ticket price with discount and tax
                BigDecimal discountRate = customerInfo.getDiscountRate();
                BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));
                BigDecimal finalPrice = discountedPrice.multiply(new BigDecimal("1.20"));

                // Insert ticket details into the database
                ticketStmt.setInt(1, selectedSessionID);
                ticketStmt.setString(2, customerInfo.getName() + " " + customerInfo.getSurname());
                ticketStmt.setString(3, seat);
                ticketStmt.setBigDecimal(4, basePrice);
                ticketStmt.setBigDecimal(5, discountRate);
                ticketStmt.setBigDecimal(6, finalPrice);

                ticketStmt.addBatch();
                totalTicketCost = totalTicketCost.add(finalPrice);
            }

            ticketStmt.executeBatch(); // Execute ticket insertions

            // Process selected products
            BigDecimal totalProductCost = BigDecimal.ZERO;
            if (!selectedProducts.isEmpty()) {
                String updateProductSql = "UPDATE Products SET StockQuantity = StockQuantity - ? WHERE ProductID = ?";
                PreparedStatement productStmt = conn.prepareStatement(updateProductSql);

                for (Product product : selectedProducts) {
                    if (product.getStockQuantity() < product.getQuantity()) {
                        throw new SQLException("Not enough stock for product: " + product.getProductName());
                    }

                    // Update product stock in the database
                    productStmt.setInt(1, product.getQuantity());
                    productStmt.setInt(2, product.getProductID());
                    productStmt.addBatch();

                    // Calculate product price with KDV
                    BigDecimal lineTotal = product.getPrice()
                            .multiply(new BigDecimal(product.getQuantity()))
                            .multiply(new BigDecimal("1.10")); // %10 KDV
                    totalProductCost = totalProductCost.add(lineTotal);
                }

                productStmt.executeBatch();
                System.out.println("Product stocks updated successfully.");
            }

            conn.commit(); // Commit transaction for tickets and products

            BigDecimal grandTotal = totalTicketCost.add(totalProductCost);

            // ===Generate a PDF invoice====
            String customerDetails = customerFirstNameField.getText() + " " + customerLastNameField.getText();
            String ticketDetails = String.join(", ", selectedSeats);
            String productDetails = selectedProducts.stream()
                    .map(p -> p.getProductName() + " x" + p.getQuantity())
                    .reduce("", (a, b) -> a + ", " + b);

            String fileName = "invoice_" + System.currentTimeMillis() + ".pdf";
            PDFInvoiceGenerator.generateInvoice(fileName, customerDetails, ticketDetails, productDetails,
                    totalTicketCost, totalProductCost);

            // --------------- Save the PDF to the "invoices" table ---------------
            try {
                Path pdfPath = Paths.get(fileName); // <-- Path ve Paths burada lazım
                byte[] pdfBytes = Files.readAllBytes(pdfPath); // <-- Files burada lazım

                // Örneğin customer_name ve pdf_file alanları var diyelim
                String insertInvoiceSql = "INSERT INTO invoices (customer_name, pdf_file) VALUES (?, ?)";
                try (PreparedStatement invoiceStmt = conn.prepareStatement(insertInvoiceSql)) {
                    invoiceStmt.setString(1, customerDetails);
                    invoiceStmt.setBytes(2, pdfBytes);
                    invoiceStmt.executeUpdate();
                    System.out.println("Invoice PDF saved into 'invoices' table!");
                }
                conn.commit(); // <-- PDF'yi de tabloya ekledik, commit edelim
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                // Hata durumunda rollback etmek isterseniz
                // conn.rollback();
            }

            // Show success message to the user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Confirmation");
            alert.setHeaderText("Order Confirmed Successfully");
            alert.setContentText("Invoice has been generated and saved as: " + fileName);
            alert.showAndWait();

            // Reset all filters and selections
            resetFiltersAndSelections();

            // Return to the main tab
            tabPane.getSelectionModel().select(0);

        } catch (SQLException e) {
            e.printStackTrace();
            // Show error message for database issues
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("An error occurred while saving the order.");
            alert.showAndWait();
        }
    }

    /**
     * Displays the order summary in the Order Confirmation view.
     * Loads the OrderConfirmation.fxml layout and passes the necessary data to the
     * controller.
     * Opens a new stage for displaying the order confirmation.
     */
    @FXML
    public void showMyOrder() {
        try {
            // Load the OrderConfirmation.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderConfirmation.fxml"));
            Parent root = loader.load();

            // Get the controller for the Order Confirmation view
            OrderConfirmationController occ = loader.getController();

            // Calculate totals for tickets, products, and the grand total
            BigDecimal ticketTotal = calculateTicketTotalWithTax();
            BigDecimal productTotal = calculateProductTotal();
            BigDecimal grandTotal = ticketTotal.add(productTotal);

            // Pass the order summary data to the controller
            occ.loadOrderSummary(selectedMovie, selectedSeats, selectedProducts, ticketTotal, productTotal, grandTotal);

            // Display the Order Confirmation view in a new stage
            Stage stage = new Stage();
            stage.setTitle("Order Confirmation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if the FXML file cannot be loaded
        }
    }

    /**
     * Resets all filters, selections, and user inputs in the application.
     * Clears search fields, resets tables and UI components, and removes user
     * selections.
     */
    private void resetFiltersAndSelections() {
        // Reset search fields
        searchField.clear();
        searchMessageLabel.setText("");
        movieList.clear();
        movieTable.getSelectionModel().clearSelection();
        selectedMovie = null;
        selectedMovieTitleLabel.setText("");
        selectedMovieGenreLabel.setText("");
        selectedMovieSummaryLabel.setText("");
        selectedMoviePoster.setImage(null);

        // Reset session and seat selections
        dayComboBox.getItems().clear();
        sessionComboBox.getItems().clear();
        hallComboBox.getItems().clear();
        sessionMessageLabel.setText("");
        selectedSessionID = 0;
        selectedSeats.clear();
        sessionSelectedSeats.clear();
        seatGrid.getChildren().clear();
        cartMessageLabel.setText("");

        // Reset product selections
        productTable.getItems().clear();
        selectedProducts.clear();
        cartTotalLabel.setText("");

        // Reset customer information
        customerFirstNameField.clear();
        customerLastNameField.clear();
        customerInfoContainer.getChildren().clear();
        customerInfoMap.clear();

        // Log reset completion
        System.out.println("All filters and selections have been reset.");
    }

}