package org.example.proyecto.application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ApplicationDevolucion extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println(getClass().getResource("/RegistroDevolucion.fxml"));

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/RegistroDevolucion.fxml")
        );

        Parent root = loader.load();

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(root, pantalla.getWidth(), pantalla.getHeight());

        stage.setTitle("Sistema de Proveedor");
        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }
}
