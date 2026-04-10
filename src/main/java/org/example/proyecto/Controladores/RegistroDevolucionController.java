package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Devolucion;
import org.example.proyecto.Modelos.Motivo;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegistroDevolucionController implements Initializable {

    // ── Búsqueda ──────────────────────────────────────────────────
    @FXML private TextField txtBuscar;

    // ── Tabla historial ───────────────────────────────────────────
    @FXML private TableView<Devolucion>               tblDevoluciones;
    @FXML private TableColumn<Devolucion, Integer>    colId;
    @FXML private TableColumn<Devolucion, String>     colProducto;
    @FXML private TableColumn<Devolucion, String>     colEmpleado;
    @FXML private TableColumn<Devolucion, String>     colFecha;
    @FXML private TableColumn<Devolucion, BigDecimal> colMonto;
    @FXML private TableColumn<Devolucion, String>     colMotivo;
    @FXML private TableColumn<Devolucion, String>     colEstado;
    @FXML private TableColumn<Devolucion, String>     colObservacion;
    // ← NUEVO: columna monto nota crédito
    @FXML private TableColumn<Devolucion, BigDecimal> colMontoNotaCredito;

    // ── Formulario ────────────────────────────────────────────────
    // ── Formulario ────────────────────────────────────────────────
    @FXML private TextField txtIdVenta;
    @FXML private Label lblInfoVenta;
    @FXML private ComboBox<String>  cmbCliente;
    @FXML private ComboBox<String>  cmbEmpleado;
    @FXML private ComboBox<String>  cmbEstado;
    @FXML private ComboBox<Motivo>  cmbMotivo;
    // ← Ahora muestra "ID - monto" en lugar de "ID - numero"
    @FXML private ComboBox<String>  cmbNotaCredito;
    @FXML private ComboBox<String>  cmbProducto;
    @FXML private DatePicker        dateFecha;
    // ← monto ahora es opcional
    @FXML private TextField         txtMonto;
    @FXML private TextField         txtObservacion;

    // ── Estado interno ────────────────────────────────────────────
    private Connection conexion;
    private int idDevolucionSeleccionada = 0;
    private final ObservableList<Devolucion> listaDevoluciones = FXCollections.observableArrayList();
    private final ObservableList<Motivo>     listaMotivos      = FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarClientes();
            cargarEmpleados();
            cargarMotivos();
            cargarNotasCredito();   // ← ahora carga monto en lugar de numero
            cargarProductos();

            cmbEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE", "APROBADA", "RECHAZADA", "PROCESADA"));
            cmbEstado.setValue("PENDIENTE");

            dateFecha.setValue(LocalDate.now());

            configurarTabla();
            cargarDevoluciones();
            configurarSeleccionTabla();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

    private void cargarMotivos() {
        listaMotivos.clear();
        String sql = "SELECT id_motivo, nombre, tipo, estado FROM tbl_MOTIVO WHERE estado = 1 ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Motivo m = new Motivo();
                m.setIdMotivo(rs.getInt("id_motivo"));
                m.setNombre(rs.getString("nombre"));
                m.setTipo(rs.getString("tipo"));
                m.setEstado(rs.getBoolean("estado"));
                listaMotivos.add(m);
            }
            cmbMotivo.setItems(listaMotivos);
            cmbMotivo.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(Motivo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbMotivo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(Motivo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar motivos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Carga las notas de crédito mostrando su monto en lugar del número.
     * Formato de cada ítem: "ID - RD$ monto"
     */
    private void cargarNotasCredito() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNA");
        // ← Cambio clave: usa columna 'monto' en vez de 'numero'
        String sql = "SELECT id_nota_credito, monto FROM tbl_NOTA_CREDITO WHERE estado = 'ACTIVA' ORDER BY id_nota_credito";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                BigDecimal monto = rs.getBigDecimal("monto");
                String montoFmt = monto != null ? String.format("%.2f", monto) : "0.00";
                lista.add(rs.getInt("id_nota_credito") + " - RD$ " + montoFmt);
            }
        } catch (SQLException e) {
            System.out.println("Nota: tabla tbl_NOTA_CREDITO no disponible: " + e.getMessage());
        }
        cmbNotaCredito.setItems(lista);
        cmbNotaCredito.setValue("NINGUNA");
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

    // ── Tabla ────────────────────────────────────────────────────

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDevolucion"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoDevuelto"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoNombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));

        colMontoNotaCredito.setCellValueFactory(new PropertyValueFactory<>("montoNotaCredito"));
        colMontoNotaCredito.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("—");
                else setText("RD$ " + String.format("%.2f", item));
            }
        });

        tblDevoluciones.setItems(listaDevoluciones);
    }

    private void configurarSeleccionTabla() {
        tblDevoluciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) cargarDatosEnFormulario(newVal);
                });
    }

    private void cargarDatosEnFormulario(Devolucion d) {
        idDevolucionSeleccionada = d.getIdDevolucion();

        // ← NUEVO: cargar ID venta
        txtIdVenta.setText(d.getIdVenta() > 0 ? String.valueOf(d.getIdVenta()) : "");
        if (d.getIdVenta() > 0) {
            // Opcional: validar automáticamente al cargar
            validarVentaCargada(d.getIdVenta());
        }

        // ... resto del código existente ...
    }

    // Método auxiliar para validar venta al cargar desde tabla
    private void validarVentaCargada(int idVenta) {
        String sql = "SELECT c.nombres as cliente_nombre, v.total, v.fecha " +
                "FROM tbl_VENTA v " +
                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
                "WHERE v.id_venta = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String cliente = rs.getString("cliente_nombre");
                BigDecimal total = rs.getBigDecimal("total");
                Date fecha = rs.getDate("fecha");
                lblInfoVenta.setText(String.format("✓ Venta: %d | Cliente: %s | Total: RD$ %.2f",
                        idVenta, cliente != null ? cliente : "N/A", total != null ? total : 0));
                lblInfoVenta.setStyle("-fx-text-fill: #2E7D32;");
            }
        } catch (SQLException e) {
            lblInfoVenta.setText("Error al cargar información de venta");
        }
    }

    // ── Carga / búsqueda ─────────────────────────────────────────

    @FXML
    private void cargarDevoluciones() {
        listaDevoluciones.clear();
        // ← JOIN con tbl_NOTA_CREDITO para traer el monto
        String sql = "SELECT d.*, e.nombres AS nombre_empleado, m.nombre AS motivo_nombre, " +
                "       p.nombre AS nombre_producto, nc.monto AS monto_nota_credito " +
                "FROM tbl_DEVOLUCION d " +
                "LEFT JOIN tbl_EMPLEADO    e  ON d.id_empleado    = e.id_empleado " +
                "LEFT JOIN tbl_MOTIVO      m  ON d.id_motivo      = m.id_motivo " +
                "LEFT JOIN tbl_PRODUCTO    p  ON d.id_producto    = p.id_producto " +
                "LEFT JOIN tbl_NOTA_CREDITO nc ON d.id_nota_credito = nc.id_nota_credito " +
                "ORDER BY d.id_devolucion DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) listaDevoluciones.add(mapear(rs));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar devoluciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Devolucion mapear(ResultSet rs) throws SQLException {
        Devolucion d = new Devolucion();
        d.setIdDevolucion(rs.getInt("id_devolucion"));
        d.setIdVenta(rs.getInt("id_venta"));
        d.setIdEmpleado(rs.getInt("id_empleado"));
        d.setIdMotivo(rs.getInt("id_motivo"));
        d.setIdProducto(rs.getInt("id_producto"));
        d.setNombreProducto(rs.getString("nombre_producto"));
        // ← usa id_nota_credito en vez de id_comprobante
        d.setIdNotaCredito(rs.getObject("id_nota_credito") != null
                ? rs.getInt("id_nota_credito") : null);
        d.setMontoNotaCredito(rs.getBigDecimal("monto_nota_credito"));  // ← NUEVO
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) d.setFecha(ts.toLocalDateTime());
        d.setMontoDevuelto(rs.getBigDecimal("monto_devuelto"));
        d.setObservacion(rs.getString("observacion"));
        d.setEstado(rs.getString("estado"));
        d.setMotivoNombre(rs.getString("motivo_nombre"));
        d.setNombreEmpleado(rs.getString("nombre_empleado"));
        return d;
    }

    @FXML
    private void buscarDevolucion() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarDevoluciones(); return; }

        listaDevoluciones.clear();
        String sql = "SELECT d.*, e.nombres AS nombre_empleado, m.nombre AS motivo_nombre, " +
                "       p.nombre AS nombre_producto, nc.monto AS monto_nota_credito " +
                "FROM tbl_DEVOLUCION d " +
                "LEFT JOIN tbl_EMPLEADO    e  ON d.id_empleado    = e.id_empleado " +
                "LEFT JOIN tbl_MOTIVO      m  ON d.id_motivo      = m.id_motivo " +
                "LEFT JOIN tbl_PRODUCTO    p  ON d.id_producto    = p.id_producto " +
                "LEFT JOIN tbl_NOTA_CREDITO nc ON d.id_nota_credito = nc.id_nota_credito " +
                "WHERE e.nombres LIKE ? OR m.nombre LIKE ? OR d.estado LIKE ? " +
                "   OR d.observacion LIKE ? OR p.nombre LIKE ? " +
                "ORDER BY d.id_devolucion DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like); ps.setString(2, like);
            ps.setString(3, like); ps.setString(4, like);
            ps.setString(5, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) listaDevoluciones.add(mapear(rs));
            if (listaDevoluciones.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados para: " + filtro,
                        Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarDevoluciones();
    }

    // ── CRUD ─────────────────────────────────────────────────────

    @FXML
    private void guardarDevolucion() {
        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_DEVOLUCION " +
                "(id_venta, id_producto, id_cliente, id_empleado, id_motivo, id_nota_credito, " +
                " fecha, monto_devuelto, observacion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarDevoluciones();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarDevolucion() {
        if (idDevolucionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una devolución de la tabla para editar.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setHeaderText(null);
        conf.setContentText("¿Desea guardar los cambios en esta devolución?");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        String sql = "UPDATE tbl_DEVOLUCION " +
                "SET id_venta=?, id_producto=?, id_cliente=?, id_empleado=?, id_motivo=?, id_nota_credito=?, " +
                "    fecha=?, monto_devuelto=?, observacion=?, estado=? " +
                "WHERE id_devolucion=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(10, idDevolucionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarDevoluciones();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarDevolucion() {
        if (idDevolucionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una devolución de la tabla para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea eliminar esta devolución?\nEsta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM tbl_DEVOLUCION WHERE id_devolucion=?")) {
            ps.setInt(1, idDevolucionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución eliminada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarDevoluciones();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la devolución tiene registros asociados.",
                    Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {

        String idVentaText = txtIdVenta.getText().trim();
        if (!idVentaText.isEmpty()) {
            try {
                ps.setInt(1, Integer.parseInt(idVentaText));
            } catch (NumberFormatException e) {
                throw new SQLException("ID de venta inválido");
            }
        } else {
            ps.setNull(1, Types.INTEGER);
        }

        // 1 id_producto (antes era posición 1, ahora es 2)
        String prodVal = cmbProducto.getValue();
        if (prodVal != null && !prodVal.equals("NINGUNO"))
            ps.setInt(2, Integer.parseInt(prodVal.split(" - ")[0]));
        else
            ps.setNull(2, Types.INTEGER);

        // 2 id_cliente (antes 2, ahora 3)
        // ... y así sucesivamente, ajustando todos los índices +1
    }

    @FXML
    private void limpiarCampos() {
            idDevolucionSeleccionada = 0;
            txtIdVenta.clear();                    // ← NUEVO
            lblInfoVenta.setText("Ingrese ID de venta y valide");  // ← NUEVO
            lblInfoVenta.setStyle("-fx-font-size: 11px; -fx-text-fill: #7FA8C9;");
        idDevolucionSeleccionada = 0;
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbMotivo.setValue(null);
        cmbEstado.setValue("PENDIENTE");
        cmbNotaCredito.setValue("NINGUNA");
        cmbProducto.setValue("NINGUNO");
        dateFecha.setValue(LocalDate.now());
        txtMonto.clear();
        txtObservacion.clear();
        txtBuscar.clear();
        tblDevoluciones.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
            if (txtIdVenta.getText().trim().isEmpty()) {
                mostrarAlerta("Validación", "El ID de venta es obligatorio", Alert.AlertType.WARNING);
                txtIdVenta.requestFocus();
                return false;
            }

            try {
                int idVenta = Integer.parseInt(txtIdVenta.getText().trim());
                if (idVenta <= 0) {
                    mostrarAlerta("Validación", "El ID de venta debe ser un número positivo", Alert.AlertType.WARNING);
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Validación", "El ID de venta debe ser un número válido", Alert.AlertType.WARNING);
                return false;
            }

        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el empleado que procesa.", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbMotivo.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el motivo de devolución.", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el estado.", Alert.AlertType.WARNING);
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha.", Alert.AlertType.WARNING);
            return false;
        }
        // ← monto es OPCIONAL; solo se valida el formato si el campo tiene algo
        String montoTxt = txtMonto.getText().trim();
        if (!montoTxt.isEmpty()) {
            try {
                if (new BigDecimal(montoTxt).compareTo(BigDecimal.ZERO) < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarAlerta("Validación", "Si ingresa un monto, debe ser un número mayor o igual a 0.",
                        Alert.AlertType.WARNING);
                txtMonto.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
    // ── Validación de venta ──────────────────────────────────────
    @FXML
    private void buscarVentaPorId() {
        String idVentaText = txtIdVenta.getText().trim();
        if (idVentaText.isEmpty()) {
            mostrarAlerta("Validación", "Ingrese el ID de la venta", Alert.AlertType.WARNING);
            return;
        }

        int idVenta;
        try {
            idVenta = Integer.parseInt(idVentaText);
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El ID de venta debe ser un número válido", Alert.AlertType.WARNING);
            return;
        }

        // Consultar la venta para validar que existe
        String sql = "SELECT v.id_venta, c.nombres as cliente_nombre, v.total, v.fecha " +
                "FROM tbl_VENTA v " +
                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
                "WHERE v.id_venta = ? AND v.estado = 1";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String cliente = rs.getString("cliente_nombre");
                BigDecimal total = rs.getBigDecimal("total");
                Date fecha = rs.getDate("fecha");

                lblInfoVenta.setText(String.format("✓ Venta válida | Cliente: %s | Total: RD$ %.2f | Fecha: %s",
                        cliente != null ? cliente : "N/A",
                        total != null ? total : 0,
                        fecha != null ? fecha.toString() : "N/A"));
                lblInfoVenta.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");

                // Opcional: auto-completar el cliente si la venta tiene cliente asociado
                if (cliente != null && cmbCliente != null) {
                    cmbCliente.getItems().stream()
                            .filter(s -> s.contains(cliente))
                            .findFirst()
                            .ifPresent(cmbCliente::setValue);
                }
            } else {
                lblInfoVenta.setText("✗ Venta no encontrada o inactiva");
                lblInfoVenta.setStyle("-fx-text-fill: #E53935; -fx-font-weight: bold;");
                txtIdVenta.requestFocus();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al validar venta: " + e.getMessage(), Alert.AlertType.ERROR);
            lblInfoVenta.setText("Error al validar venta");
            lblInfoVenta.setStyle("-fx-text-fill: #E53935; -fx-font-weight: bold;");
        }
    }
}