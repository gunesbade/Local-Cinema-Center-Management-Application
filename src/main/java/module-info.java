module com.example.cinemaapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // java.sql modülünü ekliyoruz
    requires javafx.graphics;

    opens com.example.cinemaapp to javafx.fxml;
    exports com.example.cinemaapp;
}

