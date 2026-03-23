package org.example.proyecto.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el menu
            Parent root = FXMLLoader.load(getClass().getResource("/MenuPrincipal.fxml"));

            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("Farmacia Kenia Carmen - Menú Principal");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Centrar la ventana en la pantalla
            primaryStage.centerOnScreen();

            primaryStage.show();

            System.out.println(" Sistema iniciado correctamente");
            System.out.println(" Menú principal cargado");

        } catch(Exception e) {
            System.err.println(" Error al cargar la aplicación");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}