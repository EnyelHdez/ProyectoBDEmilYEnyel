module org.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires net.sf.jasperreports.core;
    requires net.sf.jasperreports.pdf;
    requires jakarta.mail;
    requires jakarta.activation;
    requires org.eclipse.angus.mail;

    opens org.example.proyecto.application to javafx.fxml;
    opens org.example.proyecto.Controladores to javafx.fxml;
    opens org.example.proyecto.util to javafx.fxml;
    opens org.example.proyecto.Modelos to javafx.base;

    exports org.example.proyecto.application;
}