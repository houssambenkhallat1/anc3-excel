module excel {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires component.inspector.fx;

    exports excel;
    opens excel to javafx.fxml;
}