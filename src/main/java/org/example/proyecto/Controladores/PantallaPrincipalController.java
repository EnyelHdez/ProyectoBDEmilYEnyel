package org.example.proyecto.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PantallaPrincipalController implements Initializable {

    @FXML private StackPane contenedorPrincipal;
    @FXML private Label lblTituloPantalla;
    @FXML private Label lblUsuario;
    @FXML private Label lblCargo;
    @FXML private Label lblNombreUsuario;

    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnProductos;
    @FXML private Button btnCompras;
    @FXML private Button btnEmpleados;
    @FXML private Button btnProveedores;
    @FXML private Button btnPedidos;
    @FXML private Button btnPagos;
    @FXML private Button btnEnvios;
    @FXML private Button btnDevoluciones;
    @FXML private Button btnReclamaciones;
    @FXML private Button btnOrdenCompra;
    @FXML private Button btnConvenios;
    @FXML private Button btnInicio;
    @FXML private Button btnCerrarSesion;

    private Map<Button, String[]> mapaPantallas = new HashMap<>();
    private Button botonActivo = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarMapaPantallas();
        aplicarEstilosBotones();
        cargarPantallaBienvenida();
        mostrarInformacionUsuario();
    }

    private void mostrarInformacionUsuario() {
        if (SesionUsuario.getInstancia().isSesionActiva()) {
            String nombreUsuario = SesionUsuario.getInstancia().getNombreUsuario();
            String cargo = SesionUsuario.getInstancia().getCargoUsuario();
            String nombreCompleto = SesionUsuario.getInstancia().getUsuarioActual().getNombreCompleto();

            if (lblUsuario != null) lblUsuario.setText("👤 " + nombreUsuario);
            if (lblCargo != null) lblCargo.setText("📋 " + cargo);
            if (lblNombreUsuario != null) lblNombreUsuario.setText(nombreCompleto);
        } else {
            cerrarSesion();
        }
    }

    @FXML
    private void cerrarSesion() {
        SesionUsuario.getInstancia().cerrarSesion();
        Stage stage = (Stage) btnInicio.getScene().getWindow();
        stage.close();
        abrirLogin();
    }

    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login - Farmacia Kenia Carmen");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarMapaPantallas() {
        mapaPantallas.put(btnClientes, new String[]{"Gestion de Clientes", "/RegistroCliente.fxml"});
        mapaPantallas.put(btnVentas, new String[]{"Gestion de Ventas", "/RegistroVenta.fxml"});
        mapaPantallas.put(btnProductos, new String[]{"Gestion de Productos", "/RegistroProducto.fxml"});
        mapaPantallas.put(btnCompras, new String[]{"Gestion de Compras", "/RegistroCompra.fxml"});
        mapaPantallas.put(btnEmpleados, new String[]{"Gestion de Empleados", "/RegistroEmpleado.fxml"});
        mapaPantallas.put(btnProveedores, new String[]{"Gestion de Proveedores", "/RegistroProveedor.fxml"});
        mapaPantallas.put(btnPedidos, new String[]{"Gestion de Pedidos", "/RegistroPedido.fxml"});
        mapaPantallas.put(btnPagos, new String[]{"Gestion de Pagos", "/RegistroPago.fxml"});
        mapaPantallas.put(btnEnvios, new String[]{"Gestion de Envios", "/RegistroEnvio.fxml"});
        mapaPantallas.put(btnDevoluciones, new String[]{"Gestion de Devoluciones", "/RegistroDevolucion.fxml"});
        mapaPantallas.put(btnReclamaciones, new String[]{"Gestion de Reclamaciones", "/RegistroReclamacion.fxml"});
        mapaPantallas.put(btnOrdenCompra, new String[]{"Orden de Compra", "/OrdenCompra.fxml"});
        mapaPantallas.put(btnConvenios, new String[]{"Gestion de Convenios", "/RegistroConvenio.fxml"});
    }

    private void aplicarEstilosBotones() {
        for (Button btn : mapaPantallas.keySet()) {
            btn.getStyleClass().add("menu-button");
        }
    }

    private void marcarBotonActivo(Button btnActivo) {
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: #A8D8F0; -fx-font-weight: normal; -fx-background-radius: 10; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
        botonActivo = btnActivo;
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: #2A85CF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
    }

    private void cargarPantalla(String titulo, String rutaFXML) {
        try {
            lblTituloPantalla.setText(titulo);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent nuevaPantalla = loader.load();
            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(nuevaPantalla);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarPantallaBienvenida() {
        lblTituloPantalla.setText("Panel Principal");

        String nombreUsuario = "Usuario";
        if (SesionUsuario.getInstancia().isSesionActiva()) {
            nombreUsuario = SesionUsuario.getInstancia().getUsuarioActual().getNombreCompleto();
        }

        VBox bienvenida = new VBox(20);
        bienvenida.setStyle("-fx-alignment: CENTER; -fx-background-color: #EEF4FB; -fx-padding: 50;");

        Label lblIcono = new Label("🏥");
        lblIcono.setStyle("-fx-font-size: 80px;");

        Label lblTitulo = new Label("¡Bienvenido, " + nombreUsuario + "!");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1A4F7A;");

        Label lblSubtitulo = new Label("Seleccione una opcion del menu lateral para comenzar");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7FA8C9;");

        bienvenida.getChildren().addAll(lblIcono, lblTitulo, lblSubtitulo);
        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(bienvenida);

        if (botonActivo != null) marcarBotonActivo(null);
    }

    private void abrirPantalla(String modulo) {
        Button btn = null;
        switch (modulo) {
            case "Clientes": btn = btnClientes; break;
            case "Ventas": btn = btnVentas; break;
            case "Productos": btn = btnProductos; break;
            case "Compras": btn = btnCompras; break;
            case "Empleados": btn = btnEmpleados; break;
            case "Proveedores": btn = btnProveedores; break;
            case "Pedidos": btn = btnPedidos; break;
            case "Pagos": btn = btnPagos; break;
            case "Envios": btn = btnEnvios; break;
            case "Devoluciones": btn = btnDevoluciones; break;
            case "Reclamaciones": btn = btnReclamaciones; break;
            case "OrdenCompra": btn = btnOrdenCompra; break;
            case "Convenios": btn = btnConvenios; break;
        }

        if (btn != null) {
            String[] datos = mapaPantallas.get(btn);
            marcarBotonActivo(btn);
            cargarPantalla(datos[0], datos[1]);
        }
    }

    @FXML private void abrirClientes() { abrirPantalla("Clientes"); }
    @FXML private void abrirVentas() { abrirPantalla("Ventas"); }
    @FXML private void abrirProductos() { abrirPantalla("Productos"); }
    @FXML private void abrirCompras() { abrirPantalla("Compras"); }
    @FXML private void abrirEmpleados() { abrirPantalla("Empleados"); }
    @FXML private void abrirProveedores() { abrirPantalla("Proveedores"); }
    @FXML private void abrirPedidos() { abrirPantalla("Pedidos"); }
    @FXML private void abrirPagos() { abrirPantalla("Pagos"); }
    @FXML private void abrirEnvios() { abrirPantalla("Envios"); }
    @FXML private void abrirDevoluciones() { abrirPantalla("Devoluciones"); }
    @FXML private void abrirReclamaciones() { abrirPantalla("Reclamaciones"); }
    @FXML private void abrirOrdenCompra() { abrirPantalla("OrdenCompra"); }
    @FXML private void abrirConvenios() { abrirPantalla("Convenios"); }
    @FXML private void irAInicio() { cargarPantallaBienvenida(); }
}