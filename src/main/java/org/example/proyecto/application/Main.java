package org.example.proyecto.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/PantallaPrincipal.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Sistema Farmacia Kenia Carmen");
        stage.setScene(scene);
        stage.show();
    }
}



