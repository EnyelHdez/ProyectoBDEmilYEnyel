package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Compra;
import org.example.proyecto.Modelos.DetalleCompra;
import org.example.proyecto.Modelos.Proveedor;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrdenCompraController implements Initializable {

    // ── Campos del formulario ──────────────────────────────────────
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private TextField txtProducto;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TextField txtPrecioEstimado;
    @FXML private TextField txtLote;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cmbEstado;

    // ── Tabla de compras (cabeceras) ──────────────────────────────
    @FXML private TableView<Compra> tblOrdenes;
    @FXML private TableColumn<Compra, Integer> colId;
    @FXML private TableColumn<Compra, Integer> colProveedor;
    @FXML private TableColumn<Compra, Integer> colEmpleado;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, BigDecimal> colSubtotal;
    @FXML private TableColumn<Compra, BigDecimal> colDescuento;
    @FXML private TableColumn<Compra, BigDecimal> colItbis;
    @FXML private TableColumn<Compra, BigDecimal> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;

    // ── Tabla de detalles temporales (productos a ordenar) ─────────
    @FXML private TableView<DetalleCompra> tblDetalles;
    @FXML private TableColumn<DetalleCompra, String> colDetalleProducto;
    @FXML private TableColumn<DetalleCompra, Integer> colDetalleCantidad;
    @FXML private TableColumn<DetalleCompra, Double> colDetallePrecio;
    @FXML private TableColumn<DetalleCompra, String> colDetalleLote;
    @FXML private TableColumn<DetalleCompra, Double> colDetalleSubtotal;

    @FXML private Label lblTotal;

    private int idCompraSeleccionada = 0;
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private final ObservableList<DetalleCompra> listaDetalles = FXCollections.observableArrayList();
    private final ObservableList<Compra> listaCompras = FXCollections.observableArrayList();
    private Connection conexion;
    private double totalOrden = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        // Configurar ComboBoxes
        cmbEstado.setItems(FXCollections.observableArrayList(
                "PENDIENTE", "APROBADA", "ENVIADA", "RECIBIDA", "ANULADA"
        ));
        dateFecha.setValue(LocalDate.now());

        configurarSpinner();
        configurarTablas();
        cargarProveedores();
        cargarEmpleados();
        cargarCompras();

        // Listener para selección en tabla de compras
        tblOrdenes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, sel) -> {
                    if (sel != null) {
                        idCompraSeleccionada = sel.getIdCompra();
                        cargarDetallesCompra(sel.getIdCompra());
                        rellenarFormularioCompra(sel);
                    }
                });
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1);
        spnCantidad.setValueFactory(valueFactory);
        spnCantidad.setEditable(true);
    }

    private void configurarTablas() {
        // Configurar tabla de compras
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colItbis.setCellValueFactory(new PropertyValueFactory<>("itbis"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar tabla de detalles
        colDetalleProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colDetalleCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetallePrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colDetalleLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colDetalleSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tblOrdenes.setItems(listaCompras);
        tblDetalles.setItems(listaDetalles);
    }

    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT id_proveedor, nombre_comercial, rnc, telefono FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY nombre_comercial";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombreComercial(rs.getString("nombre_comercial"));
                proveedor.setRnc(rs.getString("rnc"));
                proveedor.setTelefono(rs.getString("telefono"));
                listaProveedores.add(proveedor);
            }
            cmbProveedor.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> empleados = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO WHERE estado_temp = 'Activo' ORDER BY nombres";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            }
            cmbEmpleado.setItems(empleados);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarCompras() {
        listaCompras.clear();
        String sql = "SELECT * FROM tbl_COMPRA ORDER BY id_compra DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Compra compra = new Compra(
                        rs.getInt("id_compra"),
                        rs.getInt("id_proveedor"),
                        rs.getInt("id_empleado"),
                        rs.getObject("id_comprobante") != null ? rs.getInt("id_comprobante") : null,
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getString("nro_factura_prov"),
                        rs.getBigDecimal("subtotal"),
                        rs.getBigDecimal("descuento"),
                        rs.getBigDecimal("itbis"),
                        rs.getBigDecimal("total"),
                        rs.getString("estado_temp")
                );
                listaCompras.add(compra);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar compras: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarDetallesCompra(int idCompra) {
        listaDetalles.clear();
        String sql = "SELECT dc.*, p.nombre as nombre_producto " +
                "FROM tbl_DETALLE_COMPRA dc " +
                "LEFT JOIN tbl_PRODUCTO p ON dc.id_producto = p.id_producto " +
                "WHERE dc.id_det_compra = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetalleCompra detalle = new DetalleCompra(
                        rs.getInt("id_det_compra"),
                        rs.getInt("id_compra"),
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("descuento"),
                        rs.getString("lote"),
                        rs.getDouble("subtotal"),
                        rs.getString("fecha_vencimiento")
                );
                listaDetalles.add(detalle);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar detalles: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void rellenarFormularioCompra(Compra c) {
        // Seleccionar proveedor en ComboBox
        for (Proveedor p : listaProveedores) {
            if (p.getIdProveedor() == c.getIdProveedor()) {
                cmbProveedor.setValue(p);
                break;
            }
        }

        // Seleccionar empleado
        String empleadoStr = c.getIdEmpleado() + " - ";
        for (String e : cmbEmpleado.getItems()) {
            if (e.startsWith(empleadoStr)) {
                cmbEmpleado.setValue(e);
                break;
            }
        }

        dateFecha.setValue(c.getFecha() != null ? c.getFecha().toLocalDate() : LocalDate.now());
        cmbEstado.setValue(c.getEstado());

        // Limpiar tabla de detalles temporal
        listaDetalles.clear();
        calcularTotal();
    }

    private Integer obtenerIdEmpleadoFromCombo(String comboValue) {
        if (comboValue == null) return null;
        try {
            return Integer.parseInt(comboValue.split(" - ")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void agregarProducto() {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor primero", Alert.AlertType.WARNING);
            return;
        }

        String sql = "insert into tbl_DETALLE_COMPRA(int id_det_compra, id_compra, id_producto, cantidad, precio_costo, descuento, itibis, subtotal, lote)";


        String nombreProducto = txtProducto.getText().trim();
        if (nombreProducto.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese el nombre del producto", Alert.AlertType.WARNING);
            return;
        }

        int cantidad = spnCantidad.getValue();
        double precioEstimado;
        String lote = txtLote.getText().trim();

        try {
            precioEstimado = Double.parseDouble(txtPrecioEstimado.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Advertencia", "Ingrese un precio estimado válido", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si el producto ya está en la lista
        boolean existe = false;
        for (DetalleCompra detalle : listaDetalles) {
            if (detalle.getNombreProducto().equalsIgnoreCase(nombreProducto)) {
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                existe = true;
                break;
            }
        }

        if (!existe) {
            // Obtener ID del producto si existe, sino usar 0 temporal
            int idProducto = obtenerIdProducto(nombreProducto);


        }
    }

    @FXML
    private void quitarProducto() {
        DetalleCompra seleccionado = tblDetalles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        listaDetalles.remove(seleccionado);
        calcularTotal();
    }

    @FXML
    private void guardarOrden(ActionEvent event) {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor", Alert.AlertType.WARNING);
            return;
        }

        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un empleado", Alert.AlertType.WARNING);
            return;
        }

        if (listaDetalles.isEmpty()) {
            mostrarAlerta("Advertencia", "Agregue productos a la orden", Alert.AlertType.WARNING);
            return;
        }

        if (idCompraSeleccionada == 0) {
            registrarOrden();
        } else {
            actualizarOrden();
        }
    }

    private void registrarOrden() {
        Integer idEmpleado = obtenerIdEmpleadoFromCombo(cmbEmpleado.getValue());
        BigDecimal subtotal = BigDecimal.valueOf(totalOrden);
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal itbis = subtotal.multiply(BigDecimal.valueOf(0.18)); // 18% ITBIS
        BigDecimal total = subtotal.add(itbis).subtract(descuento);

        String sqlCompra = "INSERT INTO tbl_COMPRA (id_proveedor, id_empleado, fecha, " +
                "subtotal, descuento, itbis, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cmbProveedor.getValue().getIdProveedor());
            ps.setInt(2, idEmpleado);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setBigDecimal(4, subtotal);
            ps.setBigDecimal(5, descuento);
            ps.setBigDecimal(6, itbis);
            ps.setBigDecimal(7, total);
            ps.setString(8, cmbEstado.getValue() != null ? cmbEstado.getValue() : "PENDIENTE");

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int idCompraGenerado = keys.next() ? keys.getInt(1) : -1;

            if (idCompraGenerado > 0) {
                // Guardar detalles
                String sqlDetalle = "INSERT INTO tbl_DETALLE_COMPRA (id_compra, id_producto, cantidad, precio_unitario, lote, subtotal) VALUES (?, ?, ?, ?, ?, ?)";


                mostrarAlerta("Éxito", "Orden de compra registrada correctamente.\nID: " + idCompraGenerado, Alert.AlertType.INFORMATION);
                limpiarTodo();
                cargarCompras();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al registrar orden: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void actualizarOrden() {
        if (idCompraSeleccionada == 0) {
            mostrarAlerta("Error", "No hay orden seleccionada", Alert.AlertType.ERROR);
            return;
        }

        Integer idEmpleado = obtenerIdEmpleadoFromCombo(cmbEmpleado.getValue());

        // Calcular totales
        BigDecimal subtotal = BigDecimal.valueOf(totalOrden);
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal itbis = subtotal.multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = subtotal.add(itbis).subtract(descuento);

        String sqlCompra = "UPDATE tbl_COMPRA SET id_proveedor = ?, id_empleado = ?, fecha = ?, " +
                "subtotal = ?, descuento = ?, itbis = ?, total = ?, estado = ? " +
                "WHERE id_compra = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sqlCompra)) {
            ps.setInt(1, cmbProveedor.getValue().getIdProveedor());
            ps.setInt(2, idEmpleado);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setBigDecimal(4, subtotal);
            ps.setBigDecimal(5, descuento);
            ps.setBigDecimal(6, itbis);
            ps.setBigDecimal(7, total);
            ps.setString(8, cmbEstado.getValue());
            ps.setInt(9, idCompraSeleccionada);

            ps.executeUpdate();

            // Eliminar detalles antiguos
            String sqlDelete = "DELETE FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
            try (PreparedStatement psDelete = conexion.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, idCompraSeleccionada);
                psDelete.executeUpdate();
            }

            // Insertar nuevos detalles
            String sqlDetalle = "INSERT INTO tbl_DETALLE_COMPRA (id_compra, id_producto cantidad, precio_unitario, lote, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                for (DetalleCompra detalle : listaDetalles) {
                    int idProducto = detalle.getIdProducto();
                    if (idProducto == 0) {
                        idProducto = obtenerIdProducto(detalle.getNombreProducto());
                    }

                    psDetalle.setInt(1, idCompraSeleccionada);
                    psDetalle.setInt(2, idProducto);
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setDouble(4, detalle.getPrecioUnitario());
                    psDetalle.setString(5, detalle.getLote());
                    psDetalle.setDouble(6, detalle.getSubtotal());
                    psDetalle.executeUpdate();
                }
            }

            mostrarAlerta("Éxito", "Orden actualizada correctamente", Alert.AlertType.INFORMATION);
            limpiarTodo();
            cargarCompras();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar orden: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarOrden(ActionEvent event) {
        if (idCompraSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una orden de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Eliminar orden?");
        confirmacion.setContentText("Esta acción no se puede deshacer. Se eliminarán también todos los detalles.");

        Optional<ButtonType> result = confirmacion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Eliminar detalles primero
                String sqlDeleteDetalles = "DELETE FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
                try (PreparedStatement ps = conexion.prepareStatement(sqlDeleteDetalles)) {
                    ps.setInt(1, idCompraSeleccionada);
                    ps.executeUpdate();
                }

                // Eliminar cabecera
                String sqlDeleteCompra = "DELETE FROM tbl_COMPRA WHERE id_compra = ?";
                try (PreparedStatement ps = conexion.prepareStatement(sqlDeleteCompra)) {
                    ps.setInt(1, idCompraSeleccionada);
                    ps.executeUpdate();
                }

                mostrarAlerta("Éxito", "Orden eliminada correctamente", Alert.AlertType.INFORMATION);
                limpiarTodo();
                cargarCompras();

            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al eliminar orden: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Cancelar?");
        confirmacion.setContentText("Se perderán todos los datos no guardados");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            limpiarTodo();
        }
    }

    @FXML
    private void buscarOrden(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarCompras();
            return;
        }

        ObservableList<Compra> filtrados = FXCollections.observableArrayList();
        for (Compra compra : listaCompras) {
            if (String.valueOf(compra.getIdCompra()).contains(busqueda) ||
                    String.valueOf(compra.getIdProveedor()).contains(busqueda) ||
                    (compra.getEstado() != null && compra.getEstado().toLowerCase().contains(busqueda))) {
                filtrados.add(compra);
            }
        }
        tblOrdenes.setItems(filtrados);
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        cargarCompras();
        txtBuscar.clear();
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarTodo();
    }

    private int obtenerIdProducto(String nombreProducto) {
        String sql = "SELECT id_producto FROM tbl_PRODUCTO WHERE nombre LIKE ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombreProducto + "%");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_producto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void calcularTotal() {
        totalOrden = 0.0;
        for (DetalleCompra detalle : listaDetalles) {
            totalOrden += detalle.getSubtotal();
        }
        lblTotal.setText("RD$ " + String.format("%.2f", totalOrden));
    }

    private void limpiarCamposProducto() {
        txtProducto.clear();
        txtPrecioEstimado.clear();
        txtLote.clear();
        spnCantidad.getValueFactory().setValue(1);
        txtProducto.requestFocus();
    }

    private void limpiarTodo() {
        cmbProveedor.setValue(null);
        cmbEmpleado.setValue(null);
        dateFecha.setValue(LocalDate.now());
        cmbEstado.setValue("PENDIENTE");
        txtProducto.clear();
        txtPrecioEstimado.clear();
        txtLote.clear();
        spnCantidad.getValueFactory().setValue(1);
        listaDetalles.clear();
        totalOrden = 0.0;
        lblTotal.setText("RD$ 0.00");
        idCompraSeleccionada = 0;
        tblOrdenes.getSelectionModel().clearSelection();
        tblDetalles.setItems(listaDetalles);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}