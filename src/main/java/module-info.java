module com.example.cinemaapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.apache.pdfbox;

    opens com.example.cinemaapp to javafx.fxml;
    exports com.example.cinemaapp;
}
