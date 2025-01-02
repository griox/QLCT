module com.example.qlct {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.qlct to javafx.fxml;
    exports com.example.qlct;
}