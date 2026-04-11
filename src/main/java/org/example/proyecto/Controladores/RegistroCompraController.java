package org.example.proyecto.Controladores;

import javafx.beans.property.SimpleStringProperty;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroCompraController implements Initializable {

    // Búsqueda
    @FXML private TextField txtBuscar;

    // Tabla de compras
    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, Integer> colId;
    @FXML private TableColumn<Compra, String> colProveedor;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colNroFactura;
    @FXML private TableColumn<Compra, BigDecimal> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;

    // Formulario principal
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtNroFacturaProv;

    // Detalle de productos
    @FXML private ComboBox<String> cmbProductoDetalle;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtDescuentoProducto;
    @FXML private TableView<DetalleCompra> tblDetalleProductos;
    @FXML private TableColumn<DetalleCompra, String> colProdNombre;
    @FXML private TableColumn<DetalleCompra, Integer> colProdCantidad;
    @FXML private TableColumn<DetalleCompra, BigDecimal> colProdPrecio;
    @FXML private TableColumn<DetalleCompra, BigDecimal> colProdDescuento;
    @FXML private TableColumn<DetalleCompra, BigDecimal> colProdSubtotal;

    // Totales
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotal;

    // Estado interno
    private int idCompraSeleccionada = 0;
    private final ObservableList<Compra> listaCompras = FXCollections.observableArrayList();
    private final ObservableList<DetalleCompra> listaDetalle = FXCollections.observableArrayList();
    private Connection conexion;
    private static final BigDecimal ITBIS_RATE = new BigDecimal("0.18");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cmbEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE", "RECIBIDA", "ANULADA"));

            cargarProveedores();
            cargarEmpleados();
            cargarComprobantes();
            cargarProductosDetalle();

            dateFecha.setValue(LocalDate.now());

            configurarTablaCompras();
            configurarTablaDetalle();
            cargarTablaCompras();

            // Cuando se selecciona una compra, se cargan sus datos y productos
            tblCompras.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, sel) -> {
                        if (sel != null) {
                            idCompraSeleccionada = sel.getIdCompra();
                            cargarCompraEnFormulario(sel);
                            cargarDetalleCompra(sel.getIdCompra());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al inicializar: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        ObservableList<String> proveedores = FXCollections.observableArrayList();
        String sql = "SELECT id_proveedor, razon_social FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY razon_social";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                proveedores.add(rs.getInt("id_proveedor") + " - " + rs.getString("razon_social"));
            cmbProveedor.setItems(proveedores);
        } catch (SQLException e) {
            try {
                String sql2 = "SELECT id_proveedor, nombre_comercial FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY nombre_comercial";
                try (Statement s2 = conexion.createStatement(); ResultSet rs2 = s2.executeQuery(sql2)) {
                    while (rs2.next())
                        proveedores.add(rs2.getInt("id_proveedor") + " - " + rs2.getString("nombre_comercial"));
                    cmbProveedor.setItems(proveedores);
                }
            } catch (SQLException e2) {
                mostrarError("Error al cargar proveedores: " + e.getMessage());
            }
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> empleados = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO ORDER BY nombres";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                empleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            cmbEmpleado.setItems(empleados);
        } catch (SQLException e) {
            mostrarError("Error al cargar empleados: " + e.getMessage());
        }
    }

    private void cargarComprobantes() {
        ObservableList<String> comprobantes = FXCollections.observableArrayList();
        comprobantes.add("NINGUNO");
        String sql = "SELECT id_comprobante, ncf FROM tbl_COMPROBANTE_FISCAL";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ncf = rs.getString("ncf");
                comprobantes.add(rs.getInt("id_comprobante") +
                        (ncf != null && !ncf.trim().isEmpty() ? " - " + ncf : ""));
            }
        } catch (SQLException e) {
            System.out.println("tbl_COMPROBANTE_FISCAL no disponible: " + e.getMessage());
        }
        cmbComprobante.setItems(comprobantes);
        cmbComprobante.setValue("NINGUNO");
    }

    private void cargarProductosDetalle() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_producto, nombre FROM tbl_PRODUCTO ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                lista.add(id + " - " + nombre);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
        cmbProductoDetalle.setItems(lista);
    }

    private void configurarTablaCompras() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colProveedor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreProveedor()));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFecha() != null ?
                        cellData.getValue().getFecha().toLocalDate().toString() : ""));
        colNroFactura.setCellValueFactory(new PropertyValueFactory<>("nroFacturaProv"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblCompras.setItems(listaCompras);
    }

    private void configurarTablaDetalle() {
        // Aquí se muestra el PRODUCTO RELACIONADO de la compra
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProdCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colProdDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colProdSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblDetalleProductos.setItems(listaDetalle);
    }

    private void cargarTablaCompras() {
        listaCompras.clear();
        String sql = "SELECT c.id_compra, c.id_proveedor, c.id_empleado, c.id_comprobante, " +
                "c.fecha, c.nro_factura_prov, c.subtotal, c.descuento, c.itbis, c.total, c.estado, " +
                "COALESCE(p.razon_social, p.nombre_comercial) as nombre_proveedor " +
                "FROM tbl_COMPRA c " +
                "LEFT JOIN tbl_PROVEEDOR p ON c.id_proveedor = p.id_proveedor " +
                "ORDER BY c.id_compra DESC";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Compra c = new Compra();
                c.setIdCompra(rs.getInt("id_compra"));
                c.setIdProveedor(rs.getInt("id_proveedor"));
                c.setNombreProveedor(rs.getString("nombre_proveedor"));
                c.setIdEmpleado(rs.getInt("id_empleado"));

                int idComp = rs.getInt("id_comprobante");
                if (!rs.wasNull()) {
                    c.setIdComprobante(idComp);
                }

                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) c.setFecha(ts.toLocalDateTime());

                c.setNroFacturaProv(rs.getString("nro_factura_prov"));
                c.setSubtotal(rs.getBigDecimal("subtotal"));
                c.setDescuento(rs.getBigDecimal("descuento"));
                c.setItbis(rs.getBigDecimal("itbis"));
                c.setTotal(rs.getBigDecimal("total"));
                c.setEstado(rs.getString("estado"));
                listaCompras.add(c);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar las compras:\n" + e.getMessage());
        }
    }

    private void cargarDetalleCompra(int idCompra) {
        listaDetalle.clear();
        tblDetalleProductos.setItems(null); // Forzar refresh de la tabla

        String sql = "SELECT d.id_det_compra, d.id_compra, d.id_producto, d.cantidad, " +
                "d.precio_costo, d.descuento, d.subtotal, p.nombre as nombre_producto " +
                "FROM tbl_DETALLE_COMPRA d " +
                "INNER JOIN tbl_PRODUCTO p ON d.id_producto = p.id_producto " +
                "WHERE d.id_compra = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetalleCompra d = new DetalleCompra();
                d.setIdDetalle(rs.getInt("id_det_compra"));
                d.setIdCompra(rs.getInt("id_compra"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setNombreProducto(rs.getString("nombre_producto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getBigDecimal("precio_costo"));
                d.setDescuento(rs.getBigDecimal("descuento"));
                d.setSubtotal(rs.getBigDecimal("subtotal"));
                listaDetalle.add(d);
            }

            // Reasignar explícitamente la lista a la tabla
            tblDetalleProductos.setItems(listaDetalle);
            tblDetalleProductos.refresh(); // ✅ Fuerza el repintado visual

            actualizarTotales();

            System.out.println("✅ Productos cargados: " + listaDetalle.size()); // Para debug en consola

        } catch (SQLException e) {
            mostrarError("Error al cargar detalle: " + e.getMessage());
        }
    }

    private void cargarCompraEnFormulario(Compra c) {
        // Cargar proveedor
        cmbProveedor.getItems().stream()
                .filter(s -> s.startsWith(c.getIdProveedor() + " - "))
                .findFirst().ifPresent(cmbProveedor::setValue);

        // Cargar empleado
        cmbEmpleado.getItems().stream()
                .filter(s -> s.startsWith(c.getIdEmpleado() + " - "))
                .findFirst().ifPresent(cmbEmpleado::setValue);

        // Cargar comprobante
        if (c.getIdComprobante() != null && c.getIdComprobante() > 0) {
            cmbComprobante.getItems().stream()
                    .filter(s -> s.startsWith(c.getIdComprobante() + " - "))
                    .findFirst().ifPresentOrElse(cmbComprobante::setValue,
                            () -> cmbComprobante.setValue("NINGUNO"));
        } else {
            cmbComprobante.setValue("NINGUNO");
        }

        // Cargar fecha
        dateFecha.setValue(c.getFecha() != null ? c.getFecha().toLocalDate() : LocalDate.now());

        // Cargar número de factura
        txtNroFacturaProv.setText(c.getNroFacturaProv() != null ? c.getNroFacturaProv() : "");

        // Cargar estado
        cmbEstado.setValue(c.getEstado());

        // Actualizar totales
        txtSubtotal.setText(c.getSubtotal() != null ? c.getSubtotal().toString() : "0.00");
        txtDescuento.setText(c.getDescuento() != null ? c.getDescuento().toString() : "0.00");
        txtItbis.setText(c.getItbis() != null ? c.getItbis().toString() : "0.00");
        txtTotal.setText(c.getTotal() != null ? c.getTotal().toString() : "0.00");
    }

    @FXML
    private void agregarProducto(ActionEvent event) {
        if (cmbProductoDetalle.getValue() == null) {
            mostrarError("Seleccione un producto");
            return;
        }

        String cantStr = txtCantidad.getText().trim();
        if (cantStr.isEmpty()) {
            mostrarError("Ingrese la cantidad");
            return;
        }

        String precioStr = txtPrecioUnitario.getText().trim();
        if (precioStr.isEmpty()) {
            mostrarError("Ingrese el precio unitario");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);
            BigDecimal precio = new BigDecimal(precioStr);
            BigDecimal descuento = txtDescuentoProducto.getText().trim().isEmpty() ?
                    BigDecimal.ZERO : new BigDecimal(txtDescuentoProducto.getText().trim());

            String productoSeleccionado = cmbProductoDetalle.getValue();
            int idProducto = Integer.parseInt(productoSeleccionado.split(" - ")[0]);
            String nombreProducto = productoSeleccionado.split(" - ")[1];

            DetalleCompra detalle = new DetalleCompra(idProducto, nombreProducto, cantidad, precio, descuento);
            listaDetalle.add(detalle);

            actualizarTotales();
            limpiarCamposProducto();

        } catch (NumberFormatException e) {
            mostrarError("Valores numéricos inválidos");
        }
    }

    @FXML
    private void eliminarProductoSeleccionado(ActionEvent event) {
        DetalleCompra seleccionado = tblDetalleProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalle.remove(seleccionado);
            actualizarTotales();
        } else {
            mostrarError("Seleccione un producto para eliminar");
        }
    }

    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;

        for (DetalleCompra d : listaDetalle) {
            BigDecimal totalSinDescuento = d.getPrecioUnitario().multiply(new BigDecimal(d.getCantidad()));
            subtotal = subtotal.add(totalSinDescuento);
            descuentoTotal = descuentoTotal.add(d.getDescuento());
        }

        BigDecimal baseImponible = subtotal.subtract(descuentoTotal);
        BigDecimal itbis = baseImponible.multiply(ITBIS_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(itbis);

        txtSubtotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString());
        txtDescuento.setText(descuentoTotal.setScale(2, RoundingMode.HALF_UP).toString());
        txtItbis.setText(itbis.toString());
        txtTotal.setText(total.toString());
    }

    private void limpiarCamposProducto() {
        cmbProductoDetalle.setValue(null);
        txtCantidad.clear();
        txtPrecioUnitario.clear();
        txtDescuentoProducto.clear();
    }

    @FXML
    private void guardarCompra(ActionEvent event) {
        if (!validar()) return;
        if (listaDetalle.isEmpty()) {
            mostrarError("Debe agregar al menos un producto");
            return;
        }

        if (idCompraSeleccionada == 0) {
            insertarNuevaCompra();
        } else {
            actualizarCompraExistente();
        }
    }

    // Corregir el editar — ya tiene los productos cargados, no necesita validar lista vacía como bloqueo
    @FXML
    private void editarCompra(ActionEvent event) {
        if (idCompraSeleccionada == 0) {
            mostrarError("Seleccione una compra de la tabla para editar");
            return;
        }
        if (!validar()) return;
        if (listaDetalle.isEmpty()) {
            mostrarError("La compra no tiene productos. Agregue al menos uno.");
            return;
        }
        actualizarCompraExistente();
    }

    @FXML
    private void NuevaCompra(ActionEvent event) {
        limpiarTodo();
        idCompraSeleccionada = 0;
        cmbProveedor.requestFocus();
    }

    // ==================== MÉTODOS SQL ====================

    private void insertarNuevaCompra() {
        String sqlCompra = "INSERT INTO tbl_COMPRA " +
                "(id_proveedor, id_empleado, id_comprobante, fecha, nro_factura_prov, " +
                " subtotal, descuento, itbis, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conexion.setAutoCommit(false);

            try (PreparedStatement psCompra = conexion.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
                setearParametrosCompra(psCompra);
                psCompra.executeUpdate();

                ResultSet keys = psCompra.getGeneratedKeys();
                if (!keys.next()) throw new SQLException("No se generó ID de compra");
                int idCompraGenerada = keys.getInt(1);

                String sqlDetalle = "INSERT INTO tbl_DETALLE_COMPRA " +
                        "(id_compra, id_producto, cantidad, precio_costo, descuento, subtotal) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    for (DetalleCompra d : listaDetalle) {
                        psDetalle.setInt(1, idCompraGenerada);
                        psDetalle.setInt(2, d.getIdProducto());
                        psDetalle.setInt(3, d.getCantidad());
                        psDetalle.setBigDecimal(4, d.getPrecioUnitario());
                        psDetalle.setBigDecimal(5, d.getDescuento());
                        psDetalle.setBigDecimal(6, d.getSubtotal());
                        psDetalle.executeUpdate();

                        actualizarStock(d.getIdProducto(), d.getCantidad());
                    }
                }

                conexion.commit();
                mostrarExito("✅ Compra registrada correctamente.\nID generado: " + idCompraGenerada);
                limpiarTodo();
                cargarTablaCompras();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            mostrarError("Error al registrar la compra:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarCompraExistente() {
        String sqlCompra = "UPDATE tbl_COMPRA " +
                "SET id_proveedor=?, id_empleado=?, id_comprobante=?, fecha=?, " +
                "    nro_factura_prov=?, subtotal=?, descuento=?, itbis=?, total=?, estado=? " +
                "WHERE id_compra=?";

        try {
            conexion.setAutoCommit(false);

            // 1. Obtener los detalles antiguos
            Map<Integer, Integer> detallesAntiguos = new HashMap<>();
            String sqlObtenerDetalles = "SELECT id_producto, cantidad FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
            try (PreparedStatement psOld = conexion.prepareStatement(sqlObtenerDetalles)) {
                psOld.setInt(1, idCompraSeleccionada);
                ResultSet rs = psOld.executeQuery();
                while (rs.next()) {
                    detallesAntiguos.put(rs.getInt("id_producto"), rs.getInt("cantidad"));
                }
            }

            // 2. Revertir el stock de los productos antiguos
            for (Map.Entry<Integer, Integer> entry : detallesAntiguos.entrySet()) {
                revertirStock(entry.getKey(), entry.getValue());
            }

            // 3. Eliminar detalles antiguos
            String sqlDeleteDetalle = "DELETE FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
            try (PreparedStatement psDelete = conexion.prepareStatement(sqlDeleteDetalle)) {
                psDelete.setInt(1, idCompraSeleccionada);
                psDelete.executeUpdate();
            }

            // 4. Actualizar cabecera de la compra
            try (PreparedStatement psCompra = conexion.prepareStatement(sqlCompra)) {
                setearParametrosCompra(psCompra);
                psCompra.setInt(11, idCompraSeleccionada);
                psCompra.executeUpdate();
            }

            // 5. Insertar nuevos detalles y actualizar stock
            String sqlInsertDetalle = "INSERT INTO tbl_DETALLE_COMPRA " +
                    "(id_compra, id_producto, cantidad, precio_costo, descuento, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlInsertDetalle)) {
                for (DetalleCompra d : listaDetalle) {
                    psDetalle.setInt(1, idCompraSeleccionada);
                    psDetalle.setInt(2, d.getIdProducto());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setBigDecimal(4, d.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, d.getDescuento());
                    psDetalle.setBigDecimal(6, d.getSubtotal());
                    psDetalle.executeUpdate();

                    actualizarStock(d.getIdProducto(), d.getCantidad());
                }
            }

            conexion.commit();
            mostrarExito("✅ Compra actualizada correctamente.");
            limpiarTodo();
            cargarTablaCompras();

        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarError("Error al actualizar:\n" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void eliminarCompra(ActionEvent event) {
        if (idCompraSeleccionada == 0) {
            mostrarError("Seleccione una compra de la tabla.");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro?");
        conf.setContentText("Esta acción eliminará la compra #" + idCompraSeleccionada + " y todos sus productos.");
        Optional<ButtonType> result = conf.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                conexion.setAutoCommit(false);

                // 1. Obtener detalles para revertir stock
                Map<Integer, Integer> detallesAEliminar = new HashMap<>();
                String sqlObtener = "SELECT id_producto, cantidad FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
                try (PreparedStatement psObtener = conexion.prepareStatement(sqlObtener)) {
                    psObtener.setInt(1, idCompraSeleccionada);
                    ResultSet rs = psObtener.executeQuery();
                    while (rs.next()) {
                        detallesAEliminar.put(rs.getInt("id_producto"), rs.getInt("cantidad"));
                    }
                }

                // 2. Revertir stock
                for (Map.Entry<Integer, Integer> entry : detallesAEliminar.entrySet()) {
                    revertirStock(entry.getKey(), entry.getValue());
                }

                // 3. Eliminar detalles
                String sqlDeleteDetalle = "DELETE FROM tbl_DETALLE_COMPRA WHERE id_compra = ?";
                try (PreparedStatement psDet = conexion.prepareStatement(sqlDeleteDetalle)) {
                    psDet.setInt(1, idCompraSeleccionada);
                    psDet.executeUpdate();
                }

                // 4. Eliminar compra
                String sqlDeleteCompra = "DELETE FROM tbl_COMPRA WHERE id_compra = ?";
                try (PreparedStatement ps = conexion.prepareStatement(sqlDeleteCompra)) {
                    ps.setInt(1, idCompraSeleccionada);
                    ps.executeUpdate();
                }

                conexion.commit();
                mostrarExito("✅ Compra eliminada correctamente.");
                limpiarTodo();
                cargarTablaCompras();

            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                mostrarError("Error al eliminar: " + e.getMessage());
            } finally {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void actualizarStock(int idProducto, int cantidad) {
        String sql = "UPDATE tbl_PRODUCTO SET stock = COALESCE(stock, 0) + ? WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
    }

    private void revertirStock(int idProducto, int cantidad) {
        String sql = "UPDATE tbl_PRODUCTO SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al revertir stock: " + e.getMessage());
        }
    }

    private void setearParametrosCompra(PreparedStatement ps) throws SQLException {
        ps.setInt(1, obtenerIdFromCombo(cmbProveedor.getValue()));
        ps.setInt(2, obtenerIdFromCombo(cmbEmpleado.getValue()));

        Integer idComp = obtenerIdNullableFromCombo(cmbComprobante.getValue());
        if (idComp != null) ps.setInt(3, idComp);
        else ps.setNull(3, Types.INTEGER);

        ps.setTimestamp(4, Timestamp.valueOf(
                LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));

        String nro = txtNroFacturaProv.getText().trim();
        if (nro.isEmpty()) ps.setNull(5, Types.NVARCHAR);
        else ps.setString(5, nro);

        ps.setBigDecimal(6, new BigDecimal(txtSubtotal.getText().trim()));
        ps.setBigDecimal(7, new BigDecimal(txtDescuento.getText().trim()));
        ps.setBigDecimal(8, new BigDecimal(txtItbis.getText().trim()));
        ps.setBigDecimal(9, new BigDecimal(txtTotal.getText().trim()));
        ps.setString(10, cmbEstado.getValue());
    }

    @FXML
    private void buscarCompra(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarTablaCompras();
            return;
        }

        ObservableList<Compra> filtrados = FXCollections.observableArrayList();
        for (Compra c : listaCompras) {
            if (String.valueOf(c.getIdCompra()).contains(busqueda) ||
                    (c.getNombreProveedor() != null && c.getNombreProveedor().toLowerCase().contains(busqueda)) ||
                    (c.getNroFacturaProv() != null && c.getNroFacturaProv().toLowerCase().contains(busqueda))) {
                filtrados.add(c);
            }
        }
        tblCompras.setItems(filtrados);
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        cargarTablaCompras();
        txtBuscar.clear();
    }

    @FXML
    private void limpiarCampos() {
        limpiarTodo();
    }

    private void limpiarTodo() {
        cmbProveedor.setValue(null);
        cmbEmpleado.setValue(null);
        cmbComprobante.setValue("NINGUNO");
        dateFecha.setValue(LocalDate.now());
        txtNroFacturaProv.clear();
        cmbEstado.setValue(null);

        listaDetalle.clear();
        limpiarCamposProducto();
        actualizarTotales();

        idCompraSeleccionada = 0;
        tblCompras.getSelectionModel().clearSelection();
    }

    private boolean validar() {
        if (cmbProveedor.getValue() == null) {
            mostrarError("Seleccione un proveedor.");
            cmbProveedor.requestFocus();
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarError("Seleccione un empleado.");
            cmbEmpleado.requestFocus();
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarError("Seleccione una fecha.");
            dateFecha.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarError("Seleccione un estado.");
            cmbEstado.requestFocus();
            return false;
        }
        return true;
    }

    private int obtenerIdFromCombo(String val) {
        if (val == null) return 0;
        try {
            return Integer.parseInt(val.split(" - ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Integer obtenerIdNullableFromCombo(String val) {
        if (val == null || val.equals("NINGUNO")) return null;
        try {
            return Integer.parseInt(val.split(" - ")[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}