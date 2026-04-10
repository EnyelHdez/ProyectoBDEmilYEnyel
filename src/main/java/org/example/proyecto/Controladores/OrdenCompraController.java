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
import org.example.proyecto.Modelos.OrdenCompra;
import org.example.proyecto.Modelos.DetalleOrdenCompra;

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

public class OrdenCompraController implements Initializable {

    // Búsqueda
    @FXML private TextField txtBuscar;

    // Tabla de órdenes
    @FXML private TableView<OrdenCompra> tblOrdenes;
    @FXML private TableColumn<OrdenCompra, Integer> colId;
    @FXML private TableColumn<OrdenCompra, String> colProveedor;
    @FXML private TableColumn<OrdenCompra, String> colEmpleado;
    @FXML private TableColumn<OrdenCompra, String> colFechaOrden;
    @FXML private TableColumn<OrdenCompra, String> colFechaEntrega;
    @FXML private TableColumn<OrdenCompra, BigDecimal> colTotal;
    @FXML private TableColumn<OrdenCompra, String> colEstado;

    // Formulario principal
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbCondicionPago;
    @FXML private DatePicker dateFechaOrden;
    @FXML private DatePicker dateFechaEntrega;
    @FXML private TextArea txtObservaciones;

    // Detalle de productos
    @FXML private ComboBox<String> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TableView<DetalleOrdenCompra> tblDetalleProductos;
    @FXML private TableColumn<DetalleOrdenCompra, String> colProdNombre;
    @FXML private TableColumn<DetalleOrdenCompra, Integer> colProdCantidad;
    @FXML private TableColumn<DetalleOrdenCompra, BigDecimal> colProdPrecio;
    @FXML private TableColumn<DetalleOrdenCompra, BigDecimal> colProdSubtotal;
    @FXML private TableColumn<DetalleOrdenCompra, Void> colProdAcciones;

    // Totales
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtTotal;

    // Estado interno
    private int idOrdenSeleccionada = 0;
    private final ObservableList<OrdenCompra> listaOrdenes = FXCollections.observableArrayList();
    private final ObservableList<DetalleOrdenCompra> listaDetalle = FXCollections.observableArrayList();
    private Connection conexion;
    private Map<Integer, String> mapaProductos = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            // Configurar combos
            cmbEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE", "APROBADA", "ENVIADA", "RECIBIDA", "CANCELADA"));
            cmbCondicionPago.setItems(FXCollections.observableArrayList(
                    "CONTADO", "CRÉDITO 15 DÍAS", "CRÉDITO 30 DÍAS", "CRÉDITO 45 DÍAS", "CRÉDITO 60 DÍAS"));

            cargarProveedores();
            cargarEmpleados();
            cargarProductos();

            dateFechaOrden.setValue(LocalDate.now());

            configurarTablaOrdenes();
            configurarTablaDetalle();
            cargarTablaOrdenes();

            tblOrdenes.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, sel) -> {
                        if (sel != null) {
                            idOrdenSeleccionada = sel.getIdOrden();
                            rellenarFormulario(sel);
                            cargarDetalleOrden(sel.getIdOrden());
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

    private void cargarProductos() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_producto, nombre, precio_costo FROM tbl_PRODUCTO ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                lista.add(id + " - " + nombre);
                mapaProductos.put(id, nombre);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
        cmbProducto.setItems(lista);

        // Auto-completar precio al seleccionar producto
        cmbProducto.setOnAction(e -> {
            String selected = cmbProducto.getValue();
            if (selected != null && !selected.isEmpty()) {
                int idProducto = Integer.parseInt(selected.split(" - ")[0]);
                cargarPrecioProducto(idProducto);
            }
        });
    }

    private void cargarPrecioProducto(int idProducto) {
        String sql = "SELECT precio_compra FROM tbl_PRODUCTO WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal precio = rs.getBigDecimal("precio_compra");
                if (precio != null) {
                    txtPrecioUnitario.setText(precio.toString());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar precio: " + e.getMessage());
        }
    }

    private void configurarTablaOrdenes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colProveedor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreProveedor()));
        colEmpleado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreEmpleado()));
        colFechaOrden.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaOrden() != null ?
                        cellData.getValue().getFechaOrden().toLocalDate().toString() : ""));
        colFechaEntrega.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaEntrega() != null ?
                        cellData.getValue().getFechaEntrega().toLocalDate().toString() : "Pendiente"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Colorear filas según estado
        tblOrdenes.setRowFactory(tv -> new TableRow<OrdenCompra>() {
            @Override
            protected void updateItem(OrdenCompra item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getEstado()) {
                        case "PENDIENTE":
                            setStyle("-fx-background-color: #FEF3C7;");
                            break;
                        case "APROBADA":
                            setStyle("-fx-background-color: #DBEAFE;");
                            break;
                        case "ENVIADA":
                            setStyle("-fx-background-color: #E0E7FF;");
                            break;
                        case "RECIBIDA":
                            setStyle("-fx-background-color: #D1FAE5;");
                            break;
                        case "CANCELADA":
                            setStyle("-fx-background-color: #FEE2E2;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        tblOrdenes.setItems(listaOrdenes);
    }

    private void configurarTablaDetalle() {
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProdCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colProdSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Botón eliminar
        colProdAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("🗑️");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btnEliminar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand;");
                    btnEliminar.setOnAction(event -> {
                        DetalleOrdenCompra detalle = getTableView().getItems().get(getIndex());
                        listaDetalle.remove(detalle);
                        actualizarTotales();
                    });
                    setGraphic(btnEliminar);
                }
            }
        });

        tblDetalleProductos.setItems(listaDetalle);
    }

    private void cargarTablaOrdenes() {
        listaOrdenes.clear();
        String sql = "SELECT o.*, p.razon_social as nombre_proveedor, e.nombres as nombre_empleado " +
                "FROM tbl_ORDEN_COMPRA o " +
                "LEFT JOIN tbl_PROVEEDOR p ON o.id_proveedor = p.id_proveedor " +
                "LEFT JOIN tbl_EMPLEADO e ON o.id_empleado = e.id_empleado " +
                "ORDER BY o.id_orden DESC";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                OrdenCompra o = new OrdenCompra();
                o.setIdOrden(rs.getInt("id_orden"));
                o.setIdProveedor(rs.getInt("id_proveedor"));
                o.setNombreProveedor(rs.getString("nombre_proveedor"));
                o.setIdEmpleado(rs.getInt("id_empleado"));
                o.setNombreEmpleado(rs.getString("nombre_empleado"));
                Timestamp tsOrden = rs.getTimestamp("fecha_orden");
                if (tsOrden != null) o.setFechaOrden(tsOrden.toLocalDateTime());
                Timestamp tsEntrega = rs.getTimestamp("fecha_entrega");
                if (tsEntrega != null) o.setFechaEntrega(tsEntrega.toLocalDateTime());
                o.setCondicionPago(rs.getString("condicion_pago"));
                o.setObservaciones(rs.getString("observaciones"));
                o.setSubtotal(rs.getBigDecimal("subtotal"));
                o.setTotal(rs.getBigDecimal("total"));
                o.setEstado(rs.getString("estado"));
                listaOrdenes.add(o);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar las órdenes:\n" + e.getMessage());
        }
    }

    private void cargarDetalleOrden(int idOrden) {
        listaDetalle.clear();
        String sql = "SELECT d.*, p.nombre as nombre_producto " +
                "FROM tbl_DETALLE_ORDEN_COMPRA d " +
                "JOIN tbl_PRODUCTO p ON d.id_producto = p.id_producto " +
                "WHERE d.id_orden = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetalleOrdenCompra d = new DetalleOrdenCompra();
                d.setIdDetalle(rs.getInt("id_detalle"));
                d.setIdOrden(rs.getInt("id_orden"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setNombreProducto(rs.getString("nombre_producto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                d.setSubtotal(rs.getBigDecimal("subtotal"));
                listaDetalle.add(d);
            }
            actualizarTotales();
        } catch (SQLException e) {
            mostrarError("Error al cargar detalle: " + e.getMessage());
        }
    }

    @FXML
    private void agregarProducto(ActionEvent event) {
        if (cmbProducto.getValue() == null) {
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

            String productoSeleccionado = cmbProducto.getValue();
            int idProducto = Integer.parseInt(productoSeleccionado.split(" - ")[0]);
            String nombreProducto = productoSeleccionado.split(" - ")[1];

            DetalleOrdenCompra detalle = new DetalleOrdenCompra(idProducto, nombreProducto, cantidad, precio);
            listaDetalle.add(detalle);

            actualizarTotales();
            limpiarCamposProducto();

        } catch (NumberFormatException e) {
            mostrarError("Valores numéricos inválidos");
        }
    }

    @FXML
    private void eliminarProductoSeleccionado(ActionEvent event) {
        DetalleOrdenCompra seleccionado = tblDetalleProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalle.remove(seleccionado);
            actualizarTotales();
        } else {
            mostrarError("Seleccione un producto para eliminar");
        }
    }

    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetalleOrdenCompra d : listaDetalle) {
            subtotal = subtotal.add(d.getSubtotal());
        }

        txtSubtotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString());
        txtTotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString());
    }

    private void limpiarCamposProducto() {
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtPrecioUnitario.clear();
    }

    @FXML
    private void guardarOrden(ActionEvent event) {
        if (idOrdenSeleccionada == 0) NuevaOrden();
        else actualizarOrden();
    }

    public void NuevaOrden() {
        if (!validar()) return;
        if (listaDetalle.isEmpty()) {
            mostrarError("Debe agregar al menos un producto a la orden");
            return;
        }

        String sqlOrden = "INSERT INTO tbl_ORDEN_COMPRA " +
                "(id_proveedor, id_empleado, fecha_orden, fecha_entrega, condicion_pago, " +
                " observaciones, subtotal, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conexion.setAutoCommit(false);

            try (PreparedStatement psOrden = conexion.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
                setearParametrosOrden(psOrden);
                psOrden.executeUpdate();

                ResultSet keys = psOrden.getGeneratedKeys();
                if (!keys.next()) throw new SQLException("No se generó ID de orden");
                int idOrdenGenerada = keys.getInt(1);

                // Insertar detalles
                String sqlDetalle = "INSERT INTO tbl_DETALLE_ORDEN_COMPRA " +
                        "(id_orden, id_producto, cantidad, precio_unitario, subtotal) " +
                        "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    for (DetalleOrdenCompra d : listaDetalle) {
                        psDetalle.setInt(1, idOrdenGenerada);
                        psDetalle.setInt(2, d.getIdProducto());
                        psDetalle.setInt(3, d.getCantidad());
                        psDetalle.setBigDecimal(4, d.getPrecioUnitario());
                        psDetalle.setBigDecimal(5, d.getSubtotal());
                        psDetalle.executeUpdate();
                    }
                }

                conexion.commit();
                mostrarExito("Orden de compra registrada correctamente.\nID generado: " + idOrdenGenerada);
                limpiarTodo();
                cargarTablaOrdenes();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            mostrarError("Error al registrar la orden:\n" + e.getMessage());
        }
    }

    private void actualizarOrden() {
        if (!validar()) return;
        if (listaDetalle.isEmpty()) {
            mostrarError("Debe agregar al menos un producto a la orden");
            return;
        }

        String sqlOrden = "UPDATE tbl_ORDEN_COMPRA " +
                "SET id_proveedor=?, id_empleado=?, fecha_orden=?, fecha_entrega=?, " +
                "    condicion_pago=?, observaciones=?, subtotal=?, total=?, estado=? " +
                "WHERE id_orden=?";

        try {
            conexion.setAutoCommit(false);

            // Eliminar detalles antiguos
            try (PreparedStatement psDelete = conexion.prepareStatement(
                    "DELETE FROM tbl_DETALLE_ORDEN_COMPRA WHERE id_orden = ?")) {
                psDelete.setInt(1, idOrdenSeleccionada);
                psDelete.executeUpdate();
            }

            // Actualizar orden
            try (PreparedStatement psOrden = conexion.prepareStatement(sqlOrden)) {
                setearParametrosOrden(psOrden);
                psOrden.setInt(10, idOrdenSeleccionada);
                psOrden.executeUpdate();
            }

            // Insertar nuevos detalles
            String sqlDetalle = "INSERT INTO tbl_DETALLE_ORDEN_COMPRA " +
                    "(id_orden, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                for (DetalleOrdenCompra d : listaDetalle) {
                    psDetalle.setInt(1, idOrdenSeleccionada);
                    psDetalle.setInt(2, d.getIdProducto());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setBigDecimal(4, d.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, d.getSubtotal());
                    psDetalle.executeUpdate();
                }
            }

            conexion.commit();
            mostrarExito("Orden actualizada correctamente.");
            limpiarTodo();
            cargarTablaOrdenes();

        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarError("Error al actualizar:\n" + e.getMessage());
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setearParametrosOrden(PreparedStatement ps) throws SQLException {
        ps.setInt(1, obtenerIdFromCombo(cmbProveedor.getValue()));
        ps.setInt(2, obtenerIdFromCombo(cmbEmpleado.getValue()));

        ps.setTimestamp(3, Timestamp.valueOf(
                LocalDateTime.of(dateFechaOrden.getValue(), LocalTime.now())));

        if (dateFechaEntrega.getValue() != null) {
            ps.setTimestamp(4, Timestamp.valueOf(
                    LocalDateTime.of(dateFechaEntrega.getValue(), LocalTime.now())));
        } else {
            ps.setNull(4, Types.TIMESTAMP);
        }

        ps.setString(5, cmbCondicionPago.getValue());
        ps.setString(6, txtObservaciones.getText().trim());
        ps.setBigDecimal(7, new BigDecimal(txtSubtotal.getText().trim()));
        ps.setBigDecimal(8, new BigDecimal(txtTotal.getText().trim()));
        ps.setString(9, cmbEstado.getValue() != null ? cmbEstado.getValue() : "PENDIENTE");
    }

    @FXML
    private void cambiarEstado(ActionEvent event) {
        if (idOrdenSeleccionada == 0) {
            mostrarError("Seleccione una orden de la tabla");
            return;
        }

        OrdenCompra orden = tblOrdenes.getSelectionModel().getSelectedItem();
        if (orden == null) {
            mostrarError("Seleccione una orden de la tabla");
            return;
        }

        String estadoActual = orden.getEstado();
        String nuevoEstado = null;

        switch (estadoActual) {
            case "PENDIENTE":
                nuevoEstado = "APROBADA";
                break;
            case "APROBADA":
                nuevoEstado = "ENVIADA";
                break;
            case "ENVIADA":
                nuevoEstado = "RECIBIDA";
                break;
            default:
                mostrarError("No se puede cambiar el estado de " + estadoActual);
                return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE tbl_ORDEN_COMPRA SET estado = ? WHERE id_orden = ?")) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idOrdenSeleccionada);
            ps.executeUpdate();

            mostrarExito("Estado cambiado a: " + nuevoEstado);
            cargarTablaOrdenes();

            // Si se recibe, actualizar stock
            if (nuevoEstado.equals("RECIBIDA")) {
                recibirOrden(idOrdenSeleccionada);
            }

        } catch (SQLException e) {
            mostrarError("Error al cambiar estado: " + e.getMessage());
        }
    }

    private void recibirOrden(int idOrden) {
        try {
            // Actualizar stock de productos
            String sql = "SELECT id_producto, cantidad FROM tbl_DETALLE_ORDEN_COMPRA WHERE id_orden = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idOrden);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    actualizarStock(rs.getInt("id_producto"), rs.getInt("cantidad"));
                }
            }

            mostrarExito("Stock actualizado correctamente");
        } catch (SQLException e) {
            mostrarError("Error al actualizar stock: " + e.getMessage());
        }
    }

    private void actualizarStock(int idProducto, int cantidad) {
        String sql = "UPDATE tbl_PRODUCTO SET stock = stock + ? WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarOrden(ActionEvent event) {
        if (idOrdenSeleccionada == 0) {
            mostrarError("Seleccione una orden de la tabla");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar cancelación");
        conf.setHeaderText("¿Está seguro de cancelar esta orden?");
        conf.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = conf.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "UPDATE tbl_ORDEN_COMPRA SET estado = 'CANCELADA' WHERE id_orden = ?")) {
                ps.setInt(1, idOrdenSeleccionada);
                ps.executeUpdate();

                mostrarExito("Orden cancelada correctamente.");
                cargarTablaOrdenes();

            } catch (SQLException e) {
                mostrarError("Error al cancelar orden: " + e.getMessage());
            }
        }
    }

    private void rellenarFormulario(OrdenCompra o) {
        cmbProveedor.getItems().stream()
                .filter(s -> s.startsWith(o.getIdProveedor() + " - "))
                .findFirst().ifPresent(cmbProveedor::setValue);

        cmbEmpleado.getItems().stream()
                .filter(s -> s.startsWith(o.getIdEmpleado() + " - "))
                .findFirst().ifPresent(cmbEmpleado::setValue);

        dateFechaOrden.setValue(o.getFechaOrden() != null ? o.getFechaOrden().toLocalDate() : LocalDate.now());
        dateFechaEntrega.setValue(o.getFechaEntrega() != null ? o.getFechaEntrega().toLocalDate() : null);
        cmbCondicionPago.setValue(o.getCondicionPago());
        txtObservaciones.setText(o.getObservaciones() != null ? o.getObservaciones() : "");
        cmbEstado.setValue(o.getEstado());
    }

    @FXML
    private void buscarOrden(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarTablaOrdenes();
            return;
        }

        ObservableList<OrdenCompra> filtrados = FXCollections.observableArrayList();
        for (OrdenCompra o : listaOrdenes) {
            if (String.valueOf(o.getIdOrden()).contains(busqueda) ||
                    (o.getNombreProveedor() != null && o.getNombreProveedor().toLowerCase().contains(busqueda)) ||
                    (o.getEstado() != null && o.getEstado().toLowerCase().contains(busqueda))) {
                filtrados.add(o);
            }
        }
        tblOrdenes.setItems(filtrados);
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        cargarTablaOrdenes();
        txtBuscar.clear();
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarTodo();
    }

    private void limpiarTodo() {
        cmbProveedor.setValue(null);
        cmbEmpleado.setValue(null);
        cmbEstado.setValue("PENDIENTE");
        cmbCondicionPago.setValue(null);
        dateFechaOrden.setValue(LocalDate.now());
        dateFechaEntrega.setValue(null);
        txtObservaciones.clear();

        listaDetalle.clear();
        limpiarCamposProducto();
        actualizarTotales();

        idOrdenSeleccionada = 0;
        tblOrdenes.getSelectionModel().clearSelection();
    }

    private boolean validar() {
        if (cmbProveedor.getValue() == null) {
            mostrarError("Seleccione un proveedor.");
            cmbProveedor.requestFocus(); return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarError("Seleccione un empleado.");
            cmbEmpleado.requestFocus(); return false;
        }
        if (dateFechaOrden.getValue() == null) {
            mostrarError("Seleccione una fecha de orden.");
            dateFechaOrden.requestFocus(); return false;
        }
        return true;
    }

    private int obtenerIdFromCombo(String val) {
        if (val == null) return 0;
        try { return Integer.parseInt(val.split(" - ")[0]); }
        catch (NumberFormatException e) { return 0; }
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