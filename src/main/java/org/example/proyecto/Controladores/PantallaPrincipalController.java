package org.example.proyecto.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PantallaPrincipalController implements Initializable {

    @FXML private StackPane contenedorPrincipal;
    @FXML private Label lblTituloPantalla;
    @FXML private Button btnClientes, btnVentas, btnProductos, btnCompras;
    @FXML private Button btnEmpleados, btnProveedores, btnPedidos, btnPagos;
    @FXML private Button btnEnvios, btnDevoluciones, btnReclamaciones, btnOrdenCompra;
    @FXML private Button btnInicio;

    private Map<Button, String[]> mapaPantallas = new HashMap<>();
    private Button botonActivo = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarMapaPantallas();
        aplicarEstilosBotones();
        cargarPantallaBienvenida();
    }

    private void configurarMapaPantallas() {
        // Formato: { "Título", "Ruta del FXML" }
        mapaPantallas.put(btnClientes, new String[]{"Gestión de Clientes", "/RegistroCliente.fxml"});
        mapaPantallas.put(btnVentas, new String[]{"Gestión de Ventas", "/RegistroVenta.fxml"});
        mapaPantallas.put(btnProductos, new String[]{"Gestión de Productos", "/RegistroProducto.fxml"});
        mapaPantallas.put(btnCompras, new String[]{"Gestión de Compras", "/RegistroCompra.fxml"});
        mapaPantallas.put(btnEmpleados, new String[]{"Gestión de Empleados", "/RegistroEmpleado.fxml"});
        mapaPantallas.put(btnProveedores, new String[]{"Gestión de Proveedores", "/RegistroProveedor.fxml"});
        mapaPantallas.put(btnPedidos, new String[]{"Gestión de Pedidos", "/RegistroPedido.fxml"});
        mapaPantallas.put(btnPagos, new String[]{"Gestión de Pagos", "/RegistroPago.fxml"});
        mapaPantallas.put(btnEnvios, new String[]{"Gestión de Envíos", "/RegistroEnvio.fxml"});
        mapaPantallas.put(btnDevoluciones, new String[]{"Gestión de Devoluciones", "/RegistroDevolucion.fxml"});
        mapaPantallas.put(btnReclamaciones, new String[]{"Gestión de Reclamaciones", "/RegistroReclamacion.fxml"});
        mapaPantallas.put(btnOrdenCompra, new String[]{"Orden de Compra", "/OrdEnCompra.fxml"});
    }

    private void aplicarEstilosBotones() {
        for (Button btn : mapaPantallas.keySet()) {
            btn.getStyleClass().add("menu-button");
            btn.setOnMouseEntered(e -> {
                if (botonActivo != btn) {
                    btn.setStyle("-fx-background-color: #2A85CF; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-background-radius: 10; " +
                            "-fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                }
            });
            btn.setOnMouseExited(e -> {
                if (botonActivo != btn) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #A8D8F0; " +
                            "-fx-font-weight: normal; -fx-background-radius: 10; " +
                            "-fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                }
            });
        }
    }

    private void marcarBotonActivo(Button btnActivo) {
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: #A8D8F0; " +
                    "-fx-font-weight: normal; -fx-background-radius: 10; " +
                    "-fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
        botonActivo = btnActivo;
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: #2A85CF; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 10; " +
                    "-fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
    }

    private void cargarPantalla(String titulo, String rutaFXML) {
        try {
            lblTituloPantalla.setText(titulo);

            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent nuevaPantalla = loader.load();

            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(nuevaPantalla);


        } catch (IOException e) {
            e.printStackTrace();
            mostrarPantallaError(titulo, rutaFXML, e.getMessage());
        }
    }

    private void cargarPantallaBienvenida() {
        lblTituloPantalla.setText("Panel Principal");

        // Crear pantalla de bienvenida
        VBox bienvenida = new VBox(20);
        bienvenida.setStyle("-fx-alignment: CENTER; -fx-background-color: #EEF4FB; -fx-padding: 50;");

        Label lblIcono = new Label("🏥");
        lblIcono.setStyle("-fx-font-size: 80px;");

        Label lblTitulo = new Label("¡Bienvenido al Sistema!");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1A4F7A;");

        Label lblSubtitulo = new Label("Seleccione una opción del menú lateral para comenzar");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7FA8C9;");

        Label lblInfo = new Label("Farmacia Kenia Carmen - Sistema de Gestión Integral");
        lblInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #9BBDD6; -fx-padding: 20 0 0 0;");

        bienvenida.getChildren().addAll(lblIcono, lblTitulo, lblSubtitulo, lblInfo);

        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(bienvenida);

        if (botonActivo != null) {
            marcarBotonActivo(null);
        }
    }

    private void mostrarPantallaError(String titulo, String ruta, String error) {
        VBox errorBox = new VBox(15);
        errorBox.setStyle("-fx-alignment: CENTER; -fx-background-color: #FEF2F2; -fx-padding: 40; " +
                "-fx-border-color: #FCA5A5; -fx-border-width: 2; -fx-border-radius: 10;");

        Label lblIcono = new Label("⚠️");
        lblIcono.setStyle("-fx-font-size: 50px;");

        Label lblTitulo = new Label("Error al cargar la pantalla");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #DC2626;");

        Label lblDetalle = new Label("No se pudo cargar: " + titulo);
        lblDetalle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F1D1D;");

        Label lblRuta = new Label("Archivo: " + ruta);
        lblRuta.setStyle("-fx-font-size: 11px; -fx-text-fill: #991B1B;");

        Label lblError = new Label("Error: " + error);
        lblError.setStyle("-fx-font-size: 11px; -fx-text-fill: #991B1B; -fx-wrap-text: true;");

        errorBox.getChildren().addAll(lblIcono, lblTitulo, lblDetalle, lblRuta, lblError);

        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(errorBox);
    }

    // Métodos de navegación
    @FXML private void abrirClientes() {
        String[] datos = mapaPantallas.get(btnClientes);
        marcarBotonActivo(btnClientes);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirVentas() {
        String[] datos = mapaPantallas.get(btnVentas);
        marcarBotonActivo(btnVentas);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirProductos() {
        String[] datos = mapaPantallas.get(btnProductos);
        marcarBotonActivo(btnProductos);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirCompras() {
        String[] datos = mapaPantallas.get(btnCompras);
        marcarBotonActivo(btnCompras);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirEmpleados() {
        String[] datos = mapaPantallas.get(btnEmpleados);
        marcarBotonActivo(btnEmpleados);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirProveedores() {
        String[] datos = mapaPantallas.get(btnProveedores);
        marcarBotonActivo(btnProveedores);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirPedidos() {
        String[] datos = mapaPantallas.get(btnPedidos);
        marcarBotonActivo(btnPedidos);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirPagos() {
        String[] datos = mapaPantallas.get(btnPagos);
        marcarBotonActivo(btnPagos);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirEnvios() {
        String[] datos = mapaPantallas.get(btnEnvios);
        marcarBotonActivo(btnEnvios);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirDevoluciones() {
        String[] datos = mapaPantallas.get(btnDevoluciones);
        marcarBotonActivo(btnDevoluciones);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirReclamaciones() {
        String[] datos = mapaPantallas.get(btnReclamaciones);
        marcarBotonActivo(btnReclamaciones);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void abrirOrdenCompra() {
        String[] datos = mapaPantallas.get(btnOrdenCompra);
        marcarBotonActivo(btnOrdenCompra);
        cargarPantalla(datos[0], datos[1]);
    }

    @FXML private void irAInicio() {
        cargarPantallaBienvenida();
    }
}