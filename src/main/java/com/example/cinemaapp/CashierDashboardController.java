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
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

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
    private Label searchMessageLabel;
    @FXML
    private ComboBox<String> dayComboBox;
    @FXML
    private ComboBox<String> sessionComboBox;
    @FXML
    private ComboBox<String> hallComboBox;
    @FXML
    private Label messageLabel;
    @FXML
    private TabPane tabPane;

    // Seat Selection
    @FXML
    private GridPane seatGrid;
    @FXML
    private Label cartMessageLabel;
    private final Set<String> selectedSeats = new HashSet<>();
    private final Map<Integer, Set<String>> sessionSelectedSeats = new HashMap<>();

    // Additional Products
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> productIDColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Integer> productStockColumn;
    @FXML
    private TableColumn<Product, BigDecimal> productPriceColumn;

    private ObservableList<Product> selectedProducts = FXCollections.observableArrayList();

    @FXML
    private Label discountMessageLabel;
    @FXML
    private TextField customerFirstNameField;
    @FXML
    private TextField customerLastNameField;
    @FXML
    private Label cartTotalLabel;

    @FXML
    private VBox customerInfoContainer;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie;
    private int selectedSessionID; // Seçilen seansın ID'sini tutar

    private BigDecimal basePrice;

    @FXML
    private TableColumn<Product, Integer> productQuantityColumn;

    // Koltuk başına müşteri bilgilerini tuttuğumuz map
    private final Map<String, CustomerInfo> customerInfoMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Movie TableView
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        summaryColumn.setCellValueFactory(data -> data.getValue().summaryProperty());
        posterColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPoster()));

        movieTable.setItems(movieList);
        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleMovieSelection(newVal);
            }
        });

        // Product TableView
        productIDColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductID()));
        productNameColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProductName()));
        productPriceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));
        productStockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStockQuantity()));

        // Multiple selection
        productTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 2) Quantity sütunu -> Spinner
        productQuantityColumn.setCellValueFactory(c -> c.getValue().quantityProperty().asObject());

        // TableCell içinde Spinner kullanalım:
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
                }
                else if (tabText.equals("Additional Products and Discounts")) {
                    loadProducts();
                }
            }
        });
    }

    // ========== 1) FILM ARAMA ==========
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

    private void searchMovies(String queryType, String value) {
        movieList.clear();
        String query = "SELECT MovieID, Title, Genre, Summary, Poster FROM Movies WHERE " + queryType + " LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, "%" + value + "%");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("MovieID");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                String summary = rs.getString("Summary");
                String posterPath = rs.getString("Poster");

                ImageView iv = new ImageView("file:" + posterPath);
                iv.setFitHeight(100);
                iv.setFitWidth(100);

                movieList.add(new Movie(id, title, genre, summary, iv));
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
        tabPane.getSelectionModel().selectNext();
    }

    // ========== 2) SEANS SEÇIMI ==========
    @FXML
    private void loadSessionDetails() {
        if (selectedMovie == null) {
            sessionMessageLabel.setText("Please select a movie first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT s.SessionDate, s.StartTime, h.HallName "
                    + "FROM Sessions s JOIN Halls h ON s.HallID = h.HallID "
                    + "WHERE s.MovieID = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, selectedMovie.getId());
            ResultSet rs = st.executeQuery();

            clearComboBoxes();
            ObservableList<String> days = FXCollections.observableArrayList();
            ObservableList<String> sessions = FXCollections.observableArrayList();
            ObservableList<String> halls = FXCollections.observableArrayList();

            while (rs.next()) {
                String day = rs.getString("SessionDate");
                String time = rs.getString("StartTime");
                String hall = rs.getString("HallName");

                if (!days.contains(day)) days.add(day);
                if (!sessions.contains(time)) sessions.add(time);
                if (!halls.contains(hall)) halls.add(hall);
            }

            dayComboBox.setItems(days);
            sessionComboBox.setItems(sessions);
            hallComboBox.setItems(halls);

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

    private void clearComboBoxes() {
        dayComboBox.getItems().clear();
        sessionComboBox.getItems().clear();
        hallComboBox.getItems().clear();
    }

    @FXML
    private void confirmSessionSelection() {
        String d = dayComboBox.getValue();
        String t = sessionComboBox.getValue();
        String h = hallComboBox.getValue();

        if (d == null || t == null || h == null) {
            sessionMessageLabel.setText("Please select day, session, and hall.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT SessionID FROM Sessions "
                    + "WHERE SessionDate=? AND StartTime=? "
                    + "AND HallID=(SELECT HallID FROM Halls WHERE HallName=?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, d);
            ps.setString(2, t);
            ps.setString(3, h);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                selectedSessionID = rs.getInt("SessionID");
                sessionMessageLabel.setText("Session confirmed: " + d + " " + t + " " + h);

                // Önceki koltuk seçimlerini sıfırla
                selectedSeats.clear();
                sessionSelectedSeats.clear();
                cartMessageLabel.setText(""); // Seçim mesajını sıfırla

                tabPane.getSelectionModel().selectNext();
            } else {
                sessionMessageLabel.setText("No matching session found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sessionMessageLabel.setText("Error occurred while confirming session.");
        }
    }

    @FXML
    private void backToSessionSelection() {
        tabPane.getSelectionModel().selectPrevious();
    }

    // ========== 3) KOLTUK SEÇIMI ==========
    private void loadSeats() {
        System.out.println("loadSeats() called.");

        if (dayComboBox.getValue() == null || sessionComboBox.getValue() == null || hallComboBox.getValue() == null) {
            cartMessageLabel.setText("Please select day, session, and hall first.");
            System.out.println("-> aborted: comboboxes are null.");
            return;
        }
        seatGrid.getChildren().clear();

        String hallName = hallComboBox.getValue();
        int rows = hallName.equals("Hall_A") ? 4 : 6;
        int cols = hallName.equals("Hall_A") ? 4 : 8;
        System.out.println("rows=" + rows + ", cols=" + cols);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT SeatNumber FROM Tickets WHERE SessionID=?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, selectedSessionID);
            ResultSet rs = st.executeQuery();

            Set<String> reservedSeats = new HashSet<>();
            while (rs.next()) {
                reservedSeats.add(rs.getString("SeatNumber"));
            }
            System.out.println("reserved seats in DB => " + reservedSeats);

            Set<String> prevSel = sessionSelectedSeats.getOrDefault(selectedSessionID, new HashSet<>());
            System.out.println("prev selected => " + prevSel);

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    String seatNumber = (r+1) + "-" + (c+1);
                    Button seatBtn = new Button(seatNumber);
                    seatBtn.setMinSize(40,40);

                    if (reservedSeats.contains(seatNumber)) {
                        seatBtn.setDisable(true);
                        seatBtn.setStyle("-fx-background-color: red;");
                    } else if (prevSel.contains(seatNumber)) {
                        seatBtn.setStyle("-fx-background-color: yellow;");
                        seatBtn.setOnAction(e-> handleSeatSelection(seatNumber, seatBtn));
                    } else {
                        seatBtn.setStyle("-fx-background-color: green;");
                        seatBtn.setOnAction(e-> handleSeatSelection(seatNumber, seatBtn));
                    }
                    seatGrid.add(seatBtn, c, r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            cartMessageLabel.setText("Error loading seats.");
        }
    }

    private void handleSeatSelection(String seatNumber, Button seatBtn) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            seatBtn.setStyle("-fx-background-color: green;");
        } else {
            selectedSeats.add(seatNumber);
            seatBtn.setStyle("-fx-background-color: yellow;");
        }
        cartMessageLabel.setText("Selected seats: " + selectedSeats);
    }

    private BigDecimal fetchBasePriceFromDatabase(int movieId) {
        BigDecimal base = BigDecimal.ZERO;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT BasePrice FROM Movies WHERE MovieID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                base = rs.getBigDecimal("BasePrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return base;
    }

    @FXML
    private void addToCart() {
        if (selectedSeats.isEmpty()) {
            cartMessageLabel.setText("Please select at least one seat.");
            return;
        }
        basePrice = fetchBasePriceFromDatabase(selectedMovie.getId());
        sessionSelectedSeats.put(selectedSessionID, new HashSet<>(selectedSeats));
        cartMessageLabel.setText("Seats added to cart (not in DB).");
        tabPane.getSelectionModel().selectNext();
    }

    // ========== 4) URUNLER + MUSTERI BILGISI ==========
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT ProductID, ProductName, Price, StockQuantity FROM Products";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String productName = rs.getString("ProductName");
                BigDecimal price = rs.getBigDecimal("Price");
                int stockQuantity = rs.getInt("StockQuantity");

                // Ürün nesnesini oluştur ve listeye ekle
                Product product = new Product(productId, productName, price, stockQuantity);
                products.add(product);
            }

            productTable.setItems(products);
            productTable.refresh();
            System.out.println("Products loaded successfully: " + products.size());
        } catch (SQLException e) {
            e.printStackTrace();
            discountMessageLabel.setText("Error loading products.");
        }
    }


    @FXML
    private void addSelectedProductsToCart() {
        selectedProducts.clear(); // Önce seçili ürünleri sıfırla
        ObservableList<Product> selectedItems = productTable.getItems();

        BigDecimal totalSum = BigDecimal.ZERO;

        for (Product product : selectedItems) {
            int quantity = product.getQuantity();

            if (quantity > 0) { // Miktar sıfırdan büyükse sepete ekle
                selectedProducts.add(product);

                // Ürünün toplam fiyatını hesapla (Adet x Fiyat x KDV %10)
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

        // Toplam fiyatı göster
        cartTotalLabel.setText("Total Price (+10% VAT): $" + totalSum);
        System.out.println("Products added to cart: " + selectedProducts.size());
        System.out.println("Cart Total: $" + totalSum);
    }




    @FXML
    private void proceedToCustomerInfoForm() {
        tabPane.getSelectionModel().selectNext();
        loadCustomerInfoForm();
    }

    @FXML
    private void backToSeatSelection() {
        tabPane.getSelectionModel().selectPrevious();
    }

    private void loadCustomerInfoForm() {
        System.out.println("loadCustomerInfoForm() called. selectedSeats=" + selectedSeats);

        customerInfoContainer.getChildren().clear();
        customerInfoMap.clear();

        for (String seat : selectedSeats) {
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

            CustomerInfo ci = new CustomerInfo();
            customerInfoMap.put(seat, ci);

            nameField.textProperty().addListener((o, oldV, newV) -> ci.setName(newV));
            surnameField.textProperty().addListener((o, oldV, newV) -> ci.setSurname(newV));

            under18CheckBox.setOnAction(e-> applyDiscountCheck(ci, under18CheckBox, over60CheckBox));
            over60CheckBox.setOnAction(e-> applyDiscountCheck(ci, under18CheckBox, over60CheckBox));

            customerInfoContainer.getChildren().add(seatForm);
        }
    }

    private void applyDiscountCheck(CustomerInfo info, CheckBox under18, CheckBox over60) {
        if (under18.isSelected() || over60.isSelected()) {
            info.setDiscountRate(new BigDecimal("0.50"));
        } else {
            info.setDiscountRate(BigDecimal.ZERO);
        }
    }

    @FXML
    private void proceedToFinalStage() {
        String fName = customerFirstNameField.getText().trim();
        String lName = customerLastNameField.getText().trim();
        if (fName.isEmpty() || lName.isEmpty()) {
            discountMessageLabel.setText("Lütfen Müşteri Adı/Soyadı giriniz (genel).");
            return;
        }
        tabPane.getSelectionModel().selectNext();
        System.out.println("Proceeding to final stage...");
    }



    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("User logged out.");
        System.exit(0);
    }

    // ========== GETTERS FOR ORDERCONFIRMATION ETC. ==========
    public Movie getSelectedMovie() {
        return selectedMovie;
    }
    public ComboBox<String> getDayComboBox() {
        return dayComboBox;
    }
    public ComboBox<String> getSessionComboBox() {
        return sessionComboBox;
    }
    public Set<String> getSelectedSeats() {
        return selectedSeats;
    }
    public Map<String, CustomerInfo> getCustomerInfoMap() {
        return customerInfoMap;
    }
    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }
    private BigDecimal calculateTicketTotal() {
        if (selectedSeats == null || selectedSeats.isEmpty() || basePrice == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (String seat : selectedSeats) {
            CustomerInfo customerInfo = customerInfoMap.get(seat);
            if (customerInfo != null) {
                BigDecimal discountRate = customerInfo.getDiscountRate(); // Müşteri indirim oranı
                BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));
                total = total.add(discountedPrice);
            } else {
                total = total.add(basePrice); // İndirim yoksa baz fiyatı ekle
            }
        }

        return total;
    }
    private BigDecimal calculateProductTotal() {
        if (selectedProducts == null || selectedProducts.isEmpty()) {
            return BigDecimal.ZERO; // Seçili ürün yoksa toplam 0
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Product product : selectedProducts) {
            // Ürünün toplam fiyatını hesapla (Adet x Fiyat x KDV %10)
            BigDecimal lineTotal = product.getPrice()
                    .multiply(new BigDecimal(product.getQuantity()))
                    .multiply(new BigDecimal("1.10")); // %10 KDV
            total = total.add(lineTotal); // Her ürünün toplamını genel toplama ekle
        }

        return total; // Tüm ürünlerin toplam fiyatını döndür
    }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        if (selectedSeats.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Seats Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one seat before confirming the order.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction başlat

            String checkSeatSql = "SELECT 1 FROM Tickets WHERE SessionID = ? AND SeatNumber = ?";
            String insertTicketSql = "INSERT INTO Tickets (SessionID, CustomerName, SeatNumber, BasePrice, DiscountRate, FinalPrice) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement checkStmt = conn.prepareStatement(checkSeatSql);
            PreparedStatement ticketStmt = conn.prepareStatement(insertTicketSql);

            BigDecimal totalTicketCost = BigDecimal.ZERO;

            for (String seat : selectedSeats) {
                checkStmt.setInt(1, selectedSessionID);
                checkStmt.setString(2, seat);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Seat " + seat + " is already reserved.");
                    continue; // Bu koltuğu atla
                }

                CustomerInfo customerInfo = customerInfoMap.get(seat);
                if (customerInfo == null) {
                    throw new SQLException("Customer info for seat " + seat + " is missing.");
                }

                BigDecimal discountRate = customerInfo.getDiscountRate();
                BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountRate));
                BigDecimal finalPrice = discountedPrice.multiply(new BigDecimal("1.20"));

                ticketStmt.setInt(1, selectedSessionID);
                ticketStmt.setString(2, customerInfo.getName() + " " + customerInfo.getSurname());
                ticketStmt.setString(3, seat);
                ticketStmt.setBigDecimal(4, basePrice);
                ticketStmt.setBigDecimal(5, discountRate);
                ticketStmt.setBigDecimal(6, finalPrice);

                ticketStmt.addBatch();
                totalTicketCost = totalTicketCost.add(finalPrice);
            }

            ticketStmt.executeBatch();
            conn.commit(); // Transaction onayla

            if (!selectedProducts.isEmpty()) {
                String updateProductSql = "UPDATE Products SET StockQuantity = StockQuantity - ? WHERE ProductID = ?";
                PreparedStatement productStmt = conn.prepareStatement(updateProductSql);

                for (Product product : selectedProducts) {
                    if (product.getStockQuantity() < product.getQuantity()) {
                        throw new SQLException("Not enough stock for product: " + product.getProductName());
                    }

                    productStmt.setInt(1, product.getQuantity());
                    productStmt.setInt(2, product.getProductID());
                    productStmt.addBatch();
                }

                productStmt.executeBatch();
                System.out.println("Product stocks updated successfully.");
            }

            conn.commit();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Confirmation");
            alert.setHeaderText("Order Confirmed Successfully");
            alert.setContentText("Tickets and products have been successfully saved.");
            alert.showAndWait();

            // Filtreleri ve seçimleri sıfırla
            resetFiltersAndSelections();

            // Ana ekrana dön
            tabPane.getSelectionModel().select(0);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("An error occurred while saving the order.");
            alert.showAndWait();
        }
    }






    @FXML
    public void showMyOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderConfirmation.fxml"));
            Parent root = loader.load();

            OrderConfirmationController occ = loader.getController();

            // Gerekli verileri hesapla
            BigDecimal ticketTotal = calculateTicketTotal();
            BigDecimal productTotal = calculateProductTotal();
            BigDecimal grandTotal = ticketTotal.add(productTotal);

            // Metodu doğru argümanlarla çağır
            occ.loadOrderSummary(selectedMovie, selectedSeats, selectedProducts, ticketTotal, productTotal, grandTotal);

            Stage stage = new Stage();
            stage.setTitle("Order Confirmation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void resetFiltersAndSelections() {
        // Filtreleri sıfırla
        searchField.clear();
        searchMessageLabel.setText("");
        movieList.clear();
        movieTable.getSelectionModel().clearSelection();
        selectedMovie = null;
        selectedMovieTitleLabel.setText("");
        selectedMovieGenreLabel.setText("");
        selectedMovieSummaryLabel.setText("");
        selectedMoviePoster.setImage(null);

        // Seans ve koltuk bilgilerini sıfırla
        dayComboBox.getItems().clear();
        sessionComboBox.getItems().clear();
        hallComboBox.getItems().clear();
        sessionMessageLabel.setText("");
        selectedSessionID = 0;
        selectedSeats.clear();
        sessionSelectedSeats.clear();
        seatGrid.getChildren().clear();
        cartMessageLabel.setText("");

        // Ürün seçimlerini sıfırla
        productTable.getItems().clear();
        selectedProducts.clear();
        cartTotalLabel.setText("");

        // Müşteri bilgilerini sıfırla
        customerFirstNameField.clear();
        customerLastNameField.clear();
        customerInfoContainer.getChildren().clear();
        customerInfoMap.clear();

        System.out.println("All filters and selections have been reset.");
    }




}