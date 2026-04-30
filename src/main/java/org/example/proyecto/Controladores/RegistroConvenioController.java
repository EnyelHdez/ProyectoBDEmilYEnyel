package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Convenio;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroConvenioController implements Initializable {

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
    @FXML private TableView<Convenio> tblConvenios;
    @FXML private TableColumn<Convenio, Integer> colId;
    @FXML private TableColumn<Convenio, String> colNombre;
    @FXML private TableColumn<Convenio, String> colArs;
    @FXML private TableColumn<Convenio, String> colDescripcion;
    @FXML private TableColumn<Convenio, BigDecimal> colPorcentajeCob;
    @FXML private TableColumn<Convenio, LocalDate> colFechaInicio;
    @FXML private TableColumn<Convenio, LocalDate> colFechaFin;
    @FXML private TableColumn<Convenio, String> colEstado;

    // Formulario
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbArs;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPorcentajeCob;
    @FXML private DatePicker dateFechaInicio;
    @FXML private DatePicker dateFechaFin;
    @FXML private CheckBox chkEstado;

    private ObservableList<Convenio> conveniosList = FXCollections.observableArrayList();
    private Convenio convenioSeleccionado = null;
    private Connection conexion;
    private java.util.Map<Integer, String> mapaArs = new java.util.HashMap<>();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        cargarArs();
        configurarSeleccionTabla();
        configurarBotonesPorRol();

        dateFechaInicio.setValue(LocalDate.now());
        chkEstado.setSelected(true);

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
        cargarConvenios();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idConvenio"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colArs.setCellValueFactory(new PropertyValueFactory<>("nombreArs"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPorcentajeCob.setCellValueFactory(new PropertyValueFactory<>("porcentajeCob"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));

        colPorcentajeCob.setCellFactory(col -> new TableCell<Convenio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f%%", item));
            }
        });
    }

    private void cargarArs() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_ars, nombre FROM tbl_ARS WHERE estado = 1 ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_ars");
                String nombre = rs.getString("nombre");
                mapaArs.put(id, nombre);
                lista.add(id + " - " + nombre);
            }
            cmbArs.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar ARS: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarConvenios() {
        conveniosList.clear();
        String sql = "SELECT c.*, a.nombre as nombre_ars " +
                "FROM tbl_CONVENIO c " +
                "LEFT JOIN tbl_ARS a ON c.id_ars = a.id_ars " +
                "ORDER BY c.id_convenio DESC";

        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Convenio c = new Convenio();
                c.setIdConvenio(rs.getInt("id_convenio"));
                c.setIdArs(rs.getInt("id_ars"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPorcentajeCob(rs.getBigDecimal("porcentaje_cob"));
                c.setFechaInicio(rs.getDate("fecha_inicio") != null ?
                        rs.getDate("fecha_inicio").toLocalDate() : null);
                c.setFechaFin(rs.getDate("fecha_fin") != null ?
                        rs.getDate("fecha_fin").toLocalDate() : null);
                c.setEstado(rs.getBoolean("estado"));
                c.setNombreArs(rs.getString("nombre_ars"));
                conveniosList.add(c);
            }
            tblConvenios.setItems(conveniosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar convenios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblConvenios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                convenioSeleccionado = sel;
                cargarConvenioEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarConvenioEnFormulario(Convenio c) {
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion());
        txtPorcentajeCob.setText(c.getPorcentajeCob() != null ? c.getPorcentajeCob().toString() : "");
        dateFechaInicio.setValue(c.getFechaInicio());
        dateFechaFin.setValue(c.getFechaFin());
        chkEstado.setSelected(c.isEstado());

        cmbArs.getItems().stream()
                .filter(item -> item.startsWith(c.getIdArs() + " - "))
                .findFirst()
                .ifPresent(cmbArs::setValue);
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        return Integer.parseInt(combo.getValue().split(" - ")[0]);
    }

    @FXML
    private void guardarConvenio() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar convenios", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        if (modoEdicion && convenioSeleccionado != null) {
            // Actualizar convenio existente
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Confirmar edición");
            conf.setHeaderText(null);
            conf.setContentText("¿Guardar cambios en este convenio?");
            if (conf.showAndWait().get() != ButtonType.OK) return;

            String sql = "UPDATE tbl_CONVENIO SET id_ars=?, nombre=?, descripcion=?, " +
                    "porcentaje_cob=?, fecha_inicio=?, fecha_fin=?, estado=? WHERE id_convenio=?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                setearParametros(ps);
                ps.setInt(8, convenioSeleccionado.getIdConvenio());
                ps.executeUpdate();
                mostrarAlerta("Éxito", "Convenio actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarConvenios();
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Crear nuevo convenio
            String sql = "INSERT INTO tbl_CONVENIO (id_ars, nombre, descripcion, porcentaje_cob, " +
                    "fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setearParametros(ps);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    mostrarAlerta("Éxito", "Convenio guardado correctamente (ID: " + rs.getInt(1) + ")",
                            Alert.AlertType.INFORMATION);
                }
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarConvenios();
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        modoEdicion = false;
        btnGuardar.setDisable(false);
    }

    @FXML
    private void editarConvenio() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar convenios", Alert.AlertType.ERROR);
            return;
        }

        if (convenioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un convenio para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarConvenio();
    }

    @FXML
    private void eliminarConvenio() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar convenios", Alert.AlertType.ERROR);
            return;
        }

        if (convenioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un convenio para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Eliminar este convenio? Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_CONVENIO WHERE id_convenio=?")) {
            ps.setInt(1, convenioSeleccionado.getIdConvenio());
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Convenio eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarConvenios();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: el convenio tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarCampos() {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        txtNombre.clear();
        cmbArs.setValue(null);
        txtDescripcion.clear();
        txtPorcentajeCob.clear();
        dateFechaInicio.setValue(LocalDate.now());
        dateFechaFin.setValue(null);
        chkEstado.setSelected(true);
        convenioSeleccionado = null;
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblConvenios.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarConvenio() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarConvenios(); return; }

        conveniosList.clear();
        String sql = "SELECT c.*, a.nombre as nombre_ars " +
                "FROM tbl_CONVENIO c " +
                "LEFT JOIN tbl_ARS a ON c.id_ars = a.id_ars " +
                "WHERE c.nombre LIKE ? OR a.nombre LIKE ? " +
                "ORDER BY c.id_convenio DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Convenio c = new Convenio();
                c.setIdConvenio(rs.getInt("id_convenio"));
                c.setIdArs(rs.getInt("id_ars"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setPorcentajeCob(rs.getBigDecimal("porcentaje_cob"));
                c.setFechaInicio(rs.getDate("fecha_inicio") != null ?
                        rs.getDate("fecha_inicio").toLocalDate() : null);
                c.setFechaFin(rs.getDate("fecha_fin") != null ?
                        rs.getDate("fecha_fin").toLocalDate() : null);
                c.setEstado(rs.getBoolean("estado"));
                c.setNombreArs(rs.getString("nombre_ars"));
                conveniosList.add(c);
            }
            tblConvenios.setItems(conveniosList);
            if (conveniosList.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarConvenios();
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setInt(1, getIdSeleccionado(cmbArs));
        ps.setString(2, txtNombre.getText().trim());
        ps.setString(3, txtDescripcion.getText().trim());

        String porcentaje = txtPorcentajeCob.getText().trim();
        ps.setBigDecimal(4, porcentaje.isEmpty() ? null : new BigDecimal(porcentaje));

        ps.setDate(5, dateFechaInicio.getValue() != null ?
                Date.valueOf(dateFechaInicio.getValue()) : null);
        ps.setDate(6, dateFechaFin.getValue() != null ?
                Date.valueOf(dateFechaFin.getValue()) : null);
        ps.setBoolean(7, chkEstado.isSelected());
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }
        if (cmbArs.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione una ARS", Alert.AlertType.WARNING);
            cmbArs.requestFocus();
            return false;
        }
        if (dateFechaInicio.getValue() == null) {
            mostrarAlerta("Validación", "La fecha de inicio es obligatoria", Alert.AlertType.WARNING);
            dateFechaInicio.requestFocus();
            return false;
        }
        if (dateFechaFin.getValue() != null &&
                dateFechaFin.getValue().isBefore(dateFechaInicio.getValue())) {
            mostrarAlerta("Validación", "La fecha fin no puede ser anterior a la fecha inicio",
                    Alert.AlertType.WARNING);
            return false;
        }
        String porcentaje = txtPorcentajeCob.getText().trim();
        if (!porcentaje.isEmpty()) {
            try {
                BigDecimal p = new BigDecimal(porcentaje);
                if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(new BigDecimal("100")) > 0) {
                    mostrarAlerta("Validación", "El porcentaje debe estar entre 0 y 100",
                            Alert.AlertType.WARNING);
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Validación", "Porcentaje inválido (ej: 25.50)", Alert.AlertType.WARNING);
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
}