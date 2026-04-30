package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Motivo;
import org.example.proyecto.Modelos.Reclamacion;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroReclamacionController implements Initializable {

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

    // Tabla
    @FXML private TableView<Reclamacion> tblReclamaciones;
    @FXML private TableColumn<Reclamacion, Integer> colId;
    @FXML private TableColumn<Reclamacion, String> colCliente;
    @FXML private TableColumn<Reclamacion, String> colEmpleado;
    @FXML private TableColumn<Reclamacion, String> colMotivo;
    @FXML private TableColumn<Reclamacion, String> colFecha;
    @FXML private TableColumn<Reclamacion, String> colDescripcion;
    @FXML private TableColumn<Reclamacion, String> colEstado;

    // Formulario
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<Motivo> cmbMotivo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dateFecha;
    @FXML private TextArea txtDescripcion;

    // Estado interno
    private Connection conexion;
    private int idReclamacionSeleccionada = 0;
    private final ObservableList<Reclamacion> listaReclamaciones = FXCollections.observableArrayList();
    private final ObservableList<Motivo> listaMotivos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarTabla();
        cargarClientes();
        cargarEmpleados();
        cargarMotivos();

        cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "EN_PROCESO", "RESUELTA", "RECHAZADA"));
        cmbEstado.setValue("PENDIENTE");

        dateFecha.setValue(LocalDate.now());

        configurarSeleccionTabla();
        configurarBotonesPorRol();

        // Inicialmente el panel de consulta está oculto
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);

        // Botones de edición deshabilitados al inicio
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
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

    @FXML
    private void abrirConsulta() {
        consultaPanel.setVisible(true);
        consultaPanel.setManaged(true);
        cargarReclamaciones();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReclamacion"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("nombreMotivo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));

        tblReclamaciones.setItems(listaReclamaciones);
    }

    private void cargarClientes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_cliente, nombres, apellidos FROM tbl_CLIENTE WHERE estado = 1 ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String nombre = rs.getString("nombres") + " " + rs.getString("apellidos");
                lista.add(rs.getInt("id_cliente") + " - " + nombre);
            }
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
        String sql = "SELECT id_motivo, nombre, tipo, estado FROM tbl_MOTIVO WHERE estado = 1 AND tipo = 'RECLAMACION' ORDER BY nombre";
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

    private void cargarReclamaciones() {
        listaReclamaciones.clear();
        String sql = "SELECT r.*, " +
                "CONCAT(c.nombres, ' ', c.apellidos) as nombre_cliente, " +
                "e.nombres as nombre_empleado, " +
                "m.nombre as nombre_motivo " +
                "FROM tbl_RECLAMACION r " +
                "LEFT JOIN tbl_CLIENTE c ON r.id_cliente = c.id_cliente " +
                "LEFT JOIN tbl_EMPLEADO e ON r.id_empleado = e.id_empleado " +
                "LEFT JOIN tbl_MOTIVO m ON r.id_motivo = m.id_motivo " +
                "ORDER BY r.id_reclamacion DESC";

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reclamacion reclamacion = new Reclamacion();
                reclamacion.setIdReclamacion(rs.getInt("id_reclamacion"));
                reclamacion.setIdCliente(rs.getInt("id_cliente"));
                reclamacion.setIdEmpleado(rs.getInt("id_empleado"));
                reclamacion.setIdMotivo(rs.getInt("id_motivo"));
                reclamacion.setDescripcion(rs.getString("descripcion"));
                reclamacion.setEstado(rs.getString("estado"));

                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) reclamacion.setFecha(ts.toLocalDateTime());

                reclamacion.setNombreCliente(rs.getString("nombre_cliente"));
                reclamacion.setNombreEmpleado(rs.getString("nombre_empleado"));
                reclamacion.setNombreMotivo(rs.getString("nombre_motivo"));

                listaReclamaciones.add(reclamacion);
            }
            tblReclamaciones.setItems(listaReclamaciones);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar reclamaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblReclamaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idReclamacionSeleccionada = newSelection.getIdReclamacion();
                cargarReclamacionEnFormulario(newSelection);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarReclamacionEnFormulario(Reclamacion r) {
        // Cargar cliente
        cmbCliente.getItems().stream()
                .filter(item -> item.startsWith(r.getIdCliente() + " - "))
                .findFirst()
                .ifPresent(cmbCliente::setValue);

        // Cargar empleado
        cmbEmpleado.getItems().stream()
                .filter(item -> item.startsWith(r.getIdEmpleado() + " - "))
                .findFirst()
                .ifPresent(cmbEmpleado::setValue);

        // Cargar motivo
        Motivo motivoBuscado = listaMotivos.stream()
                .filter(m -> m.getIdMotivo() == r.getIdMotivo())
                .findFirst()
                .orElse(null);
        cmbMotivo.setValue(motivoBuscado);

        // Cargar fecha
        if (r.getFecha() != null) {
            dateFecha.setValue(r.getFecha().toLocalDate());
        }

        // Cargar descripción
        txtDescripcion.setText(r.getDescripcion());

        // Cargar estado
        cmbEstado.setValue(r.getEstado());
    }

    @FXML
    private void guardarReclamacion(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar reclamaciones", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        if (modoEdicion && idReclamacionSeleccionada != 0) {
            actualizarReclamacion();
        } else {
            insertarReclamacion();
        }
    }

    private void insertarReclamacion() {
        String sql = "INSERT INTO tbl_RECLAMACION (id_cliente, id_empleado, id_motivo, fecha, descripcion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setearParametros(ps);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                mostrarAlerta("Éxito", "Reclamación guardada correctamente (ID: " + rs.getInt(1) + ")", Alert.AlertType.INFORMATION);
            }
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarReclamaciones();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarReclamacion() {
        String sql = "UPDATE tbl_RECLAMACION SET id_cliente=?, id_empleado=?, id_motivo=?, fecha=?, descripcion=?, estado=? WHERE id_reclamacion=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(7, idReclamacionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Reclamación actualizada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarReclamaciones();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        // id_cliente
        String clienteVal = cmbCliente.getValue();
        ps.setInt(1, Integer.parseInt(clienteVal.split(" - ")[0]));

        // id_empleado
        String empleadoVal = cmbEmpleado.getValue();
        ps.setInt(2, Integer.parseInt(empleadoVal.split(" - ")[0]));

        // id_motivo
        Motivo motivo = cmbMotivo.getValue();
        ps.setInt(3, motivo.getIdMotivo());

        // fecha
        LocalDateTime fechaHora = LocalDateTime.of(dateFecha.getValue(), LocalTime.now());
        ps.setTimestamp(4, Timestamp.valueOf(fechaHora));

        // descripcion
        ps.setString(5, txtDescripcion.getText().trim());

        // estado
        ps.setString(6, cmbEstado.getValue());
    }

    @FXML
    private void editarReclamacion(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar reclamaciones", Alert.AlertType.ERROR);
            return;
        }

        if (idReclamacionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una reclamación para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarReclamacion(event);
    }

    @FXML
    private void eliminarReclamacion(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar reclamaciones", Alert.AlertType.ERROR);
            return;
        }

        if (idReclamacionSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una reclamación para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro de eliminar esta reclamación?");
        conf.setContentText("Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_RECLAMACION WHERE id_reclamacion=?")) {
            ps.setInt(1, idReclamacionSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Reclamación eliminada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarReclamaciones();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la reclamación tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idReclamacionSeleccionada = 0;
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbMotivo.setValue(null);
        cmbEstado.setValue("PENDIENTE");
        dateFecha.setValue(LocalDate.now());
        txtDescripcion.clear();
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblReclamaciones.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarReclamacion(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarReclamaciones();
            return;
        }

        listaReclamaciones.clear();
        String sql = "SELECT r.*, " +
                "CONCAT(c.nombres, ' ', c.apellidos) as nombre_cliente, " +
                "e.nombres as nombre_empleado, " +
                "m.nombre as nombre_motivo " +
                "FROM tbl_RECLAMACION r " +
                "LEFT JOIN tbl_CLIENTE c ON r.id_cliente = c.id_cliente " +
                "LEFT JOIN tbl_EMPLEADO e ON r.id_empleado = e.id_empleado " +
                "LEFT JOIN tbl_MOTIVO m ON r.id_motivo = m.id_motivo " +
                "WHERE CONCAT(c.nombres, ' ', c.apellidos) LIKE ? " +
                "OR e.nombres LIKE ? " +
                "OR m.nombre LIKE ? " +
                "OR r.estado LIKE ? " +
                "OR r.descripcion LIKE ? " +
                "ORDER BY r.id_reclamacion DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclamacion reclamacion = new Reclamacion();
                reclamacion.setIdReclamacion(rs.getInt("id_reclamacion"));
                reclamacion.setIdCliente(rs.getInt("id_cliente"));
                reclamacion.setIdEmpleado(rs.getInt("id_empleado"));
                reclamacion.setIdMotivo(rs.getInt("id_motivo"));
                reclamacion.setDescripcion(rs.getString("descripcion"));
                reclamacion.setEstado(rs.getString("estado"));

                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) reclamacion.setFecha(ts.toLocalDateTime());

                reclamacion.setNombreCliente(rs.getString("nombre_cliente"));
                reclamacion.setNombreEmpleado(rs.getString("nombre_empleado"));
                reclamacion.setNombreMotivo(rs.getString("nombre_motivo"));

                listaReclamaciones.add(reclamacion);
            }
            tblReclamaciones.setItems(listaReclamaciones);

            if (listaReclamaciones.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron resultados para: " + filtro, Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarReclamaciones();
    }

    private boolean validarCampos() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un cliente", Alert.AlertType.WARNING);
            cmbCliente.requestFocus();
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un empleado", Alert.AlertType.WARNING);
            cmbEmpleado.requestFocus();
            return false;
        }
        if (cmbMotivo.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un motivo", Alert.AlertType.WARNING);
            cmbMotivo.requestFocus();
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha", Alert.AlertType.WARNING);
            dateFecha.requestFocus();
            return false;
        }
        if (txtDescripcion.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "La descripción es obligatoria", Alert.AlertType.WARNING);
            txtDescripcion.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el estado", Alert.AlertType.WARNING);
            cmbEstado.requestFocus();
            return false;
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
}