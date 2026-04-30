package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Venta;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroVentaController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Integer> colIdCliente;
    @FXML private TableColumn<Venta, Integer> colIdEmpleado;
    @FXML private TableColumn<Venta, Integer> colIdComprobante;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, BigDecimal> colSubtotal;
    @FXML private TableColumn<Venta, BigDecimal> colDescuento;
    @FXML private TableColumn<Venta, BigDecimal> colItbis;
    @FXML private TableColumn<Venta, BigDecimal> colTotal;
    @FXML private TableColumn<Venta, String> colEstado;
    @FXML private TableColumn<Venta, String> colProducto;

    // Formulario
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbProducto;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotal;

    // Botones
    @FXML private Button btnConsultar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;
    @FXML private Button btnVentaConSeguro;

    private Connection conexion;
    private int idVentaSeleccionada = 0;
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarClientes();
            cargarEmpleados();
            cargarComprobantes();
            cargarProductos();

            cmbEstado.setItems(FXCollections.observableArrayList(
                    "COMPLETADA", "PENDIENTE", "ANULADA"));
            cmbEstado.setValue("PENDIENTE");

            dateFecha.setValue(LocalDate.now());

            configurarTabla();
            configurarSeleccionTabla();
            configurarBotonesPorRol();

            // Inicialmente el panel de consulta está oculto
            consultaPanel.setVisible(false);
            consultaPanel.setManaged(false);

            // Botones de edición deshabilitados al inicio
            habilitarBotonesEdicion(false);
            btnGuardar.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();

        boolean puedeEditar = false;
        boolean puedeEliminar = false;

        switch (rol) {
            case "Administrador":
                puedeEditar = true;
                puedeEliminar = true;
                break;
            case "Cajero":
                puedeEditar = false;
                puedeEliminar = false;
                break;
            default:
                puedeEditar = false;
                puedeEliminar = false;
                break;
        }

        btnEditar.setVisible(puedeEditar);
        btnEditar.setManaged(puedeEditar);
        btnEliminar.setVisible(puedeEliminar);
        btnEliminar.setManaged(puedeEliminar);
    }

    private void habilitarBotonesEdicion(boolean habilitar) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();

        if (!"Administrador".equals(rol)) {
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
            return;
        }

        btnEditar.setDisable(!habilitar);
        btnEliminar.setDisable(!habilitar);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colIdEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colIdComprobante.setCellValueFactory(new PropertyValueFactory<>("idComprobante"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colItbis.setCellValueFactory(new PropertyValueFactory<>("itbis"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        tblVentas.setItems(listaVentas);
    }

    private void configurarSeleccionTabla() {
        tblVentas.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        cargarDatosEnFormulario(newVal);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                });
    }

    private void cargarDatosEnFormulario(Venta v) {
        idVentaSeleccionada = v.getIdVenta();

        // Cliente
        if (v.getIdCliente() != null) {
            cmbCliente.getItems().stream()
                    .filter(s -> s.startsWith(v.getIdCliente() + " - "))
                    .findFirst().ifPresent(cmbCliente::setValue);
        } else {
            cmbCliente.setValue(null);
        }

        // Empleado
        cmbEmpleado.getItems().stream()
                .filter(s -> s.startsWith(v.getIdEmpleado() + " - "))
                .findFirst().ifPresent(cmbEmpleado::setValue);

        // Comprobante
        if (v.getIdComprobante() != null && v.getIdComprobante() > 0) {
            cmbComprobante.getItems().stream()
                    .filter(s -> s.startsWith(v.getIdComprobante() + " - "))
                    .findFirst().ifPresent(cmbComprobante::setValue);
        } else {
            cmbComprobante.setValue("NINGUNO");
        }

        // Producto
        if (v.getIdProducto() != null && v.getIdProducto() > 0) {
            cmbProducto.getItems().stream()
                    .filter(s -> s.startsWith(v.getIdProducto() + " - "))
                    .findFirst().ifPresent(cmbProducto::setValue);
        } else {
            cmbProducto.setValue("NINGUNO");
        }

        dateFecha.setValue(v.getFecha() != null ? v.getFecha().toLocalDate() : LocalDate.now());
        txtSubtotal.setText(v.getSubtotal() != null ? v.getSubtotal().toPlainString() : "");
        txtDescuento.setText(v.getDescuento() != null ? v.getDescuento().toPlainString() : "");
        txtItbis.setText(v.getItbis() != null ? v.getItbis().toPlainString() : "");
        txtTotal.setText(v.getTotal() != null ? v.getTotal().toPlainString() : "");
        cmbEstado.setValue(v.getEstado() != null ? v.getEstado() : "PENDIENTE");
    }

    // ── Carga de combos ──────────────────────────────────────────

    private void cargarClientes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_cliente, nombres FROM tbl_CLIENTE WHERE estado = 1 ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(rs.getInt("id_cliente") + " - " + rs.getString("nombres"));
            cmbCliente.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            cmbEmpleado.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarComprobantes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNO");
        String sql = "SELECT id_comprobante, ncf FROM tbl_COMPROBANTE_FISCAL ORDER BY ncf";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(rs.getInt("id_comprobante") + " - " + rs.getString("ncf"));
        } catch (SQLException e) {
            System.out.println("Nota: tabla tbl_COMPROBANTE_FISCAL no disponible: " + e.getMessage());
        }
        cmbComprobante.setItems(lista);
        cmbComprobante.setValue("NINGUNO");
    }

    private void cargarProductos() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNO");
        String sql = "SELECT id_producto, nombre FROM tbl_PRODUCTO ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(rs.getInt("id_producto") + " - " + rs.getString("nombre"));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        cmbProducto.setItems(lista);
        cmbProducto.setValue("NINGUNO");
    }

    // ── Consulta ─────────────────────────────────────────────────

    @FXML
    private void abrirConsulta() {
        consultaPanel.setVisible(true);
        consultaPanel.setManaged(true);
        cargarVentas();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void cargarVentas() {
        listaVentas.clear();
        String sql = "SELECT v.*, p.nombre AS nombre_producto " +
                "FROM tbl_VENTA v " +
                "LEFT JOIN tbl_PRODUCTO p ON v.id_producto = p.id_producto " +
                "ORDER BY v.id_venta DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) listaVentas.add(mapear(rs));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar ventas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarVenta() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarVentas(); return; }

        listaVentas.clear();
        String sql = "SELECT v.*, p.nombre AS nombre_producto " +
                "FROM tbl_VENTA v " +
                "LEFT JOIN tbl_PRODUCTO p ON v.id_producto = p.id_producto " +
                "WHERE CAST(v.id_venta AS CHAR) LIKE ? " +
                "   OR CAST(v.id_cliente AS CHAR) LIKE ? " +
                "   OR p.nombre LIKE ? " +
                "   OR v.estado LIKE ? " +
                "ORDER BY v.id_venta DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like); ps.setString(2, like);
            ps.setString(3, like); ps.setString(4, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) listaVentas.add(mapear(rs));
            if (listaVentas.isEmpty())
                mostrarAlerta("Sin resultados",
                        "No se encontraron ventas para: " + filtro, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarVentas();
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("id_venta"));
        v.setIdCliente(rs.getObject("id_cliente") != null ? rs.getInt("id_cliente") : null);
        v.setIdEmpleado(rs.getInt("id_empleado"));
        v.setIdComprobante(rs.getObject("id_comprobante") != null ? rs.getInt("id_comprobante") : null);
        v.setIdProducto(rs.getObject("id_producto") != null ? rs.getInt("id_producto") : null);
        v.setNombreProducto(rs.getString("nombre_producto"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) v.setFecha(ts.toLocalDateTime());
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setDescuento(rs.getBigDecimal("descuento"));
        v.setItbis(rs.getBigDecimal("itbis"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setEstado(rs.getString("estado"));
        return v;
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @FXML
    private void guardarVenta() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar ventas", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_VENTA " +
                "(id_cliente, id_empleado, id_comprobante, id_producto, " +
                " fecha, subtotal, descuento, itbis, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Venta registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarVenta() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar ventas", Alert.AlertType.ERROR);
            return;
        }

        if (idVentaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una venta de la tabla para editar.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setHeaderText(null);
        conf.setContentText("¿Desea guardar los cambios en esta venta?");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        String sql = "UPDATE tbl_VENTA " +
                "SET id_cliente=?, id_empleado=?, id_comprobante=?, id_producto=?, " +
                "    fecha=?, subtotal=?, descuento=?, itbis=?, total=?, estado=? " +
                "WHERE id_venta=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(11, idVentaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Venta actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarVenta() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar ventas", Alert.AlertType.ERROR);
            return;
        }

        if (idVentaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una venta de la tabla para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea eliminar esta venta?\nEsta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM tbl_VENTA WHERE id_venta=?")) {
            ps.setInt(1, idVentaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Venta eliminada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la venta tiene registros asociados.",
                    Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        // 1 id_cliente
        if (cmbCliente.getValue() != null)
            ps.setInt(1, Integer.parseInt(cmbCliente.getValue().split(" - ")[0]));
        else
            ps.setNull(1, Types.INTEGER);

        // 2 id_empleado
        ps.setInt(2, Integer.parseInt(cmbEmpleado.getValue().split(" - ")[0]));

        // 3 id_comprobante
        String compVal = cmbComprobante.getValue();
        if (compVal != null && !compVal.equals("NINGUNO"))
            ps.setInt(3, Integer.parseInt(compVal.split(" - ")[0]));
        else
            ps.setNull(3, Types.INTEGER);

        // 4 id_producto
        String prodVal = cmbProducto.getValue();
        if (prodVal != null && !prodVal.equals("NINGUNO"))
            ps.setInt(4, Integer.parseInt(prodVal.split(" - ")[0]));
        else
            ps.setNull(4, Types.INTEGER);

        // 5 fecha
        ps.setTimestamp(5, Timestamp.valueOf(
                LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));

        // 6 subtotal
        ps.setBigDecimal(6, new BigDecimal(txtSubtotal.getText().trim()));

        // 7 descuento
        String desc = txtDescuento.getText().trim();
        ps.setBigDecimal(7, desc.isEmpty() ? BigDecimal.ZERO : new BigDecimal(desc));

        // 8 itbis
        String itbis = txtItbis.getText().trim();
        ps.setBigDecimal(8, itbis.isEmpty() ? BigDecimal.ZERO : new BigDecimal(itbis));

        // 9 total
        ps.setBigDecimal(9, new BigDecimal(txtTotal.getText().trim()));

        // 10 estado
        ps.setString(10, cmbEstado.getValue());
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idVentaSeleccionada = 0;
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbComprobante.setValue("NINGUNO");
        cmbProducto.setValue("NINGUNO");
        cmbEstado.setValue("PENDIENTE");
        dateFecha.setValue(LocalDate.now());
        txtSubtotal.clear();
        txtDescuento.clear();
        txtItbis.clear();
        txtTotal.clear();
        modoEdicion = false;

        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);

        tblVentas.getSelectionModel().clearSelection();
    }


    private boolean validarCampos() {
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el empleado.", Alert.AlertType.WARNING);
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha.", Alert.AlertType.WARNING);
            return false;
        }
        if (!esDecimalPositivo(txtSubtotal.getText())) {
            mostrarAlerta("Validación", "Ingrese un subtotal válido (mayor a 0).", Alert.AlertType.WARNING);
            txtSubtotal.requestFocus();
            return false;
        }
        if (!esDecimalNoNegativo(txtTotal.getText())) {
            mostrarAlerta("Validación", "Ingrese un total válido.", Alert.AlertType.WARNING);
            txtTotal.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el estado.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean esDecimalPositivo(String txt) {
        if (txt == null || txt.trim().isEmpty()) return false;
        try {
            return new BigDecimal(txt.trim()).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean esDecimalNoNegativo(String txt) {
        if (txt == null || txt.trim().isEmpty()) return false;
        try {
            return new BigDecimal(txt.trim()).compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void abrirVentaConSeguro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistroVentaConSeguro.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Venta con Seguro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (consultaPanel.isVisible()) {
                cargarVentas();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de venta con seguro", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}