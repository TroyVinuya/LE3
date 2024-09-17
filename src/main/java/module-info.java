module com.guiyomi {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.guiyomi to javafx.fxml;
    exports com.guiyomi;
}
