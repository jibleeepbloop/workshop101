module com.example.project101 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.dotenv;

    opens com.example.project101 to javafx.fxml;
    exports com.example.project101;
}