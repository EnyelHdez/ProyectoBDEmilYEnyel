//package org.example.proyecto.Controladores;
//
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import org.example.proyecto.Conexion.ConexionBD;
//import org.example.proyecto.Modelos.Devolucion;
//import org.example.proyecto.Modelos.Devolucion;
//import org.example.proyecto.Modelos.Venta;
//import org.example.proyecto.Modelos.DetalleVenta;
//
//import java.math.BigDecimal;
//import java.net.URL;
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.ResourceBundle;
//
//public class RegistroDevolucionController implements Initializable {
//
//    // Campos de búsqueda
//    @FXML private TextField txtBuscarVenta;
//    @FXML private TableView<Venta> tblVentas;
//    @FXML private TableColumn<Venta, Integer> colIdVenta;
//    @FXML private TableColumn<Venta, String> colCliente;
//    @FXML private TableColumn<Venta, String> colEmpleado;
//    @FXML private TableColumn<Venta, String> colFechaVenta;
//    @FXML private TableColumn<Venta, BigDecimal> colTotalVenta;
//    @FXML private TableColumn<Venta, String> colEstadoVenta;
//
//    // Tabla de detalles de venta
//    @FXML private TableView<DetalleVenta> tblDetalle;
//    @FXML private TableColumn<DetalleVenta, String> colDetProducto;
//    @FXML private TableColumn<DetalleVenta, Integer> colDetCantidad;
//    @FXML private TableColumn<DetalleVenta, BigDecimal> colDetPrecio;
//    @FXML private TableColumn<DetalleVenta, BigDecimal> colDetSubtotal;
//    @FXML private TableColumn<DetalleVenta, String> colDetLote;
//    @FXML private TextField txtCantDevolver;
//
//    // Tabla de devolución actual
//    @FXML private TableView<DetalleDevolucion> tblDevolucion;
//    @FXML private TableColumn<DetalleDevolucion, String> colDevProducto;
//    @FXML private TableColumn<DetalleDevolucion, Integer> colDevCantidad;
//    @FXML private TableColumn<DetalleDevolucion, BigDecimal> colDevPrecio;
//    @FXML private TableColumn<DetalleDevolucion, BigDecimal> colDevSubtotal;
//
//    // Formulario de devolución
//    @FXML private ComboBox<String> cmbEmpleado;
//    @FXML private ComboBox<MotivoDevolucion> cmbMotivo;
//    @FXML private ComboBox<String> cmbComprobante;
//    @FXML private TextArea txtObservacion;
//    @FXML private Label lblMontoDevolucion;
//
//    // Historial de devoluciones
//    @FXML private TableView<Devolucion> tblHistorial;
//    @FXML private TableColumn<Devolucion, Integer> colHistId;
//    @FXML private TableColumn<Devolucion, Integer> colHistVenta;
//    @FXML private TableColumn<Devolucion, String> colHistFecha;
//    @FXML private TableColumn<Devolucion, BigDecimal> colHistMonto;
//    @FXML private TableColumn<Devolucion, String> colHistMotivo;
//
//    private Connection conexion;
//    private int ventaSeleccionadaId = 0;
//    private ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
//    private ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList();
//    private ObservableList<DetalleDevolucion> listaDevolucion = FXCollections.observableArrayList();
//    private ObservableList<Devolucion> listaHistorial = FXCollections.observableArrayList();
//    private ObservableList<MotivoDevolucion> listaMotivos = FXCollections.observableArrayList();
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        try {
//            conexion = new ConexionBD().EstablecerConexion();
//
//            cargarEmpleados();
//            cargarMotivos();
//            cargarComprobantes();
//            configurarTablas();
//            cargarHistorial();
//            cargarVentas();
//
//            // Listener para selección de venta
//            tblVentas.getSelectionModel().selectedItemProperty().addListener(
//                    (obs, oldVal, sel) -> {
//                        if (sel != null) {
//                            ventaSeleccionadaId = sel.getIdVenta();
//                            cargarDetallesVenta(sel.getIdVenta());
//                        }
//                    });
//
//            // Listener para selección de detalle de venta
//            tblDetalle.getSelectionModel().selectedItemProperty().addListener(
//                    (obs, oldVal, sel) -> {
//                        if (sel != null) {
//                            txtCantDevolver.setText(String.valueOf(sel.getCantidad()));
//                        }
//                    });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            mostrarError("Error al inicializar: " + e.getMessage());
//        }
//    }
//
//    private void cargarEmpleados() {
//        ObservableList<String> empleados = FXCollections.observableArrayList();
//        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO WHERE estado = 'Activo' ORDER BY nombres";
//
//        try (Statement stmt = conexion.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                empleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
//            }
//            cmbEmpleado.setItems(empleados);
//        } catch (SQLException e) {
//            mostrarError("Error al cargar empleados: " + e.getMessage());
//        }
//    }
//
//    private void cargarMotivos() {
//        listaMotivos.clear();
//        String sql = "SELECT id_motivo, nombre, descripcion FROM tbl_MOTIVO_DEVOLUCION WHERE estado = 'Activo' ORDER BY nombre";
//
//        try (Statement stmt = conexion.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                MotivoDevolucion motivo = new MotivoDevolucion();
//                motivo.setIdMotivo(rs.getInt("id_motivo"));
//                motivo.setNombre(rs.getString("nombre"));
//                motivo.setDescripcion(rs.getString("descripcion"));
//                listaMotivos.add(motivo);
//            }
//            cmbMotivo.setItems(listaMotivos);
//
//            // Configurar cómo se muestra el motivo en el ComboBox
//            cmbMotivo.setCellFactory(lv -> new ListCell<MotivoDevolucion>() {
//                @Override
//                protected void updateItem(MotivoDevolucion item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setText(empty || item == null ? null : item.getNombre());
//                }
//            });
//
//            cmbMotivo.setButtonCell(new ListCell<MotivoDevolucion>() {
//                @Override
//                protected void updateItem(MotivoDevolucion item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setText(empty || item == null ? null : item.getNombre());
//                }
//            });
//
//        } catch (SQLException e) {
//            mostrarError("Error al cargar motivos: " + e.getMessage());
//        }
//    }
//
//    private void cargarComprobantes() {
//        ObservableList<String> comprobantes = FXCollections.observableArrayList();
//        comprobantes.add("NINGUNO");
//
//        try {
//            String sql = "SELECT id_comprobante, nfc FROM tbl_COMPROBANTE_FISCAL WHERE estado = 'Activo' ORDER BY nfc";
//            try (Statement stmt = conexion.createStatement();
//                 ResultSet rs = stmt.executeQuery(sql)) {
//                while (rs.next()) {
//                    comprobantes.add(rs.getInt("id_comprobante") + " - " + rs.getString("nfc"));
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Tabla tbl_COMPROBANTE_FISCAL no encontrada");
//        }
//        cmbComprobante.setItems(comprobantes);
//        cmbComprobante.setValue("NINGUNO");
//    }
//
//    private void configurarTablas() {
//        // Configurar tabla de ventas
//        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
//        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
//        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
//        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
//        colTotalVenta.setCellValueFactory(new PropertyValueFactory<>("total"));
//        colEstadoVenta.setCellValueFactory(new PropertyValueFactory<>("estado"));
//
//        // Configurar tabla de detalles de venta
//        colDetProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
//        colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
//        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
//        colDetSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
//        colDetLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
//
//        // Configurar tabla de devolución actual
//        colDevProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
//        colDevCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
//        colDevPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
//        colDevSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
//
//        // Configurar tabla de historial
//        colHistId.setCellValueFactory(new PropertyValueFactory<>("idDevolucion"));
//        colHistVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
//        colHistFecha.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
//        colHistMonto.setCellValueFactory(new PropertyValueFactory<>("montoDevuelto"));
//        colHistMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoNombre"));
//
//        tblVentas.setItems(listaVentas);
//        tblDetalle.setItems(listaDetalles);
//        tblDevolucion.setItems(listaDevolucion);
//        tblHistorial.setItems(listaHistorial);
//    }
//
//    @FXML
//    private void buscarVenta() {
//        String busqueda = txtBuscarVenta.getText().trim().toLowerCase();
//        if (busqueda.isEmpty()) {
//            cargarVentas();
//            return;
//        }
//
//        listaVentas.clear();
//        String sql = "SELECT v.*, c.nombre as nombre_cliente, e.nombres as nombre_empleado " +
//                "FROM tbl_VENTA v " +
//                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
//                "LEFT JOIN tbl_EMPLEADO e ON v.id_empleado = e.id_empleado " +
//                "WHERE v.estado = 'COMPLETADA' AND " +
//                "(CAST(v.id_venta AS VARCHAR) LIKE ? OR c.nombre LIKE ?) " +
//                "ORDER BY v.id_venta DESC";
//
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            String like = "%" + busqueda + "%";
//            ps.setString(1, like);
//            ps.setString(2, like);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Venta venta = new Venta();
//                venta.setIdVenta(rs.getInt("id_venta"));
//                venta.setNombreCliente(rs.getString("nombre_cliente"));
//                venta.setNombreEmpleado(rs.getString("nombre_empleado"));
//                venta.setFecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null);
//                venta.setTotal(rs.getBigDecimal("total"));
//                venta.setEstado(rs.getString("estado"));
//                listaVentas.add(venta);
//            }
//        } catch (SQLException e) {
//            mostrarError("Error al buscar ventas: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    private void mostrarTodos() {
//        txtBuscarVenta.clear();
//        cargarVentas();
//    }
//
//    private void cargarVentas() {
//        listaVentas.clear();
//        String sql = "SELECT v.*, c.nombre as nombre_cliente, e.nombres as nombre_empleado " +
//                "FROM tbl_VENTA v " +
//                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
//                "LEFT JOIN tbl_EMPLEADO e ON v.id_empleado = e.id_empleado " +
//                "WHERE v.estado = 'COMPLETADA' " +
//                "ORDER BY v.id_venta DESC";
//
//        try (Statement stmt = conexion.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                Venta venta = new Venta();
//                venta.setIdVenta(rs.getInt("id_venta"));
//                venta.setNombreCliente(rs.getString("nombre_cliente"));
//                venta.setNombreEmpleado(rs.getString("nombre_empleado"));
//                venta.setFecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null);
//                venta.setTotal(rs.getBigDecimal("total"));
//                venta.setEstado(rs.getString("estado"));
//                listaVentas.add(venta);
//            }
//
//        } catch (SQLException e) {
//            mostrarError("Error al cargar ventas: " + e.getMessage());
//        }
//    }
//
//    private void cargarDetallesVenta(int idVenta) {
//        listaDetalles.clear();
//        String sql = "SELECT dv.*, p.nombre as nombre_producto " +
//                "FROM tbl_DETALLE_VENTA dv " +
//                "LEFT JOIN tbl_PRODUCTO p ON dv.id_producto = p.id_producto " +
//                "WHERE dv.id_venta = ?";
//
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            ps.setInt(1, idVenta);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                DetalleVenta detalle = new DetalleVenta();
//                detalle.setIdDetalle(rs.getInt("id_detalle"));
//                detalle.setIdVenta(rs.getInt("id_venta"));
//                detalle.setIdProducto(rs.getInt("id_producto"));
//                detalle.setNombreProducto(rs.getString("nombre_producto"));
//                detalle.setCantidad(rs.getInt("cantidad"));
//                detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
//                detalle.setSubtotal(rs.getBigDecimal("subtotal"));
//                detalle.setLote(rs.getString("lote"));
//                listaDetalles.add(detalle);
//            }
//
//        } catch (SQLException e) {
//            mostrarError("Error al cargar detalles: " + e.getMessage());
//        }
//    }
//
//    private void cargarHistorial() {
//        listaHistorial.clear();
//        String sql = "SELECT d.*, m.nombre as motivo_nombre, e.nombres as nombre_empleado " +
//                "FROM tbl_DEVOLUCION d " +
//                "LEFT JOIN tbl_MOTIVO_DEVOLUCION m ON d.id_motivo = m.id_motivo " +
//                "LEFT JOIN tbl_EMPLEADO e ON d.id_empleado = e.id_empleado " +
//                "ORDER BY d.id_devolucion DESC";
//
//        try (Statement stmt = conexion.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                Devolucion devolucion = new Devolucion(
//                        rs.getInt("id_devolucion"),
//                        rs.getInt("id_venta"),
//                        rs.getInt("id_empleado"),
//                        rs.getInt("id_motivo"),
//                        rs.getObject("id_comprobante") != null ? rs.getInt("id_comprobante") : null,
//                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
//                        rs.getBigDecimal("monto_devuelto"),
//                        rs.getString("observacion")
//                );
//                devolucion.setMotivoNombre(rs.getString("motivo_nombre"));
//                listaHistorial.add(devolucion);
//            }
//
//        } catch (SQLException e) {
//            mostrarError("Error al cargar historial: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    private void agregarADevolucion() {
//        DetalleVenta seleccionado = tblDetalle.getSelectionModel().getSelectedItem();
//        if (seleccionado == null) {
//            mostrarError("Seleccione un producto de la venta");
//            return;
//        }
//
//        int cantidadDevolver;
//        try {
//            cantidadDevolver = Integer.parseInt(txtCantDevolver.getText().trim());
//            if (cantidadDevolver <= 0 || cantidadDevolver > seleccionado.getCantidad()) {
//                mostrarError("Cantidad inválida. Máximo: " + seleccionado.getCantidad());
//                return;
//            }
//        } catch (NumberFormatException e) {
//            mostrarError("Ingrese una cantidad válida");
//            return;
//        }
//
//        // Verificar si el producto ya está en la lista de devolución
//        for (DetalleDevolucion dd : listaDevolucion) {
//            if (dd.getIdProducto() == seleccionado.getIdProducto()) {
//                int nuevaCantidad = dd.getCantidad() + cantidadDevolver;
//                if (nuevaCantidad > seleccionado.getCantidad()) {
//                    mostrarError("La cantidad total a devolver no puede exceder la cantidad vendida");
//                    return;
//                }
//                dd.setCantidad(nuevaCantidad);
//                dd.setSubtotal(dd.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
//                calcularTotalDevolucion();
//                tblDevolucion.refresh();
//                return;
//            }
//        }
//
//        // Agregar nuevo producto a la lista
//        DetalleDevolucion nuevo = new DetalleDevolucion();
//        nuevo.setIdProducto(seleccionado.getIdProducto());
//        nuevo.setNombreProducto(seleccionado.getNombreProducto());
//        nuevo.setCantidad(cantidadDevolver);
//        nuevo.setPrecioUnitario(seleccionado.getPrecioUnitario());
//        nuevo.setSubtotal(seleccionado.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidadDevolver)));
//        nuevo.setLote(seleccionado.getLote());
//        listaDevolucion.add(nuevo);
//
//        calcularTotalDevolucion();
//    }
//
//    @FXML
//    private void quitarDeDevolucion() {
//        DetalleDevolucion seleccionado = tblDevolucion.getSelectionModel().getSelectedItem();
//        if (seleccionado == null) {
//            mostrarError("Seleccione un producto de la lista de devolución");
//            return;
//        }
//        listaDevolucion.remove(seleccionado);
//        calcularTotalDevolucion();
//    }
//
//    private void calcularTotalDevolucion() {
//        BigDecimal total = BigDecimal.ZERO;
//        for (DetalleDevolucion dd : listaDevolucion) {
//            total = total.add(dd.getSubtotal());
//        }
//        lblMontoDevolucion.setText("RD$ " + total.toPlainString());
//    }
//
//    @FXML
//    private void procesarDevolucion() {
//        if (ventaSeleccionadaId == 0) {
//            mostrarError("Seleccione una venta primero");
//            return;
//        }
//
//        if (listaDevolucion.isEmpty()) {
//            mostrarError("Agregue al menos un producto a devolver");
//            return;
//        }
//
//        if (cmbEmpleado.getValue() == null) {
//            mostrarError("Seleccione el empleado que procesa la devolución");
//            return;
//        }
//
//        if (cmbMotivo.getValue() == null) {
//            mostrarError("Seleccione un motivo de devolución");
//            return;
//        }
//
//        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmacion.setTitle("Confirmar devolución");
//        confirmacion.setHeaderText("¿Está seguro de procesar esta devolución?");
//        confirmacion.setContentText("Esta acción actualizará el inventario y registrará la devolución.");
//
//        if (confirmacion.showAndWait().get() != ButtonType.OK) return;
//
//        try {
//            conexion.setAutoCommit(false);
//
//            // Obtener ID del empleado
//            int idEmpleado = Integer.parseInt(cmbEmpleado.getValue().split(" - ")[0]);
//
//            // Obtener ID del motivo
//            int idMotivo = cmbMotivo.getValue().getIdMotivo();
//
//            // Obtener ID del comprobante
//            Integer idComprobante = null;
//            if (cmbComprobante.getValue() != null && !cmbComprobante.getValue().equals("NINGUNO")) {
//                idComprobante = Integer.parseInt(cmbComprobante.getValue().split(" - ")[0]);
//            }
//
//            // Calcular monto total a devolver
//            BigDecimal montoTotal = BigDecimal.ZERO;
//            for (DetalleDevolucion dd : listaDevolucion) {
//                montoTotal = montoTotal.add(dd.getSubtotal());
//            }
//
//            // Insertar cabecera de devolución
//            String sqlDevolucion = "INSERT INTO tbl_DEVOLUCION (id_venta, id_empleado, id_motivo, id_comprobante, fecha, monto_devuelto, observacion) " +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//            PreparedStatement psDev = conexion.prepareStatement(sqlDevolucion, Statement.RETURN_GENERATED_KEYS);
//            psDev.setInt(1, ventaSeleccionadaId);
//            psDev.setInt(2, idEmpleado);
//            psDev.setInt(3, idMotivo);
//            if (idComprobante != null) {
//                psDev.setInt(4, idComprobante);
//            } else {
//                psDev.setNull(4, Types.INTEGER);
//            }
//            psDev.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
//            psDev.setBigDecimal(6, montoTotal);
//            psDev.setString(7, txtObservacion.getText());
//            psDev.executeUpdate();
//
//            ResultSet keys = psDev.getGeneratedKeys();
//            int idDevolucion = keys.next() ? keys.getInt(1) : -1;
//
//            // Insertar detalles y actualizar inventario
//            String sqlDetalle = "INSERT INTO tbl_DETALLE_DEVOLUCION (id_devolucion, id_producto, cantidad, precio_unitario, subtotal, lote) " +
//                    "VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement psDet = conexion.prepareStatement(sqlDetalle);
//
//            String sqlUpdateStock = "UPDATE tbl_PRODUCTO SET stock_actual = stock_actual + ? WHERE id_producto = ?";
//            PreparedStatement psStock = conexion.prepareStatement(sqlUpdateStock);
//
//            for (DetalleDevolucion dd : listaDevolucion) {
//                // Insertar detalle
//                psDet.setInt(1, idDevolucion);
//                psDet.setInt(2, dd.getIdProducto());
//                psDet.setInt(3, dd.getCantidad());
//                psDet.setBigDecimal(4, dd.getPrecioUnitario());
//                psDet.setBigDecimal(5, dd.getSubtotal());
//                psDet.setString(6, dd.getLote());
//                psDet.executeUpdate();
//
//                // Actualizar stock
//                psStock.setInt(1, dd.getCantidad());
//                psStock.setInt(2, dd.getIdProducto());
//                psStock.executeUpdate();
//            }
//
//            conexion.commit();
//
//            mostrarExito("Devolución procesada correctamente. ID: " + idDevolucion);
//            limpiarFormulario();
//            cargarHistorial();
//            cargarVentas();
//
//        } catch (SQLException e) {
//            try {
//                conexion.rollback();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//            mostrarError("Error al procesar devolución: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            try {
//                conexion.setAutoCommit(true);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @FXML
//    private void limpiarFormulario() {
//        ventaSeleccionadaId = 0;
//        txtBuscarVenta.clear();
//        listaVentas.clear();
//        listaDetalles.clear();
//        listaDevolucion.clear();
//        cmbEmpleado.setValue(null);
//        cmbMotivo.setValue(null);
//        cmbComprobante.setValue("NINGUNO");
//        txtObservacion.clear();
//        lblMontoDevolucion.setText("RD$ 0.00");
//        cargarVentas();
//    }
//
//    private void mostrarError(String mensaje) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Error");
//        alert.setHeaderText(null);
//        alert.setContentText(mensaje);
//        alert.showAndWait();
//    }
//
//    private void mostrarExito(String mensaje) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Éxito");
//        alert.setHeaderText(null);
//        alert.setContentText(mensaje);
//        alert.showAndWait();
//    }
//}
