package org.example.proyecto.Controladores;

import javafx.beans.property.SimpleStringProperty;
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
import org.example.proyecto.Modelos.DetalleVenta;
import org.example.proyecto.Modelos.Venta;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;
import org.example.proyecto.util.ReportUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
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
    @FXML private TableColumn<Venta, BigDecimal> colMontoPagado;
    @FXML private TableColumn<Venta, BigDecimal> colSaldoPendiente;
    @FXML private TableColumn<Venta, String> colEstadoPago;
    @FXML private TableColumn<Venta, String> colEstado;

    // Formulario
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotal;
    @FXML private TextField txtMontoPagado;
    @FXML private TextField txtSaldoPendiente;
    @FXML private ComboBox<String> cmbEstadoPago;

    // Campos para detalles de venta
    @FXML private ComboBox<String> cmbDetalleProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtDetalleDescuento;
    @FXML private TextField txtLote;
    @FXML private DatePicker dateFechaVencimiento;
    @FXML private TableView<DetalleVenta> tblDetalles;
    @FXML private TableColumn<DetalleVenta, String> colProdNombre;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, BigDecimal> colPrecioUnit;
    @FXML private TableColumn<DetalleVenta, BigDecimal> colDetalleDescuento;
    @FXML private TableColumn<DetalleVenta, BigDecimal> colSubtotalDetalle;
    @FXML private TableColumn<DetalleVenta, BigDecimal> colItbisDetalle;
    @FXML private TableColumn<DetalleVenta, String> colLoteDetalle;
    @FXML private TableColumn<DetalleVenta, LocalDate> colFechaVencDetalle;

    // Botones
    @FXML private Button btnConsultar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;
    @FXML private Button btnVentaConSeguro;
    @FXML private Button btnAgregarDetalle;
    @FXML private Button btnQuitarDetalle;

    private Connection conexion;
    private int idVentaSeleccionada = 0;
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private final ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarClientes();
            cargarEmpleados();
            cargarComprobantes();
            cargarProductosDetalle();

            cmbEstado.setItems(FXCollections.observableArrayList(
                    "COMPLETADA", "PENDIENTE", "ANULADA"));
            cmbEstado.setValue("PENDIENTE");

            cmbEstadoPago.setItems(FXCollections.observableArrayList(
                    "PAGADO", "PENDIENTE", "PARCIAL"));
            cmbEstadoPago.setValue("PAGADO");

            dateFecha.setValue(LocalDate.now());

            configurarTabla();
            configurarTablaDetalles();
            configurarSeleccionTabla();
            configurarBotonesPorRol();
            configurarSeleccionProductoDetalle();
            configurarCalculoSaldoPendiente();

            consultaPanel.setVisible(false);
            consultaPanel.setManaged(false);

            habilitarBotonesEdicion(false);
            btnGuardar.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionProductoDetalle() {
        cmbDetalleProducto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("NINGUNO")) {
                cargarDatosProductoDetalle(newVal);
            } else {
                txtPrecioUnitario.clear();
                txtLote.clear();
                dateFechaVencimiento.setValue(null);
            }
        });
    }

    private void cargarDatosProductoDetalle(String productoSeleccionado) {
        try {
            int idProducto = Integer.parseInt(productoSeleccionado.split(" - ")[0]);
            String sql = "SELECT precio_venta FROM tbl_PRODUCTO WHERE id_producto = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtPrecioUnitario.setText(rs.getBigDecimal("precio_venta").toPlainString());
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        boolean puedeEditar = rol.equals("Administrador");
        boolean puedeEliminar = rol.equals("Administrador");

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
        colMontoPagado.setCellValueFactory(new PropertyValueFactory<>("montoPagado"));
        colSaldoPendiente.setCellValueFactory(new PropertyValueFactory<>("saldoPendiente"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblVentas.setItems(listaVentas);
    }

    private void configurarTablaDetalles() {
        colProdNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerNombreProducto(cellData.getValue().getIdProducto())));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnit.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colDetalleDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colSubtotalDetalle.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colItbisDetalle.setCellValueFactory(new PropertyValueFactory<>("itbis"));
        colLoteDetalle.setCellValueFactory(new PropertyValueFactory<>("lote"));
        // colFechaVencDetalle.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento")); // COMENTA ESTA LÍNEA
        tblDetalles.setItems(listaDetalles);
    }

    private String obtenerNombreProducto(int idProducto) {
        try {
            String sql = "SELECT nombre FROM tbl_PRODUCTO WHERE id_producto = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Desconocido";
    }

    private void configurarSeleccionTabla() {
        tblVentas.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        cargarDatosEnFormulario(newVal);
                        cargarDetallesVenta(newVal.getIdVenta());
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                });
    }

    private void cargarDatosEnFormulario(Venta v) {
        idVentaSeleccionada = v.getIdVenta();

        if (v.getIdCliente() != null) {
            cmbCliente.getItems().stream()
                    .filter(s -> s.startsWith(v.getIdCliente() + " - "))
                    .findFirst().ifPresent(cmbCliente::setValue);
        } else {
            cmbCliente.setValue(null);
        }

        cmbEmpleado.getItems().stream()
                .filter(s -> s.startsWith(v.getIdEmpleado() + " - "))
                .findFirst().ifPresent(cmbEmpleado::setValue);

        if (v.getIdComprobante() != null && v.getIdComprobante() > 0) {
            cmbComprobante.getItems().stream()
                    .filter(s -> s.startsWith(v.getIdComprobante() + " - "))
                    .findFirst().ifPresent(cmbComprobante::setValue);
        } else {
            cmbComprobante.setValue("NINGUNO");
        }

        dateFecha.setValue(v.getFecha() != null ? v.getFecha().toLocalDate() : LocalDate.now());
        txtSubtotal.setText(v.getSubtotal() != null ? v.getSubtotal().toPlainString() : "");
        txtDescuento.setText(v.getDescuento() != null ? v.getDescuento().toPlainString() : "");
        txtItbis.setText(v.getItbis() != null ? v.getItbis().toPlainString() : "");
        txtTotal.setText(v.getTotal() != null ? v.getTotal().toPlainString() : "");

        txtMontoPagado.setText(v.getMontoPagado() != null ? v.getMontoPagado().toPlainString() : "");
        txtSaldoPendiente.setText(v.getSaldoPendiente() != null ? v.getSaldoPendiente().toPlainString() : "");
        cmbEstadoPago.setValue(v.getEstadoPago() != null ? v.getEstadoPago() : "PENDIENTE");

        cmbEstado.setValue(v.getEstado() != null ? v.getEstado() : "PENDIENTE");
    }

    private void cargarDetallesVenta(int idVenta) {
        listaDetalles.clear();
        String sql = "SELECT * FROM tbl_DETALLE_VENTA WHERE id_venta = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdDetVenta(rs.getInt("id_det_venta"));
                detalle.setIdVenta(rs.getInt("id_venta"));
                detalle.setIdProducto(rs.getInt("id_producto"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                detalle.setDescuento(rs.getBigDecimal("descuento"));
                detalle.setItbis(rs.getBigDecimal("itbis"));
                detalle.setSubtotal(rs.getBigDecimal("subtotal"));
                detalle.setLote(rs.getString("lote"));
                detalle.setFechaVencimiento(rs.getDate("fecha_vencimiento") != null ?
                        rs.getDate("fecha_vencimiento").toLocalDate() : null);
                listaDetalles.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private void cargarProductosDetalle() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_producto, nombre FROM tbl_PRODUCTO ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(rs.getInt("id_producto") + " - " + rs.getString("nombre"));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        cmbDetalleProducto.setItems(lista);
        cmbDetalleProducto.setValue(null);
    }

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
        String sql = "SELECT * FROM tbl_VENTA ORDER BY id_venta DESC";
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
        String sql = "SELECT v.* FROM tbl_VENTA v " +
                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
                "WHERE CAST(v.id_venta AS CHAR) LIKE ? " +
                "   OR CAST(v.id_cliente AS CHAR) LIKE ? " +
                "   OR c.nombres LIKE ? " +
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
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) v.setFecha(ts.toLocalDateTime());
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setDescuento(rs.getBigDecimal("descuento"));
        v.setItbis(rs.getBigDecimal("itbis"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setMontoPagado(rs.getBigDecimal("monto_pagado"));
        v.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        v.setEstadoPago(rs.getString("estado_pago"));
        v.setEstado(rs.getString("estado"));
        return v;
    }

    @FXML
    private void agregarDetalle() {
        if (cmbDetalleProducto.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un producto", Alert.AlertType.WARNING);
            return;
        }

        if (txtCantidad.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una cantidad", Alert.AlertType.WARNING);
            return;
        }

        try {
            int idProducto = Integer.parseInt(cmbDetalleProducto.getValue().split(" - ")[0]);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());
            BigDecimal descuento = txtDetalleDescuento.getText().trim().isEmpty() ?
                    BigDecimal.ZERO : new BigDecimal(txtDetalleDescuento.getText().trim());
            String lote = txtLote.getText().trim();


            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            BigDecimal itbis = subtotal.multiply(new BigDecimal("0.18"));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.setSubtotal(subtotal);
            detalle.setItbis(itbis);
            detalle.setLote(lote);


            listaDetalles.add(detalle);
            recalcularTotales();

            cmbDetalleProducto.setValue(null);
            txtCantidad.clear();
            txtPrecioUnitario.clear();
            txtDetalleDescuento.clear();
            txtLote.clear();
            dateFechaVencimiento.setValue(null);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad inválida", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void quitarDetalle() {
        DetalleVenta seleccionado = tblDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalles.remove(seleccionado);
            recalcularTotales();
        } else {
            mostrarAlerta("Advertencia", "Seleccione un detalle para eliminar", Alert.AlertType.WARNING);
        }
    }

    private void configurarCalculoSaldoPendiente() {
        txtMontoPagado.textProperty().addListener((obs, oldVal, newVal) -> {
            calcularSaldoPendiente();
        });
        txtTotal.textProperty().addListener((obs, oldVal, newVal) -> {
            calcularSaldoPendiente();
        });
    }

    private void calcularSaldoPendiente() {
        try {
            BigDecimal total = txtTotal.getText().trim().isEmpty() ?
                    BigDecimal.ZERO : new BigDecimal(txtTotal.getText().trim());
            BigDecimal montoPagado = txtMontoPagado.getText().trim().isEmpty() ?
                    BigDecimal.ZERO : new BigDecimal(txtMontoPagado.getText().trim());
            BigDecimal saldo = total.subtract(montoPagado);
            txtSaldoPendiente.setText(saldo.toPlainString());

            if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
                cmbEstadoPago.setValue("PAGADO");
            } else if (montoPagado.compareTo(BigDecimal.ZERO) > 0) {
                cmbEstadoPago.setValue("PARCIAL");
            } else {
                cmbEstadoPago.setValue("PENDIENTE");
            }
        } catch (NumberFormatException e) {
            // Ignorar mientras escribe
        }
    }

    private void recalcularTotales() {
        BigDecimal subtotalTotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal itbisTotal = BigDecimal.ZERO;

        for (DetalleVenta detalle : listaDetalles) {
            subtotalTotal = subtotalTotal.add(detalle.getSubtotal());
            descuentoTotal = descuentoTotal.add(detalle.getDescuento());
            itbisTotal = itbisTotal.add(detalle.getItbis());
        }

        BigDecimal total = subtotalTotal.subtract(descuentoTotal).add(itbisTotal);

        txtSubtotal.setText(subtotalTotal.toPlainString());
        txtDescuento.setText(descuentoTotal.toPlainString());
        txtItbis.setText(itbisTotal.toPlainString());
        txtTotal.setText(total.toPlainString());
    }

    @FXML
    private void guardarVenta() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar ventas", Alert.AlertType.ERROR);
            return;
        }

        if (listaDetalles.isEmpty()) {
            mostrarAlerta("Error", "Debe agregar al menos un producto a la venta", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        String sqlVenta = "INSERT INTO tbl_VENTA " +
                "(id_cliente, id_empleado, id_comprobante, fecha, " +
                " subtotal, descuento, itbis, total, monto_pagado, saldo_pendiente, estado_pago, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
            setearParametrosVenta(ps);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            int idVentaGenerada = 0;
            if (generatedKeys.next()) {
                idVentaGenerada = generatedKeys.getInt(1);
            }

            String sqlDetalle = "INSERT INTO tbl_DETALLE_VENTA " +
                    "(id_venta, id_producto, cantidad, precio_unitario, " +
                    " descuento, itbis, subtotal, lote, fecha_vencimiento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            for (DetalleVenta detalle : listaDetalles) {
                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, idVentaGenerada);
                    psDetalle.setInt(2, detalle.getIdProducto());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, detalle.getDescuento());
                    psDetalle.setBigDecimal(6, detalle.getItbis());
                    psDetalle.setBigDecimal(7, detalle.getSubtotal());
                    psDetalle.setString(8, detalle.getLote());
                    if (detalle.getFechaVencimiento() != null)
                        psDetalle.setDate(9, Date.valueOf(detalle.getFechaVencimiento()));
                    else
                        psDetalle.setNull(9, Types.DATE);
                    psDetalle.executeUpdate();
                }
            }

            mostrarAlerta("Éxito", "Venta registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarVentas();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        String sqlDeleteDetalles = "DELETE FROM tbl_DETALLE_VENTA WHERE id_venta = ?";
        try (PreparedStatement psDelete = conexion.prepareStatement(sqlDeleteDetalles)) {
            psDelete.setInt(1, idVentaSeleccionada);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlVenta = "UPDATE tbl_VENTA " +
                "SET id_cliente=?, id_empleado=?, id_comprobante=?, " +
                "    fecha=?, subtotal=?, descuento=?, itbis=?, total=?, " +
                "    monto_pagado=?, saldo_pendiente=?, estado_pago=?, estado=? " +
                "WHERE id_venta=?";
        try (PreparedStatement ps = conexion.prepareStatement(sqlVenta)) {
            setearParametrosVenta(ps);
            ps.setInt(13, idVentaSeleccionada);
            ps.executeUpdate();

            String sqlDetalle = "INSERT INTO tbl_DETALLE_VENTA " +
                    "(id_venta, id_producto, cantidad, precio_unitario, " +
                    " descuento, itbis, subtotal, lote, fecha_vencimiento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            for (DetalleVenta detalle : listaDetalles) {
                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, idVentaSeleccionada);
                    psDetalle.setInt(2, detalle.getIdProducto());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, detalle.getDescuento());
                    psDetalle.setBigDecimal(6, detalle.getItbis());
                    psDetalle.setBigDecimal(7, detalle.getSubtotal());
                    psDetalle.setString(8, detalle.getLote());
                    if (detalle.getFechaVencimiento() != null)
                        psDetalle.setDate(9, Date.valueOf(detalle.getFechaVencimiento()));
                    else
                        psDetalle.setNull(9, Types.DATE);
                    psDetalle.executeUpdate();
                }
            }

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
        conf.setTitle("Confirmar anulación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea anular esta venta?\nLa venta quedará marcada como ANULADA.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE tbl_VENTA SET estado = 'ANULADA' WHERE id_venta=?")) {
            ps.setInt(1, idVentaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Venta anulada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede anular la venta.",
                    Alert.AlertType.ERROR);
        }
    }

    private void setearParametrosVenta(PreparedStatement ps) throws SQLException {
        if (cmbCliente.getValue() != null)
            ps.setInt(1, Integer.parseInt(cmbCliente.getValue().split(" - ")[0]));
        else
            ps.setNull(1, Types.INTEGER);

        ps.setInt(2, Integer.parseInt(cmbEmpleado.getValue().split(" - ")[0]));

        String compVal = cmbComprobante.getValue();
        if (compVal != null && !compVal.equals("NINGUNO"))
            ps.setInt(3, Integer.parseInt(compVal.split(" - ")[0]));
        else
            ps.setNull(3, Types.INTEGER);

        ps.setTimestamp(4, Timestamp.valueOf(
                LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));

        ps.setBigDecimal(5, new BigDecimal(txtSubtotal.getText().trim()));

        String desc = txtDescuento.getText().trim();
        ps.setBigDecimal(6, desc.isEmpty() ? BigDecimal.ZERO : new BigDecimal(desc));

        String itbis = txtItbis.getText().trim();
        ps.setBigDecimal(7, itbis.isEmpty() ? BigDecimal.ZERO : new BigDecimal(itbis));

        ps.setBigDecimal(8, new BigDecimal(txtTotal.getText().trim()));

        String montoPagadoStr = txtMontoPagado.getText().trim();
        ps.setBigDecimal(9, montoPagadoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(montoPagadoStr));

        String saldoPendienteStr = txtSaldoPendiente.getText().trim();
        ps.setBigDecimal(10, saldoPendienteStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(saldoPendienteStr));

        ps.setString(11, cmbEstadoPago.getValue() != null ? cmbEstadoPago.getValue() : "PENDIENTE");

        ps.setString(12, cmbEstado.getValue());
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
        cmbEstado.setValue("PENDIENTE");
        dateFecha.setValue(LocalDate.now());
        txtSubtotal.clear();
        txtDescuento.clear();
        txtItbis.clear();
        txtTotal.clear();
        txtMontoPagado.clear();
        txtSaldoPendiente.clear();
        cmbEstadoPago.setValue("PAGADO");
        listaDetalles.clear();
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
            mostrarAlerta("Validación", "Ingrese un subtotal válido.", Alert.AlertType.WARNING);
            return false;
        }
        if (!esDecimalNoNegativo(txtTotal.getText())) {
            mostrarAlerta("Validación", "Ingrese un total válido.", Alert.AlertType.WARNING);
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

    @FXML
    private void generarReporte() {
        Venta seleccion = tblVentas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Seleccionar Venta", "Debe seleccionar una venta para generar el reporte.", Alert.AlertType.WARNING);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id_venta", seleccion.getIdVenta());
        ReportUtil.generarReporte("Ventas", "/reportes/ReporteVentas.jasper", params, conexion);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}