package org.example.proyecto.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private Label lblBadgeEstado;

    // Botones del menú
    @FXML private Button btnMedicamentos;
    @FXML private Button btnRecetas;
    @FXML private Button btnPerdidas;
    @FXML private Button btnCuentasPago;
    @FXML private Button btnHistorialReclamaciones;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnProductos;
    @FXML private Button btnCompras;
    @FXML private Button btnUsuarios;
    @FXML private Button btnEmpleados;
    @FXML private Button btnProveedores;
    @FXML private Button btnPedidos;
    @FXML private Button btnPagos;
    @FXML private Button btnEnvios;
    @FXML private Button btnDevoluciones;
    @FXML private Button btnFidelizacion;
    @FXML private Button btnReclamaciones;
    @FXML private Button btnOrdenCompra;
    @FXML private Button btnConvenios;
    @FXML private Button btnInicio;
    @FXML private Button btnCerrarSesion;

    private Map<Button, String[]> mapaPantallas = new HashMap<>();
    private Map<String, List<String>> permisosPorCargo = new HashMap<>();
    private Button botonActivo = null;
    private Map<String, Parent> vistasCache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarPermisos();
        configurarMapaPantallas();
        aplicarControlDeAcceso();
        aplicarEstilosBotones();
        cargarPantallaBienvenida();
        mostrarInformacionUsuario();
        configurarBadgePorRol();
    }

    private void configurarPermisos() {
        // Administrador: ACCESO TOTAL
        permisosPorCargo.put("Administrador", Arrays.asList(
                "Usuarios", "Clientes", "Ventas", "Productos", "Compras", "Empleados",
                "Proveedores", "Pedidos", "Pagos", "Envios", "Devoluciones",
                "Fidelizacion", "Reclamaciones", "OrdenCompra", "Convenios",
                "Medicamentos", "Recetas", "Perdidas", "CuentasPago", "HistorialReclamaciones"  // ← NUEVOS
        ));

// Farmacéutico
        permisosPorCargo.put("Farmacéutico", Arrays.asList(
                "Clientes", "Ventas", "Productos", "Pedidos", "Convenios", "Medicamentos", "Recetas"  // ← NUEVOS
        ));

// Cajero
        permisosPorCargo.put("Cajero", Arrays.asList(
                "Clientes", "Ventas", "CuentasPago"  // ← NUEVO
        ));

// Almacenista
        permisosPorCargo.put("Almacenista", Arrays.asList(
                "Productos", "Compras", "Proveedores", "OrdenCompra", "Perdidas"  // ← NUEVO
        ));

// Auxiliar
        permisosPorCargo.put("Auxiliar", Arrays.asList(
                "Clientes", "Ventas", "Productos", "Medicamentos"  // ← NUEVO
        ));
    }

    private void aplicarControlDeAcceso() {
        String cargoUsuario = SesionUsuario.getInstancia().getCargoUsuario();
        List<String> permisos = permisosPorCargo.getOrDefault(cargoUsuario, new ArrayList<>());

        // Aplicar visibilidad según permisos
        if (btnUsuarios != null) {
            btnUsuarios.setVisible(permisos.contains("Usuarios"));
            btnUsuarios.setManaged(permisos.contains("Usuarios"));
        }

        if (btnClientes != null) {
            btnClientes.setVisible(permisos.contains("Clientes"));
            btnClientes.setManaged(permisos.contains("Clientes"));
        }

        if (btnVentas != null) {
            btnVentas.setVisible(permisos.contains("Ventas"));
            btnVentas.setManaged(permisos.contains("Ventas"));
        }

        if (btnProductos != null) {
            btnProductos.setVisible(permisos.contains("Productos"));
            btnProductos.setManaged(permisos.contains("Productos"));
        }

        if (btnCompras != null) {
            btnCompras.setVisible(permisos.contains("Compras"));
            btnCompras.setManaged(permisos.contains("Compras"));
        }

        if (btnEmpleados != null) {
            btnEmpleados.setVisible(permisos.contains("Empleados"));
            btnEmpleados.setManaged(permisos.contains("Empleados"));
        }

        if (btnProveedores != null) {
            btnProveedores.setVisible(permisos.contains("Proveedores"));
            btnProveedores.setManaged(permisos.contains("Proveedores"));
        }

        if (btnPedidos != null) {
            btnPedidos.setVisible(permisos.contains("Pedidos"));
            btnPedidos.setManaged(permisos.contains("Pedidos"));
        }

        if (btnPagos != null) {
            btnPagos.setVisible(permisos.contains("Pagos"));
            btnPagos.setManaged(permisos.contains("Pagos"));
        }

        if (btnEnvios != null) {
            btnEnvios.setVisible(permisos.contains("Envios"));
            btnEnvios.setManaged(permisos.contains("Envios"));
        }

        if (btnDevoluciones != null) {
            btnDevoluciones.setVisible(permisos.contains("Devoluciones"));
            btnDevoluciones.setManaged(permisos.contains("Devoluciones"));
        }

        if (btnFidelizacion != null) {
            btnFidelizacion.setVisible(permisos.contains("Fidelizacion"));
            btnFidelizacion.setManaged(permisos.contains("Fidelizacion"));
        }

        if (btnReclamaciones != null) {
            btnReclamaciones.setVisible(permisos.contains("Reclamaciones"));
            btnReclamaciones.setManaged(permisos.contains("Reclamaciones"));
        }

        if (btnOrdenCompra != null) {
            btnOrdenCompra.setVisible(permisos.contains("OrdenCompra"));
            btnOrdenCompra.setManaged(permisos.contains("OrdenCompra"));
        }

        if (btnConvenios != null) {
            btnConvenios.setVisible(permisos.contains("Convenios"));
            btnConvenios.setManaged(permisos.contains("Convenios"));
        }
    }

    private void mostrarInformacionUsuario() {
        if (SesionUsuario.getInstancia().isSesionActiva()) {
            String nombreUsuario = SesionUsuario.getInstancia().getNombreUsuario();
            String cargo = SesionUsuario.getInstancia().getCargoUsuario();
            String nombreCompleto = SesionUsuario.getInstancia().getNombreUsuario();

            if (lblUsuario != null) lblUsuario.setText("👤 " + nombreUsuario);
            if (lblCargo != null) lblCargo.setText("📋 " + cargo);
            if (lblNombreUsuario != null) lblNombreUsuario.setText(nombreCompleto);
        } else {
            cerrarSesion();
        }
    }

    private void configurarBadgePorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (lblBadgeEstado != null) {
            switch (rol) {
                case "Administrador":
                    lblBadgeEstado.setText("🔒 Administrador");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1A4F6E; -fx-background-color: #E3F2FD; -fx-padding: 6 16; -fx-background-radius: 30;");
                    break;
                case "Cajero":
                    lblBadgeEstado.setText("💰 Cajero");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #E65100; -fx-background-color: #FFF3E0; -fx-padding: 6 16; -fx-background-radius: 30;");
                    break;
                case "Farmacéutico":
                    lblBadgeEstado.setText("💊 Farmacéutico");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #2E7D32; -fx-background-color: #E8F5E9; -fx-padding: 6 16; -fx-background-radius: 30;");
                    break;
                default:
                    lblBadgeEstado.setText("👤 " + rol);
                    break;
            }
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
        mapaPantallas.put(btnUsuarios, new String[]{"Gestión de Usuarios", "/GestionUsuarios.fxml"});
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
        mapaPantallas.put(btnFidelizacion, new String[]{"Gestión de Fidelización", "/RegistroFidelizacion.fxml"});
        mapaPantallas.put(btnReclamaciones, new String[]{"Gestión de Reclamaciones", "/RegistroReclamacion.fxml"});  // ← NUEVO
        mapaPantallas.put(btnOrdenCompra, new String[]{"Orden de Compra", "/OrdenCompra.fxml"});
        mapaPantallas.put(btnMedicamentos, new String[]{"Gestión de Medicamentos", "/RegistroMedicamento.fxml"});
        mapaPantallas.put(btnRecetas, new String[]{"Gestión de Recetas Médicas", "/RecetaMedica.fxml"});
        mapaPantallas.put(btnPerdidas, new String[]{"Registro de Pérdidas", "/RegistroPerdida.fxml"});
        mapaPantallas.put(btnCuentasPago, new String[]{"Cuentas de Pago", "/CuentaPago.fxml"});
        mapaPantallas.put(btnHistorialReclamaciones, new String[]{"Historial de Reclamaciones", "/HistorialReclamacion.fxml"});
        mapaPantallas.put(btnConvenios, new String[]{"Gestión de Convenios", "/RegistroConvenio.fxml"});
    }

    private void aplicarEstilosBotones() {
        for (Button btn : mapaPantallas.keySet()) {
            if (btn != null) {
                btn.getStyleClass().add("menu-button");
                btn.setOnMouseEntered(e -> {
                    if (botonActivo != btn) {
                        btn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                    }
                });
                btn.setOnMouseExited(e -> {
                    if (botonActivo != btn) {
                        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: normal; -fx-background-radius: 12; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                    }
                });
            }
        }
    }

    private void marcarBotonActivo(Button btnActivo) {
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: normal; -fx-background-radius: 12; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
        botonActivo = btnActivo;
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
    }

    private void cargarPantalla(String titulo, String rutaFXML) {
        try {
            lblTituloPantalla.setText(titulo);

            // Usar caché de vistas para mejorar rendimiento
            Parent nuevaPantalla = vistasCache.get(rutaFXML);
            if (nuevaPantalla == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                nuevaPantalla = loader.load();
                vistasCache.put(rutaFXML, nuevaPantalla);
            }

            contenedorPrincipal.getChildren().clear();
            contenedorPrincipal.getChildren().add(nuevaPantalla);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarPantallaError(titulo, rutaFXML, e.getMessage());
        }
    }

    private void cargarPantallaBienvenida() {
        lblTituloPantalla.setText("Panel Principal");

        String nombreUsuario = "Usuario";
        if (SesionUsuario.getInstancia().isSesionActiva()) {
            nombreUsuario = SesionUsuario.getInstancia().getNombreUsuario();
        }

        VBox bienvenida = new VBox(20);
        bienvenida.setStyle("-fx-alignment: CENTER; -fx-background-color: #F4F7FC; -fx-padding: 50;");

        Label lblIcono = new Label("🏥");
        lblIcono.setStyle("-fx-font-size: 80px;");

        Label lblTitulo = new Label("¡Bienvenido, " + nombreUsuario + "!");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1A4F6E;");

        Label lblSubtitulo = new Label("Seleccione una opción del menú lateral para comenzar");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7A9FBB;");

        bienvenida.getChildren().addAll(lblIcono, lblTitulo, lblSubtitulo);
        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(bienvenida);

        if (botonActivo != null) marcarBotonActivo(null);
    }

    private void mostrarPantallaError(String titulo, String ruta, String error) {
        VBox errorBox = new VBox(15);
        errorBox.setStyle("-fx-alignment: CENTER; -fx-background-color: #FEF2F2; -fx-padding: 40; -fx-border-color: #FCA5A5; -fx-border-width: 2; -fx-border-radius: 10;");

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

    // Métodos para abrir cada módulo
    @FXML private void abrirClientes() {
        abrirPantalla("Clientes");
    }

    @FXML private void abrirUsuarios() {
        abrirPantalla("Usuarios");
    }

    @FXML private void abrirVentas() {
        abrirPantalla("Ventas");
    }

    @FXML private void abrirProductos() {
        abrirPantalla("Productos");
    }

    @FXML private void abrirCompras() {
        abrirPantalla("Compras");
    }

    @FXML private void abrirEmpleados() {
        abrirPantalla("Empleados");
    }


    @FXML private void abrirProveedores() {
        abrirPantalla("Proveedores");
    }

    @FXML private void abrirPedidos() {
        abrirPantalla("Pedidos");
    }

    @FXML private void abrirPagos() {
        abrirPantalla("Pagos");
    }

    @FXML private void abrirEnvios() {
        abrirPantalla("Envios");
    }

    @FXML private void abrirDevoluciones() {
        abrirPantalla("Devoluciones");
    }

    @FXML private void abrirFidelizacion() {
        abrirPantalla("Fidelizacion");
    }

    @FXML private void abrirReclamaciones() {
        abrirPantalla("Reclamaciones");
    }

    @FXML private void abrirOrdenCompra() {
        abrirPantalla("OrdenCompra");
    }

    @FXML private void abrirConvenios() {
        abrirPantalla("Convenios");
    }
    @FXML private void abrirMedicamentos() { abrirPantalla("Medicamentos"); }
    @FXML private void abrirRecetas() { abrirPantalla("Recetas"); }
    @FXML private void abrirPerdidas() { abrirPantalla("Perdidas"); }
    @FXML private void abrirCuentasPago() { abrirPantalla("CuentasPago"); }
    @FXML private void abrirHistorialReclamaciones() { abrirPantalla("HistorialReclamaciones"); }

    @FXML
    private void irAInicio() {
        cargarPantallaBienvenida();
        if (botonActivo != null) marcarBotonActivo(null);
    }

    private void abrirPantalla(String modulo) {
        Button btn = null;
        switch (modulo) {
            case "Usuarios": btn = btnUsuarios; break;
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
            case "Fidelizacion": btn = btnFidelizacion; break;
            case "Reclamaciones": btn = btnReclamaciones; break;  // ← NUEVO
            case "OrdenCompra": btn = btnOrdenCompra; break;
            case "Convenios": btn = btnConvenios; break;
            case "Medicamentos": btn = btnMedicamentos; break;
            case "Recetas": btn = btnRecetas; break;
            case "Perdidas": btn = btnPerdidas; break;
            case "CuentasPago": btn = btnCuentasPago; break;
            case "HistorialReclamaciones": btn = btnHistorialReclamaciones; break;
        }

        if (btn != null && mapaPantallas.containsKey(btn)) {
            String[] datos = mapaPantallas.get(btn);
            marcarBotonActivo(btn);
            cargarPantalla(datos[0], datos[1]);
        } else {
            mostrarAlerta("Módulo no disponible", "Esta funcionalidad estará disponible próximamente.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}