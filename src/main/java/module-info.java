module com.searesoft.table5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;

    opens com.searesoft.table5 to javafx.fxml;
    exports com.searesoft.table5;
}