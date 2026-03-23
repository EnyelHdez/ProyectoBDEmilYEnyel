package org.example.proyecto.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MenuPrincipalController {

    @FXML
    private void abrirProveedores(ActionEvent event) {
        abrirVentana("/RegistroProveedor.fxml", "Gestión de Proveedores - Farmacia Kenia Carmen", 920, 650);
    }

    @FXML
    private void abrirProductos(ActionEvent event) {
        abrirVentana("/RegistroProducto.fxml", "Gestión de Productos - Farmacia Kenia Carmen", 920, 680);
    }

    @FXML
    private void abrirOrdenCompra(ActionEvent event) {
        abrirVentana("/OrdenCompra.fxml", "Orden de Compra - Farmacia Kenia Carmen", 650, 450);
    }

    @FXML
    private void abrirRegistroCompra(ActionEvent event) {
        abrirVentana("/RegistroCompra.fxml", "Registrar Compra - Farmacia Kenia Carmen", 750, 700);
    }

    @FXML
    private void abrirRegistroPago(ActionEvent event) {
        abrirVentana("/RegistroPago.fxml", "Registro de Pagos - Farmacia Kenia Carmen", 739, 650);
    }

    @FXML
    private void salir(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Salida");
        confirmacion.setHeaderText("¿Desea salir del sistema?");
        confirmacion.setContentText("Se cerrarán todas las ventanas abiertas");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.out.println(" Cerrando sistema...");
            System.exit(0);
        }
    }

    private void abrirVentana(String rutaFXML, String titulo, int ancho, int alto) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root, ancho, alto));
            stage.setResizable(false);
            stage.show();

            System.out.println(" Ventana abierta: " + titulo);

            // OPCIONAL: Si quieres que se cierre el menú al abrir una ventana, descomenta esto:
            // ((Stage)((Node)event.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println(" Error al abrir la ventana: " + titulo);
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("No se pudo abrir la ventana");
            alerta.setContentText("Error: " + e.getMessage());
            alerta.showAndWait();
        }
    }
}