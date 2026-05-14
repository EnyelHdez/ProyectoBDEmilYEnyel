package org.example.proyecto.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class PantallaPrincipalController implements Initializable {

    @FXML private StackPane contenedorPrincipal;
    @FXML private Label lblTituloPantalla;
    @FXML private Label lblUsuario;
    @FXML private Label lblCargo;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblBadgeEstado;

    // Botones del menú - TODOS
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
    @FXML private Button btnMedicamentos;
    @FXML private Button btnRecetas;
    @FXML private Button btnPerdidas;
    @FXML private Button btnCuentasPago;
    @FXML private Button btnCatalogo;
    @FXML private Button btnHistorialReclamaciones;
    @FXML private Button btnNominas;
    @FXML private Button btnInicio;
    @FXML private Button btnCerrarSesion;

    private Map<Button, String[]> mapaPantallas = new HashMap<>();
    private Map<String, List<String>> permisosPorCargo = new HashMap<>();
    private Button botonActivo = null;
    private Map<String, Parent> vistasCache = new HashMap<>();
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarPermisos();
        configurarMapaPantallas();
        aplicarControlDeAcceso();
        aplicarEstilosBotones();
        try {
            conexion = new ConexionBD().EstablecerConexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cargarDashboard();
        mostrarInformacionUsuario();
        configurarBadgePorRol();
    }

    private void configurarPermisos() {
            // ADMINISTRADOR - Acceso Total
            permisosPorCargo.put("Administrador", Arrays.asList(
                    "Usuarios", "Clientes", "Ventas", "Productos", "Compras", "Empleados",
                    "Proveedores", "Pedidos", "Pagos", "Envios", "Devoluciones",
                    "Fidelizacion", "Reclamaciones", "OrdenCompra", "Convenios",
                    "Medicamentos", "Recetas", "Perdidas", "CuentasPago", "HistorialReclamaciones",
                    "Nominas"
            ));

            // FARMACÉUTICO (1 persona) - Enfoque clínico y ventas
            permisosPorCargo.put("Farmaceutico", Arrays.asList(
                    "Clientes",        // Para fidelización
                    "Ventas",          // Registrar ventas de medicamentos
                    "Productos",       // Ver stock de medicamentos
                    "Pedidos",         // Solicitar medicamentos faltantes
                    "Medicamentos",    // Gestionar inventario de medicamentos
                    "Recetas",         // Validar recetas médicas
                    "Fidelizacion",    // Programa de puntos/fidelidad
                    "Reclamaciones"    // Atención a quejas de clientes
            ));

            // AUXILIAR (1-2 personas) - Apoyo en mostrador
            permisosPorCargo.put("Auxiliar", Arrays.asList(
                    "Clientes",        // Crear/buscar clientes
                    "Ventas",          // Registrar ventas
                    "Productos",       // Consultar precios y stock
                    "Medicamentos",    // Consultar medicamentos
                    "Pedidos",         // Ver pedidos pendientes
                    "Catalogo"         // Ver catálogo de productos
            ));

            // CAJERO (1 persona - puede ser compartido con Auxiliar)
            permisosPorCargo.put("Cajero", Arrays.asList(
                    "Clientes",        // Buscar cliente para factura
                    "Ventas",          // Registrar venta y cobrar
                    "CuentasPago",     // Procesar pagos (efectivo/tarjeta)
                    "Pagos",           // Historial de pagos
                    "Devoluciones"     // Procesar devoluciones con ticket
            ));

            // DELIVERY (1 persona)
            permisosPorCargo.put("Delivery", Arrays.asList(
                    "Clientes",        // Ver dirección del cliente
                    "Pedidos",         // Ver pedidos asignados
                    "Envios",          // Gestionar entregas
                    "Ventas"           // Solo consulta de ventas
            ));
    }

    private void aplicarControlDeAcceso() {
        String cargoUsuario = SesionUsuario.getInstancia().getCargoUsuario();
        List<String> permisos = permisosPorCargo.getOrDefault(cargoUsuario, new ArrayList<>());

        // Aplicar visibilidad según permisos para TODOS los botones
        setButtonVisibility(btnUsuarios, permisos.contains("Usuarios"));
        setButtonVisibility(btnCatalogo, true); // Todos pueden ver catálogo
        setButtonVisibility(btnCatalogo, true);
        setButtonVisibility(btnClientes, permisos.contains("Clientes"));
        setButtonVisibility(btnVentas, permisos.contains("Ventas"));
        setButtonVisibility(btnProductos, permisos.contains("Productos"));
        setButtonVisibility(btnCompras, permisos.contains("Compras"));
        setButtonVisibility(btnEmpleados, permisos.contains("Empleados"));
        setButtonVisibility(btnProveedores, permisos.contains("Proveedores"));
        setButtonVisibility(btnPedidos, permisos.contains("Pedidos"));
        setButtonVisibility(btnPagos, permisos.contains("Pagos"));
        setButtonVisibility(btnEnvios, permisos.contains("Envios"));
        setButtonVisibility(btnDevoluciones, permisos.contains("Devoluciones"));
        setButtonVisibility(btnFidelizacion, permisos.contains("Fidelizacion"));
        setButtonVisibility(btnReclamaciones, permisos.contains("Reclamaciones"));
        setButtonVisibility(btnOrdenCompra, permisos.contains("OrdenCompra"));
        setButtonVisibility(btnConvenios, permisos.contains("Convenios"));
        setButtonVisibility(btnMedicamentos, permisos.contains("Medicamentos"));
        setButtonVisibility(btnRecetas, permisos.contains("Recetas"));
        setButtonVisibility(btnPerdidas, permisos.contains("Perdidas"));
        setButtonVisibility(btnCuentasPago, permisos.contains("CuentasPago"));
        setButtonVisibility(btnHistorialReclamaciones, permisos.contains("HistorialReclamaciones"));
        setButtonVisibility(btnNominas, permisos.contains("Nominas"));
    }

    private void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
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
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #1A4F6E; -fx-background-color: #E3F2FD; -fx-padding: 5 14; -fx-background-radius: 20;");
                    break;
                case "Cajero":
                    lblBadgeEstado.setText("💰 Cajero");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #E65100; -fx-background-color: #FFF3E0; -fx-padding: 5 14; -fx-background-radius: 20;");
                    break;
                case "Farmacéutico":
                    lblBadgeEstado.setText("💊 Farmacéutico");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #2E7D32; -fx-background-color: #E8F5E9; -fx-padding: 5 14; -fx-background-radius: 20;");
                    break;
                case "Almacenista":
                    lblBadgeEstado.setText("📦 Almacenista");
                    lblBadgeEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #1565C0; -fx-background-color: #E3F2FD; -fx-padding: 5 14; -fx-background-radius: 20;");
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
        mapaPantallas.put(btnReclamaciones, new String[]{"Gestión de Reclamaciones", "/RegistroReclamacion.fxml"});
        mapaPantallas.put(btnOrdenCompra, new String[]{"Orden de Compra", "/OrdenCompra.fxml"});
        mapaPantallas.put(btnConvenios, new String[]{"Gestión de Convenios", "/RegistroConvenio.fxml"});
        mapaPantallas.put(btnMedicamentos, new String[]{"Gestión de Medicamentos", "/RegistroMedicamento.fxml"});
        mapaPantallas.put(btnRecetas, new String[]{"Gestión de Recetas Médicas", "/RecetaMedica.fxml"});
        mapaPantallas.put(btnPerdidas, new String[]{"Registro de Pérdidas", "/RegistroPerdida.fxml"});
        mapaPantallas.put(btnCuentasPago, new String[]{"Cuentas de Pago", "/CuentaPago.fxml"});
        mapaPantallas.put(btnHistorialReclamaciones, new String[]{"Historial de Reclamaciones", "/HistorialReclamacion.fxml"});
        mapaPantallas.put(btnCatalogo, new String[]{"Catálogo de Productos", "/CatalogoProductos.fxml"});
        mapaPantallas.put(btnNominas, new String[]{"Registro de Nómina", "/RegistroNomina.fxml"});

    }

    private void aplicarEstilosBotones() {
        for (Button btn : mapaPantallas.keySet()) {
            if (btn != null) {
                btn.getStyleClass().add("menu-button");
                btn.setOnMouseEntered(e -> {
                    if (botonActivo != btn) {
                        btn.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                    }
                });
                btn.setOnMouseExited(e -> {
                    if (botonActivo != btn) {
                        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: normal; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
                    }
                });
            }
        }
    }

    private void marcarBotonActivo(Button btnActivo) {
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: normal; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
        botonActivo = btnActivo;
        if (botonActivo != null) {
            botonActivo.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 16;");
        }
    }

    private void cargarPantalla(String titulo, String rutaFXML) {
        try {
            lblTituloPantalla.setText(titulo);

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

    private void cargarDashboard() {
        lblTituloPantalla.setText("Inicio");

        String nombreUsuario = "Usuario";
        if (SesionUsuario.getInstancia().isSesionActiva()) {
            nombreUsuario = SesionUsuario.getInstancia().getNombreUsuario();
        }

        VBox dashboard = new VBox(20);
        dashboard.setStyle("-fx-padding: 20; -fx-background-color: #F4F7FC;");

        Label lblBienvenida = new Label("¡Bienvenido, " + nombreUsuario + "!");
        lblBienvenida.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A4F6E;");

        Label lblSubtitulo = new Label("Resumen general del sistema");
        lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A9FBB;");

        VBox headerBox = new VBox(5, lblBienvenida, lblSubtitulo);
        headerBox.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,80,160,0.07), 8, 0, 0, 2);");

        // Grid de tarjetas 3x2
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-padding: 0;");

        // Obtener métricas
        Map<String, String> metricas = obtenerMetricasDashboard();

        // Tarjeta 1: Ventas Hoy
        VBox card1 = crearCard("💰", "Ventas Hoy", metricas.get("ventasHoy"), "Total de ventas registradas hoy", "#00897B", "#E0F2F1");
        grid.add(card1, 0, 0);

        // Tarjeta 2: Ventas del Mes
        VBox card2 = crearCard("📊", "Ventas del Mes", metricas.get("ventasMes"), "Total de ventas del mes actual", "#1A6BAD", "#E3F2FD");
        grid.add(card2, 1, 0);

        // Tarjeta 3: Productos con Stock Bajo
        VBox card3 = crearCard("⚠️", "Stock Bajo", metricas.get("stockBajo"), "Productos por debajo del mínimo", "#E65100", "#FFF3E0");
        grid.add(card3, 2, 0);

        // Tarjeta 4: Órdenes Pendientes
        VBox card4 = crearCard("📄", "Órdenes Pendientes", metricas.get("ordenesPendientes"), "Órdenes de compra sin procesar", "#F57F17", "#FFF8E1");
        grid.add(card4, 0, 1);

        // Tarjeta 5: Devoluciones del Mes
        VBox card5 = crearCard("🔄", "Devoluciones del Mes", metricas.get("devolucionesMes"), "Devoluciones registradas este mes", "#C62828", "#FFEBEE");
        grid.add(card5, 1, 1);

        // Tarjeta 6: Total Clientes
        VBox card6 = crearCard("👥", "Total Clientes", metricas.get("totalClientes"), "Clientes registrados en el sistema", "#2E7D32", "#E8F5E9");
        grid.add(card6, 2, 1);

        dashboard.getChildren().addAll(headerBox, grid);
        contenedorPrincipal.getChildren().clear();
        contenedorPrincipal.getChildren().add(dashboard);

        if (botonActivo != null) marcarBotonActivo(null);
    }

    private Map<String, String> obtenerMetricasDashboard() {
        Map<String, String> metricas = new HashMap<>();
        metricas.put("ventasHoy", "0");
        metricas.put("ventasMes", "0");
        metricas.put("stockBajo", "0");
        metricas.put("ordenesPendientes", "0");
        metricas.put("devolucionesMes", "0");
        metricas.put("totalClientes", "0");

        if (conexion == null) return metricas;

        try {
            String sqlVentasHoy = "SELECT COUNT(*) AS total, ISNULL(SUM(total), 0) AS monto FROM tbl_VENTA WHERE CAST(fecha AS DATE) = CAST(GETDATE() AS DATE)";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlVentasHoy)) {
                if (rs.next()) {
                    metricas.put("ventasHoy", rs.getInt("total") + " | RD$ " + String.format("%.2f", rs.getBigDecimal("monto")));
                }
            }

            String sqlVentasMes = "SELECT COUNT(*) AS total, ISNULL(SUM(total), 0) AS monto FROM tbl_VENTA WHERE MONTH(fecha) = MONTH(GETDATE()) AND YEAR(fecha) = YEAR(GETDATE())";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlVentasMes)) {
                if (rs.next()) {
                    metricas.put("ventasMes", rs.getInt("total") + " | RD$ " + String.format("%.2f", rs.getBigDecimal("monto")));
                }
            }

            String sqlStockBajo = "SELECT COUNT(*) AS total FROM tbl_PRODUCTO WHERE stock_actual <= stock_minimo";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlStockBajo)) {
                if (rs.next()) {
                    metricas.put("stockBajo", String.valueOf(rs.getInt("total")));
                }
            }

            String sqlOrdenesPend = "SELECT COUNT(*) AS total FROM tbl_ORDEN_COMPRA WHERE estado = 'PENDIENTE'";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlOrdenesPend)) {
                if (rs.next()) {
                    metricas.put("ordenesPendientes", String.valueOf(rs.getInt("total")));
                }
            }

            String sqlDevolucionesMes = "SELECT COUNT(*) AS total, ISNULL(SUM(monto_devuelto), 0) AS monto FROM tbl_DEVOLUCION WHERE MONTH(fecha) = MONTH(GETDATE()) AND YEAR(fecha) = YEAR(GETDATE())";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlDevolucionesMes)) {
                if (rs.next()) {
                    metricas.put("devolucionesMes", rs.getInt("total") + " | RD$ " + String.format("%.2f", rs.getBigDecimal("monto")));
                }
            }

            String sqlClientes = "SELECT COUNT(*) AS total FROM tbl_CLIENTE WHERE estado = 1";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sqlClientes)) {
                if (rs.next()) {
                    metricas.put("totalClientes", String.valueOf(rs.getInt("total")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metricas;
    }

    private VBox crearCard(String icono, String titulo, String valor, String descripcion, String colorAccent, String colorBg) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-padding: 22; -fx-effect: dropshadow(gaussian, rgba(0,80,160,0.07), 8, 0, 0, 2); -fx-border-color: " + colorBg.replace("#", "") + "; -fx-border-radius: 14; -fx-border-width: 2;");
        card.setPrefWidth(280);
        card.setPrefHeight(140);

        HBox iconTitleRow = new HBox(10);
        iconTitleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 28px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #455A64;");

        iconTitleRow.getChildren().addAll(lblIcono, lblTitulo);

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + colorAccent + ";");

        Label lblDesc = new Label(descripcion);
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #90A4AE;");

        card.getChildren().addAll(iconTitleRow, lblValor, lblDesc);
        return card;
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
    @FXML private void abrirClientes() { abrirPantalla("Clientes"); }
    @FXML private void abrirCatalogo() { abrirPantalla("Catalogo"); }
    @FXML private void abrirUsuarios() { abrirPantalla("Usuarios"); }
    @FXML private void abrirVentas() { abrirPantalla("Ventas"); }
    @FXML private void abrirProductos() { abrirPantalla("Productos"); }
    @FXML private void abrirCompras() { abrirPantalla("Compras"); }
    @FXML private void abrirEmpleados() { abrirPantalla("Empleados"); }
    @FXML private void abrirProveedores() { abrirPantalla("Proveedores"); }
    @FXML private void abrirPedidos() { abrirPantalla("Pedidos"); }
    @FXML private void abrirPagos() { abrirPantalla("Pagos"); }
    @FXML private void abrirEnvios() { abrirPantalla("Envios"); }
    @FXML private void abrirDevoluciones() { abrirPantalla("Devoluciones"); }
    @FXML private void abrirFidelizacion() { abrirPantalla("Fidelizacion"); }
    @FXML private void abrirReclamaciones() { abrirPantalla("Reclamaciones"); }
    @FXML private void abrirOrdenCompra() { abrirPantalla("OrdenCompra"); }
    @FXML private void abrirConvenios() { abrirPantalla("Convenios"); }
    @FXML private void abrirMedicamentos() { abrirPantalla("Medicamentos"); }
    @FXML private void abrirRecetas() { abrirPantalla("Recetas"); }
    @FXML private void abrirPerdidas() { abrirPantalla("Perdidas"); }
    @FXML private void abrirCuentasPago() { abrirPantalla("CuentasPago"); }
    @FXML private void abrirHistorialReclamaciones() { abrirPantalla("HistorialReclamaciones"); }
    @FXML private void abrirNominas() { abrirPantalla("Nominas"); }



    @FXML
    private void irAInicio() {
        cargarDashboard();
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
            case "Catalogo": btn = btnCatalogo; break;
            case "Envios": btn = btnEnvios; break;
            case "Devoluciones": btn = btnDevoluciones; break;
            case "Fidelizacion": btn = btnFidelizacion; break;
            case "Reclamaciones": btn = btnReclamaciones; break;
            case "OrdenCompra": btn = btnOrdenCompra; break;
            case "Convenios": btn = btnConvenios; break;
            case "Medicamentos": btn = btnMedicamentos; break;
            case "Recetas": btn = btnRecetas; break;
            case "Perdidas": btn = btnPerdidas; break;
            case "CuentasPago": btn = btnCuentasPago; break;
            case "HistorialReclamaciones": btn = btnHistorialReclamaciones; break;
            case "Nominas": btn = btnNominas; break;
            default: break;
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