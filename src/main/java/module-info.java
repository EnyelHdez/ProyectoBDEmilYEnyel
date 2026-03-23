module org.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;

    opens org.example.proyecto.application to javafx.fxml;
    opens org.example.proyecto.Controladores to javafx.fxml;
    opens org.example.proyecto.Modelos to javafx.base;

    exports org.example.proyecto.application;
}