package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Devolucion;
import org.example.proyecto.Modelos.Motivo;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;
import org.example.proyecto.util.EmailUtil;
import org.example.proyecto.util.ReportUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroDevolucionController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;

    // Botones de acción
    @FXML private Button btnConsultar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnEnviarCorreo;

    // Tabla historial
    @FXML private TableView<Devolucion> tblDevoluciones;
    @FXML private TableColumn<Devolucion, Integer> colId;
    @FXML private TableColumn<Devolucion, Integer> colIdVenta;
    @FXML private TableColumn<Devolucion, String> colProducto;
    @FXML private TableColumn<Devolucion, String> colEmpleado;
    @FXML private TableColumn<Devolucion, String> colFecha;
    @FXML private TableColumn<Devolucion, BigDecimal> colMonto;
    @FXML private TableColumn<Devolucion, String> colMotivo;
    @FXML private TableColumn<Devolucion, String> colEstado;
    @FXML private TableColumn<Devolucion, String> colObservacion;
    @FXML private TableColumn<Devolucion, BigDecimal> colMontoNotaCredito;

    // Formulario
    @FXML private ComboBox<String> cmbVenta;
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Motivo> cmbMotivo;
    @FXML private ComboBox<String> cmbNotaCredito;
    @FXML private ComboBox<String> cmbProducto;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtMonto;
    @FXML private TextField txtObservacion;

    // Estado interno
    private Connection conexion;
    private int idDevolucionSeleccionada = 0;
    private final ObservableList<Devolucion> listaDevoluciones = FXCollections.observableArrayList();
    private final ObservableList<Motivo> listaMotivos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    // Clase interna para almacenar información de venta
    private static class VentaInfo {
        int idVenta;
        int idCliente;
        String clienteNombre;
        BigDecimal total;

        VentaInfo(int idVenta, int idCliente, String clienteNombre, BigDecimal total) {
            this.idVenta = idVenta;
            this.idCliente = idCliente;
            this.clienteNombre = clienteNombre;
            this.total = total;
        }

        @Override
        public String toString() {
            return idVenta + " - " + clienteNombre + " - RD$ " + String.format("%.2f", total);
        }
    }

    private final ObservableList<VentaInfo> listaVentas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarVentas();
            cargarClientes();
            cargarEmpleados();
            cargarMotivos();
            cargarNotasCredito();
            cargarProductos();

            cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PROCESADA", "ANULADA"));
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

            // Listener para cuando seleccionan una venta, auto-completar cliente
            cmbVenta.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    autoCompletarPorVenta();
                }
            });

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

    @FXML
    private void abrirConsulta() {
        consultaPanel.setVisible(true);
        consultaPanel.setManaged(true);
        cargarDevoluciones();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    // Carga de combos
    private void cargarVentas() {
        listaVentas.clear();
        String sql = "SELECT v.id_venta, v.id_cliente, c.nombres as cliente_nombre, v.total " +
                "FROM tbl_VENTA v " +
                "LEFT JOIN tbl_CLIENTE c ON v.id_cliente = c.id_cliente " +
                "ORDER BY v.id_venta DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                VentaInfo venta = new VentaInfo(
                        rs.getInt("id_venta"),
                        rs.getInt("id_cliente"),
                        rs.getString("cliente_nombre") != null ? rs.getString("cliente_nombre") : "Sin cliente",
                        rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO
                );
                listaVentas.add(venta);
            }
            cmbVenta.setItems(FXCollections.observableArrayList(
                    listaVentas.stream().map(VentaInfo::toString).collect(java.util.stream.Collectors.toList())
            ));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar ventas: " + e.getMessage(), Alert.AlertType.ERROR);
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
                @Override
                protected void updateItem(Motivo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbMotivo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Motivo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar motivos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarNotasCredito() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNA");
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

    private void autoCompletarPorVenta() {
        String ventaSeleccionada = cmbVenta.getValue();
        if (ventaSeleccionada == null) return;

        int idVenta = Integer.parseInt(ventaSeleccionada.split(" - ")[0]);

        VentaInfo ventaEncontrada = listaVentas.stream()
                .filter(v -> v.idVenta == idVenta)
                .findFirst()
                .orElse(null);

        if (ventaEncontrada != null) {
            String clienteBuscado = cmbCliente.getItems().stream()
                    .filter(item -> item.startsWith(ventaEncontrada.idCliente + " - "))
                    .findFirst()
                    .orElse(null);
            if (clienteBuscado != null) {
                cmbCliente.setValue(clienteBuscado);
            }
        }
    }

    // Tabla
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDevolucion"));
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
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
                    if (newVal != null) {
                        cargarDatosEnFormulario(newVal);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                });
    }

    private void cargarDatosEnFormulario(Devolucion d) {
        idDevolucionSeleccionada = d.getIdDevolucion();

        if (d.getIdVenta() > 0) {
            String ventaBuscada = cmbVenta.getItems().stream()
                    .filter(item -> item.startsWith(d.getIdVenta() + " - "))
                    .findFirst()
                    .orElse(null);
            cmbVenta.setValue(ventaBuscada);
        }

        if (d.getIdCliente() > 0) {
            String clienteBuscado = cmbCliente.getItems().stream()
                    .filter(item -> item.startsWith(d.getIdCliente() + " - "))
                    .findFirst()
                    .orElse(null);
            cmbCliente.setValue(clienteBuscado);
        }

        if (d.getIdEmpleado() > 0) {
            String empleadoBuscado = cmbEmpleado.getItems().stream()
                    .filter(item -> item.startsWith(d.getIdEmpleado() + " - "))
                    .findFirst()
                    .orElse(null);
            cmbEmpleado.setValue(empleadoBuscado);
        }

        if (d.getIdMotivo() > 0) {
            Motivo motivoBuscado = listaMotivos.stream()
                    .filter(m -> m.getIdMotivo() == d.getIdMotivo())
                    .findFirst()
                    .orElse(null);
            cmbMotivo.setValue(motivoBuscado);
        }

        if (d.getIdProducto() > 0 && d.getNombreProducto() != null) {
            String productoBuscado = d.getIdProducto() + " - " + d.getNombreProducto();
            cmbProducto.setValue(productoBuscado);
        } else {
            cmbProducto.setValue("NINGUNO");
        }

        if (d.getIdNotaCredito() != null && d.getIdNotaCredito() > 0) {
            String notaBuscada = cmbNotaCredito.getItems().stream()
                    .filter(item -> item.startsWith(d.getIdNotaCredito() + " - "))
                    .findFirst()
                    .orElse("NINGUNA");
            cmbNotaCredito.setValue(notaBuscada);
        } else {
            cmbNotaCredito.setValue("NINGUNA");
        }

        if (d.getFecha() != null) {
            dateFecha.setValue(d.getFecha().toLocalDate());
        }

        if (d.getMontoDevuelto() != null) {
            txtMonto.setText(String.format("%.2f", d.getMontoDevuelto()));
        } else {
            txtMonto.clear();
        }

        txtObservacion.setText(d.getObservacion() != null ? d.getObservacion() : "");
        cmbEstado.setValue(d.getEstado() != null ? d.getEstado() : "PENDIENTE");
    }

    // Carga / búsqueda
    @FXML
    private void cargarDevoluciones() {
        listaDevoluciones.clear();
        String sql = "SELECT d.*, e.nombres AS nombre_empleado, m.nombre AS motivo_nombre, " +
                "       p.nombre AS nombre_producto, nc.monto AS monto_nota_credito, " +
                "       d.id_cliente " +
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
        d.setIdCliente(rs.getInt("id_cliente"));
        d.setIdEmpleado(rs.getInt("id_empleado"));
        d.setIdMotivo(rs.getInt("id_motivo"));
        d.setIdProducto(rs.getInt("id_producto"));
        d.setNombreProducto(rs.getString("nombre_producto"));
        d.setIdNotaCredito(rs.getObject("id_nota_credito") != null ? rs.getInt("id_nota_credito") : null);
        d.setMontoNotaCredito(rs.getBigDecimal("monto_nota_credito"));
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
                "       p.nombre AS nombre_producto, nc.monto AS monto_nota_credito, " +
                "       d.id_cliente " +
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
                mostrarAlerta("Información", "No se encontraron resultados para: " + filtro, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarDevoluciones();
    }

    // CRUD
    @FXML
    private void guardarDevolucion() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar devoluciones", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_DEVOLUCION " +
                "(id_venta, id_producto, id_cliente, id_empleado, id_motivo, id_nota_credito, " +
                " fecha, monto_devuelto, observacion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarDevoluciones();
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarDevolucion() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar devoluciones", Alert.AlertType.ERROR);
            return;
        }

        if (idDevolucionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una devolución de la tabla para editar.", Alert.AlertType.WARNING);
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
            ps.setInt(11, idDevolucionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarDevoluciones();
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarDevolucion() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar devoluciones", Alert.AlertType.ERROR);
            return;
        }

        if (idDevolucionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una devolución de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea eliminar esta devolución?\nEsta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_DEVOLUCION WHERE id_devolucion=?")) {
            ps.setInt(1, idDevolucionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Devolución eliminada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarDevoluciones();
                cargarVentas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la devolución tiene registros asociados.", Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        int idx = 1;

        String ventaVal = cmbVenta.getValue();
        if (ventaVal != null && !ventaVal.isEmpty()) {
            ps.setInt(idx++, Integer.parseInt(ventaVal.split(" - ")[0]));
        } else {
            ps.setNull(idx++, Types.INTEGER);
        }

        String prodVal = cmbProducto.getValue();
        if (prodVal != null && !prodVal.equals("NINGUNO"))
            ps.setInt(idx++, Integer.parseInt(prodVal.split(" - ")[0]));
        else
            ps.setNull(idx++, Types.INTEGER);

        String clienteVal = cmbCliente.getValue();
        if (clienteVal != null && !clienteVal.isEmpty())
            ps.setInt(idx++, Integer.parseInt(clienteVal.split(" - ")[0]));
        else
            ps.setNull(idx++, Types.INTEGER);

        String empVal = cmbEmpleado.getValue();
        if (empVal != null && !empVal.isEmpty())
            ps.setInt(idx++, Integer.parseInt(empVal.split(" - ")[0]));
        else
            ps.setNull(idx++, Types.INTEGER);

        Motivo motivo = cmbMotivo.getValue();
        if (motivo != null)
            ps.setInt(idx++, motivo.getIdMotivo());
        else
            ps.setNull(idx++, Types.INTEGER);

        String notaVal = cmbNotaCredito.getValue();
        if (notaVal != null && !notaVal.equals("NINGUNA"))
            ps.setInt(idx++, Integer.parseInt(notaVal.split(" - ")[0]));
        else
            ps.setNull(idx++, Types.INTEGER);

        LocalDate fecha = dateFecha.getValue();
        if (fecha != null)
            ps.setDate(idx++, Date.valueOf(fecha));
        else
            ps.setNull(idx++, Types.DATE);

        String montoTxt = txtMonto.getText().trim();
        if (!montoTxt.isEmpty()) {
            try {
                ps.setBigDecimal(idx++, new BigDecimal(montoTxt));
            } catch (NumberFormatException e) {
                ps.setNull(idx++, Types.DECIMAL);
            }
        } else {
            ps.setNull(idx++, Types.DECIMAL);
        }

        ps.setString(idx++, txtObservacion.getText().trim());

        String estado = cmbEstado.getValue();
        if (estado == null || estado.isEmpty()) estado = "PENDIENTE";
        ps.setString(idx++, estado);
    }

    @FXML
    private void limpiarCampos() {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idDevolucionSeleccionada = 0;
        cmbVenta.setValue(null);
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbMotivo.setValue(null);
        cmbEstado.setValue("PENDIENTE");
        cmbNotaCredito.setValue("NINGUNA");
        cmbProducto.setValue("NINGUNO");
        dateFecha.setValue(LocalDate.now());
        txtMonto.clear();
        txtObservacion.clear();
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblDevoluciones.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbVenta.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la venta asociada.", Alert.AlertType.WARNING);
            cmbVenta.requestFocus();
            return false;
        }
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el cliente.", Alert.AlertType.WARNING);
            cmbCliente.requestFocus();
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el empleado que procesa.", Alert.AlertType.WARNING);
            cmbEmpleado.requestFocus();
            return false;
        }
        if (cmbMotivo.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el motivo de devolución.", Alert.AlertType.WARNING);
            cmbMotivo.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el estado.", Alert.AlertType.WARNING);
            cmbEstado.requestFocus();
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha.", Alert.AlertType.WARNING);
            dateFecha.requestFocus();
            return false;
        }
        String montoTxt = txtMonto.getText().trim();
        if (!montoTxt.isEmpty()) {
            try {
                if (new BigDecimal(montoTxt).compareTo(BigDecimal.ZERO) < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarAlerta("Validación", "Si ingresa un monto, debe ser un número mayor o igual a 0.", Alert.AlertType.WARNING);
                txtMonto.requestFocus();
                return false;
            }
        }
        return true;
    }

    @FXML
    private void generarReporte() {
        Devolucion seleccion = tblDevoluciones.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Seleccionar Devolución", "Debe seleccionar una devolución para generar el reporte.", Alert.AlertType.WARNING);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id_devolucion", seleccion.getIdDevolucion());
        ReportUtil.generarReporte("Devoluciones", "/reportes/ReporteDevoluciones.jasper", params, conexion);
    }

    @FXML
    private void enviarReporte() {
        Devolucion seleccion = tblDevoluciones.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Seleccionar Devolución", "Debe seleccionar una devolución para enviar el reporte.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String correo = "";
            String sql = "SELECT c.correo FROM tbl_CLIENTE c JOIN tbl_DEVOLUCION d ON c.id_cliente = d.id_cliente WHERE d.id_devolucion = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, seleccion.getIdDevolucion());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    correo = rs.getString("correo");
                }
            }
            if (correo == null || correo.trim().isEmpty()) {
                mostrarAlerta("Sin correo", "El cliente no tiene un correo electrónico registrado.", Alert.AlertType.WARNING);
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id_devolucion", seleccion.getIdDevolucion());
            byte[] pdf = ReportUtil.generarReportePDF("/reportes/ReporteDevoluciones.jasper", params, conexion);
            if (pdf == null) return;

            EmailUtil.enviarFacturaPDF(correo,
                    "Devolución #" + seleccion.getIdDevolucion(),
                    "Estimado cliente,\n\nAdjunto encontrará el comprobante de su devolución.\n\nSaludos cordiales.\nFarmacia Kenia Carmen",
                    pdf, "Devolucion_" + seleccion.getIdDevolucion() + ".pdf");

            mostrarAlerta("Correo enviado", "La devolución ha sido enviada exitosamente a: " + correo, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al enviar el correo: " + e.getMessage(), Alert.AlertType.ERROR);
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