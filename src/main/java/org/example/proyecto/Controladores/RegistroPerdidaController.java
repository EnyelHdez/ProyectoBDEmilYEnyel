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
import org.example.proyecto.Modelos.Perdida;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroPerdidaController implements Initializable {

    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar, btnVerTodos, btnCerrarConsulta;
    @FXML private Button btnConsultar, btnLimpiar, btnEliminar, btnEditar, btnGuardar;

    @FXML private TableView<Perdida> tblPerdidas;
    @FXML private TableColumn<Perdida, Integer> colId;
    @FXML private TableColumn<Perdida, String> colEmpleado;
    @FXML private TableColumn<Perdida, String> colFecha;
    @FXML private TableColumn<Perdida, String> colMotivo;
    @FXML private TableColumn<Perdida, BigDecimal> colTotalPerdida;
    @FXML private TableColumn<Perdida, Boolean> colEstado;

    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private DatePicker dateFecha;
    @FXML private TextArea txtMotivo;
    @FXML private TextField txtTotalPerdida;
    @FXML private CheckBox chkEstado;

    private Connection conexion;
    private int idPerdidaSeleccionada = 0;
    private final ObservableList<Perdida> listaPerdidas = FXCollections.observableArrayList();
    private boolean modoEdicion = false;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        cargarEmpleados();
        cargarPerdidas();
        configurarSeleccionTabla();
        configurarBotonesPorRol();
        dateFecha.setValue(LocalDate.now());

        consultaPanel.setVisible(false); consultaPanel.setManaged(false);
        habilitarBotonesEdicion(false); btnGuardar.setDisable(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPerdida"));
        colEmpleado.setCellValueFactory(cellData -> {
            String nombre = obtenerNombreEmpleado(cellData.getValue().getIdEmpleado());
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFecha() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha().format(fmt));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTotalPerdida.setCellValueFactory(new PropertyValueFactory<>("totalPerdida"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<Perdida, Boolean>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item ? "Activo" : "Inactivo"); }
            }
        });
        tblPerdidas.setItems(listaPerdidas);
    }

    private void cargarEmpleados() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            cmbEmpleado.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String obtenerNombreEmpleado(int idEmpleado) {
        String sql = "SELECT nombres FROM tbl_EMPLEADO WHERE id_empleado = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nombres");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return "Desconocido";
    }

    private void cargarPerdidas() {
        listaPerdidas.clear();
        String sql = "SELECT * FROM tbl_PERDIDA ORDER BY id_perdida DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Perdida p = new Perdida(rs.getInt("id_perdida"), rs.getInt("id_empleado"),
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getString("motivo"), rs.getBigDecimal("total_perdida"), rs.getBoolean("estado"));
                listaPerdidas.add(p);
            }
            tblPerdidas.setItems(listaPerdidas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar pérdidas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblPerdidas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idPerdidaSeleccionada = sel.getIdPerdida();
                cargarPerdidaEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarPerdidaEnFormulario(Perdida p) {
        cmbEmpleado.getItems().stream()
                .filter(item -> item.startsWith(p.getIdEmpleado() + " - "))
                .findFirst().ifPresent(cmbEmpleado::setValue);
        if (p.getFecha() != null) dateFecha.setValue(p.getFecha().toLocalDate());
        txtMotivo.setText(p.getMotivo());
        txtTotalPerdida.setText(p.getTotalPerdida() != null ? p.getTotalPerdida().toString() : "");
        chkEstado.setSelected(p.isEstado());
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        boolean puedeEditar = false, puedeEliminar = false;
        if (rol.equals("Administrador")) { puedeEditar = true; puedeEliminar = true; }
        else if (rol.equals("Almacenista")) { puedeEditar = true; puedeEliminar = false; }
        btnEditar.setVisible(puedeEditar); btnEditar.setManaged(puedeEditar);
        btnEliminar.setVisible(puedeEliminar); btnEliminar.setManaged(puedeEliminar);
    }

    private void habilitarBotonesEdicion(boolean habilitar) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!"Administrador".equals(rol) && !"Almacenista".equals(rol)) {
            btnEditar.setDisable(true); btnEliminar.setDisable(true); return;
        }
        btnEditar.setDisable(!habilitar); btnEliminar.setDisable(!habilitar);
    }

    @FXML private void abrirConsulta() { consultaPanel.setVisible(true); consultaPanel.setManaged(true); cargarPerdidas(); }
    @FXML private void cerrarConsulta() { consultaPanel.setVisible(false); consultaPanel.setManaged(false); }

    @FXML private void guardarPerdida(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Almacenista")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos", Alert.AlertType.ERROR); return;
        }
        if (!validarCampos()) return;
        if (modoEdicion && idPerdidaSeleccionada != 0) actualizarPerdida();
        else insertarPerdida();
    }

    private void insertarPerdida() {
        String sql = "INSERT INTO tbl_PERDIDA (id_empleado, fecha, motivo, total_perdida, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setearParametros(ps);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) mostrarAlerta("Éxito", "Pérdida guardada (ID: " + rs.getInt(1) + ")", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarPerdidas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarPerdida() {
        String sql = "UPDATE tbl_PERDIDA SET id_empleado=?, fecha=?, motivo=?, total_perdida=?, estado=? WHERE id_perdida=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(6, idPerdidaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Pérdida actualizada", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarPerdidas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setInt(1, Integer.parseInt(cmbEmpleado.getValue().split(" - ")[0]));
        LocalDateTime fechaHora = LocalDateTime.of(dateFecha.getValue(), LocalTime.now());
        ps.setTimestamp(2, Timestamp.valueOf(fechaHora));
        ps.setString(3, txtMotivo.getText().trim());
        ps.setBigDecimal(4, new BigDecimal(txtTotalPerdida.getText().trim()));
        ps.setBoolean(5, chkEstado.isSelected());
    }

    @FXML private void editarPerdida(ActionEvent event) {
        if (idPerdidaSeleccionada == 0) mostrarAlerta("Advertencia", "Seleccione una pérdida", Alert.AlertType.WARNING);
        else guardarPerdida(event);
    }

    @FXML private void eliminarPerdida(ActionEvent event) {
        if (!SesionUsuario.getInstancia().getCargoUsuario().equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores", Alert.AlertType.ERROR); return;
        }
        if (idPerdidaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una pérdida", Alert.AlertType.WARNING); return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setContentText("¿Eliminar esta pérdida?");
        if (conf.showAndWait().get() != ButtonType.OK) return;
        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_PERDIDA WHERE id_perdida=?")) {
            ps.setInt(1, idPerdidaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Pérdida eliminada", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarPerdidas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar", Alert.AlertType.ERROR);
        }
    }

    @FXML private void buscarPerdida(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarPerdidas(); return; }
        listaPerdidas.clear();
        String sql = "SELECT * FROM tbl_PERDIDA WHERE motivo LIKE ? ORDER BY id_perdida DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaPerdidas.add(new Perdida(rs.getInt("id_perdida"), rs.getInt("id_empleado"),
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getString("motivo"), rs.getBigDecimal("total_perdida"), rs.getBoolean("estado")));
            }
            tblPerdidas.setItems(listaPerdidas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void mostrarTodos(ActionEvent event) { txtBuscar.clear(); cargarPerdidas(); }
    @FXML private void limpiarCampos(ActionEvent event) { limpiarCamposInterno(); }

    private void limpiarCamposInterno() {
        idPerdidaSeleccionada = 0; cmbEmpleado.setValue(null);
        dateFecha.setValue(LocalDate.now()); txtMotivo.clear();
        txtTotalPerdida.clear(); chkEstado.setSelected(true);
        modoEdicion = false; habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false); tblPerdidas.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un empleado", Alert.AlertType.WARNING);
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha", Alert.AlertType.WARNING);
            return false;
        }
        if (txtMotivo.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El motivo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtTotalPerdida.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El total de pérdida es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        try {
            BigDecimal total = new BigDecimal(txtTotalPerdida.getText().trim());
            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("Validación", "El total debe ser mayor a 0", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "Total inválido", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje);
        a.showAndWait();
    }
}