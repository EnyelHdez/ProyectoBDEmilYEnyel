package org.example.proyecto.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ApplicationConvenio extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroConvenio.fxml"));
        Parent root = loader.load();

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, pantalla.getWidth(), pantalla.getHeight());

        stage.setTitle("Sistema de Convenios");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}