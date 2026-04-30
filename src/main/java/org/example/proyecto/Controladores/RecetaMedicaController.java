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
import org.example.proyecto.Modelos.RecetaMedica;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecetaMedicaController implements Initializable {

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
    @FXML private TableView<RecetaMedica> tblRecetas;
    @FXML private TableColumn<RecetaMedica, Integer> colId;
    @FXML private TableColumn<RecetaMedica, String> colNroReceta;
    @FXML private TableColumn<RecetaMedica, String> colCliente;
    @FXML private TableColumn<RecetaMedica, String> colMedico;
    @FXML private TableColumn<RecetaMedica, String> colFechaEmision;
    @FXML private TableColumn<RecetaMedica, String> colFechaVencimiento;
    @FXML private TableColumn<RecetaMedica, String> colObservacion;
    @FXML private TableColumn<RecetaMedica, String> colEstado;

    // Formulario
    @FXML private ComboBox<String> cmbCliente;
    @FXML private TextField txtNroReceta;
    @FXML private ComboBox<String> cmbVenta;
    @FXML private ComboBox<String> cmbMedico;
    @FXML private TextField txtNombreMedicoExt;
    @FXML private TextField txtEspecialidadExt;
    @FXML private DatePicker dateFechaEmision;
    @FXML private DatePicker dateFechaVencimiento;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtObservacion;

    private Connection conexion;
    private int idRecetaSeleccionada = 0;
    private final ObservableList<RecetaMedica> listaRecetas = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarCombos();
        configurarTabla();
        cargarClientes();
        cargarMedicos();
        cargarVentas();
        cargarRecetas();
        configurarSeleccionTabla();
        configurarBotonesPorRol();

        dateFechaEmision.setValue(LocalDate.now());

        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    private void configurarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVA", "VENCIDA", "USADA", "ANULADA"));
        cmbEstado.setValue("ACTIVA");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReceta"));
        colNroReceta.setCellValueFactory(new PropertyValueFactory<>("nroReceta"));

        colCliente.setCellValueFactory(cellData -> {
            String nombre = obtenerNombreCliente(cellData.getValue().getIdCliente());
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        colMedico.setCellValueFactory(cellData -> {
            if (cellData.getValue().getIdMedico() != null && cellData.getValue().getIdMedico() > 0) {
                String medico = obtenerNombreMedico(cellData.getValue().getIdMedico());
                return new javafx.beans.property.SimpleStringProperty(medico);
            } else {
                String medicoExt = cellData.getValue().getNombreMedicoExt();
                return new javafx.beans.property.SimpleStringProperty(medicoExt != null ? medicoExt : "");
            }
        });

        colFechaEmision.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaEmision() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaEmision().toString());
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colFechaVencimiento.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaVencimiento() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaVencimiento().toString());
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));

        // CORREGIDO: Usar SimpleStringProperty para el estado
        colEstado.setCellValueFactory(cellData -> {
            boolean estado = cellData.getValue().isEstado();
            return new javafx.beans.property.SimpleStringProperty(estado ? "ACTIVA" : "INACTIVA");
        });

        // CORREGIDO: Aplicar estilo a las celdas de estado
        colEstado.setCellFactory(column -> new TableCell<RecetaMedica, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("ACTIVA")) {
                        setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tblRecetas.setItems(listaRecetas);
    }

    private void cargarClientes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_cliente, nombres, apellidos FROM tbl_CLIENTE WHERE estado = 1 ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_cliente") + " - " + rs.getString("nombres") + " " + rs.getString("apellidos"));
            }
            cmbCliente.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarMedicos() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_medico, nombres FROM tbl_MEDICO WHERE estado = 1 ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_medico") + " - " + rs.getString("nombres"));
            }
            cmbMedico.setItems(lista);
        } catch (SQLException e) {
            System.out.println("Error al cargar médicos: " + e.getMessage());
        }
    }

    private void cargarVentas() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_venta FROM tbl_VENTA ORDER BY id_venta DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(String.valueOf(rs.getInt("id_venta")));
            }
            cmbVenta.setItems(lista);
        } catch (SQLException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
        }
    }

    private void cargarRecetas() {
        listaRecetas.clear();
        String sql = "SELECT * FROM tbl_RECETA_MEDICA ORDER BY id_receta DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                boolean estado = rs.getInt("estado") == 1;
                RecetaMedica r = new RecetaMedica(
                        rs.getInt("id_receta"),
                        rs.getInt("id_cliente"),
                        rs.getObject("id_medico") != null ? rs.getInt("id_medico") : null,
                        rs.getObject("id_venta") != null ? rs.getInt("id_venta") : null,
                        rs.getString("nro_receta"),
                        rs.getDate("fecha_emision") != null ? rs.getDate("fecha_emision").toLocalDate() : null,
                        rs.getDate("fecha_vencimiento") != null ? rs.getDate("fecha_vencimiento").toLocalDate() : null,
                        rs.getString("nombre_medico_ext"),
                        rs.getString("especialidad_ext"),
                        rs.getString("observacion"),
                        rs.getString("imagen_receta"),
                        estado
                );
                listaRecetas.add(r);
            }
            tblRecetas.setItems(listaRecetas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar recetas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String obtenerNombreCliente(int idCliente) {
        String sql = "SELECT nombres, apellidos FROM tbl_CLIENTE WHERE id_cliente = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombres") + " " + rs.getString("apellidos");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }
        return "Desconocido";
    }

    private String obtenerNombreMedico(int idMedico) {
        String sql = "SELECT nombres FROM tbl_MEDICO WHERE id_medico = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombres");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener médico: " + e.getMessage());
        }
        return "Desconocido";
    }

    private void configurarSeleccionTabla() {
        tblRecetas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idRecetaSeleccionada = sel.getIdReceta();
                cargarRecetaEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarRecetaEnFormulario(RecetaMedica r) {
        cmbCliente.getItems().stream()
                .filter(item -> item.startsWith(r.getIdCliente() + " - "))
                .findFirst().ifPresent(cmbCliente::setValue);

        txtNroReceta.setText(r.getNroReceta());

        if (r.getIdVenta() != null) {
            cmbVenta.setValue(String.valueOf(r.getIdVenta()));
        }

        if (r.getIdMedico() != null) {
            cmbMedico.getItems().stream()
                    .filter(item -> item.startsWith(r.getIdMedico() + " - "))
                    .findFirst().ifPresent(cmbMedico::setValue);
        }

        txtNombreMedicoExt.setText(r.getNombreMedicoExt());
        txtEspecialidadExt.setText(r.getEspecialidadExt());

        if (r.getFechaEmision() != null) dateFechaEmision.setValue(r.getFechaEmision());
        if (r.getFechaVencimiento() != null) dateFechaVencimiento.setValue(r.getFechaVencimiento());

        txtObservacion.setText(r.getObservacion());
        cmbEstado.setValue(r.isEstado() ? "ACTIVA" : "ANULADA");
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
            case "Farmacéutico":
                puedeEditar = true;
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
        if (!"Administrador".equals(rol) && !"Farmacéutico".equals(rol)) {
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
        cargarRecetas();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    @FXML
    private void guardarReceta(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Farmacéutico")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar recetas", Alert.AlertType.ERROR);
            return;
        }
        if (!validarCampos()) return;
        if (modoEdicion && idRecetaSeleccionada != 0) actualizarReceta();
        else insertarReceta();
    }

    private void insertarReceta() {
        String sql = "INSERT INTO tbl_RECETA_MEDICA (id_cliente, id_medico, id_venta, nro_receta, fecha_emision, fecha_vencimiento, nombre_medico_ext, especialidad_ext, observacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setearParametros(ps);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) mostrarAlerta("Éxito", "Receta guardada correctamente (ID: " + rs.getInt(1) + ")", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarRecetas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarReceta() {
        String sql = "UPDATE tbl_RECETA_MEDICA SET id_cliente=?, id_medico=?, id_venta=?, nro_receta=?, fecha_emision=?, fecha_vencimiento=?, nombre_medico_ext=?, especialidad_ext=?, observacion=?, estado=? WHERE id_receta=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(11, idRecetaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Receta actualizada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarRecetas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setInt(1, Integer.parseInt(cmbCliente.getValue().split(" - ")[0]));
        if (cmbMedico.getValue() != null) ps.setInt(2, Integer.parseInt(cmbMedico.getValue().split(" - ")[0]));
        else ps.setNull(2, Types.INTEGER);
        if (cmbVenta.getValue() != null) ps.setInt(3, Integer.parseInt(cmbVenta.getValue()));
        else ps.setNull(3, Types.INTEGER);
        ps.setString(4, txtNroReceta.getText().trim());
        ps.setDate(5, Date.valueOf(dateFechaEmision.getValue()));
        ps.setDate(6, dateFechaVencimiento.getValue() != null ? Date.valueOf(dateFechaVencimiento.getValue()) : null);
        ps.setString(7, txtNombreMedicoExt.getText().trim());
        ps.setString(8, txtEspecialidadExt.getText().trim());
        ps.setString(9, txtObservacion.getText().trim());
        ps.setBoolean(10, "ACTIVA".equals(cmbEstado.getValue()));
    }

    @FXML
    private void editarReceta(ActionEvent event) {
        if (idRecetaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una receta para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarReceta(event);
    }

    @FXML
    private void eliminarReceta(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar recetas", Alert.AlertType.ERROR);
            return;
        }
        if (idRecetaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una receta", Alert.AlertType.WARNING);
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Eliminar esta receta?");
        conf.setContentText("Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;
        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_RECETA_MEDICA WHERE id_receta=?")) {
            ps.setInt(1, idRecetaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Receta eliminada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarRecetas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarReceta(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarRecetas(); return; }
        listaRecetas.clear();
        String sql = "SELECT * FROM tbl_RECETA_MEDICA WHERE nro_receta LIKE ? ORDER BY id_receta DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boolean estado = rs.getInt("estado") == 1;
                RecetaMedica r = new RecetaMedica(
                        rs.getInt("id_receta"), rs.getInt("id_cliente"),
                        rs.getObject("id_medico") != null ? rs.getInt("id_medico") : null,
                        rs.getObject("id_venta") != null ? rs.getInt("id_venta") : null,
                        rs.getString("nro_receta"),
                        rs.getDate("fecha_emision") != null ? rs.getDate("fecha_emision").toLocalDate() : null,
                        rs.getDate("fecha_vencimiento") != null ? rs.getDate("fecha_vencimiento").toLocalDate() : null,
                        rs.getString("nombre_medico_ext"), rs.getString("especialidad_ext"),
                        rs.getString("observacion"), rs.getString("imagen_receta"), estado
                );
                listaRecetas.add(r);
            }
            tblRecetas.setItems(listaRecetas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarRecetas();
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idRecetaSeleccionada = 0;
        cmbCliente.setValue(null);
        txtNroReceta.clear();
        cmbVenta.setValue(null);
        cmbMedico.setValue(null);
        txtNombreMedicoExt.clear();
        txtEspecialidadExt.clear();
        dateFechaEmision.setValue(LocalDate.now());
        dateFechaVencimiento.setValue(null);
        txtObservacion.clear();
        cmbEstado.setValue("ACTIVA");
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblRecetas.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un cliente", Alert.AlertType.WARNING);
            return false;
        }
        if (txtNroReceta.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El número de receta es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (dateFechaEmision.getValue() == null) {
            mostrarAlerta("Validación", "La fecha de emisión es obligatoria", Alert.AlertType.WARNING);
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