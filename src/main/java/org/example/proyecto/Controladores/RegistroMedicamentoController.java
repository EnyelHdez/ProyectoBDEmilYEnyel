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
import org.example.proyecto.Modelos.Medicamento;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroMedicamentoController implements Initializable {

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
    @FXML private TableView<Medicamento> tblMedicamentos;
    @FXML private TableColumn<Medicamento, Integer> colId;
    @FXML private TableColumn<Medicamento, String> colProducto;
    @FXML private TableColumn<Medicamento, String> colPrincipioActivo;
    @FXML private TableColumn<Medicamento, String> colConcentracion;
    @FXML private TableColumn<Medicamento, String> colViaAdministracion;
    @FXML private TableColumn<Medicamento, String> colRegistroSanitario;
    @FXML private TableColumn<Medicamento, String> colLaboratorio;
    @FXML private TableColumn<Medicamento, Boolean> colEstado;

    // Formulario
    @FXML private ComboBox<String> cmbProducto;
    @FXML private TextField txtPrincipioActivo;
    @FXML private TextField txtConcentracion;
    @FXML private ComboBox<String> cmbViaAdministracion;
    @FXML private TextField txtRegistroSanitario;
    @FXML private TextField txtLaboratorio;
    @FXML private CheckBox chkEstado;

    private Connection conexion;
    private int idMedicamentoSeleccionado = 0;
    private final ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarCombos();
        configurarTabla();
        cargarProductos();
        cargarMedicamentos();
        configurarSeleccionTabla();
        configurarBotonesPorRol();

        // Inicialmente el panel de consulta está oculto
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);

        // Botones de edición deshabilitados al inicio
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    private void configurarCombos() {
        cmbViaAdministracion.setItems(FXCollections.observableArrayList(
                "Oral", "Tópica", "Intravenosa", "Intramuscular", "Subcutánea", "Inhalatoria", "Oftálmica", "Ótica"
        ));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMedicamento"));
        colProducto.setCellValueFactory(cellData -> {
            String nombreProducto = obtenerNombreProducto(cellData.getValue().getIdProducto());
            return new javafx.beans.property.SimpleStringProperty(nombreProducto);
        });
        colPrincipioActivo.setCellValueFactory(new PropertyValueFactory<>("principioActivo"));
        colConcentracion.setCellValueFactory(new PropertyValueFactory<>("concentracion"));
        colViaAdministracion.setCellValueFactory(new PropertyValueFactory<>("viaAdministracion"));
        colRegistroSanitario.setCellValueFactory(new PropertyValueFactory<>("registroSanitario"));
        colLaboratorio.setCellValueFactory(new PropertyValueFactory<>("laboratorio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<Medicamento, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Activo" : "Inactivo");
                    setStyle(item ? "-fx-text-fill: #2E7D32; -fx-font-weight: bold;" : "-fx-text-fill: #C62828; -fx-font-weight: bold;");
                }
            }
        });

        tblMedicamentos.setItems(listaMedicamentos);
    }

    private void cargarProductos() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_producto, nombre FROM tbl_PRODUCTO WHERE estado = 1 ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_producto") + " - " + rs.getString("nombre"));
            }
            cmbProducto.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String obtenerNombreProducto(int idProducto) {
        String sql = "SELECT nombre FROM tbl_PRODUCTO WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de producto: " + e.getMessage());
        }
        return "Desconocido";
    }

    private void cargarMedicamentos() {
        listaMedicamentos.clear();
        String sql = "SELECT * FROM tbl_MEDICAMENTO ORDER BY id_medicamento DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Medicamento m = new Medicamento(
                        rs.getInt("id_medicamento"),
                        rs.getInt("id_producto"),
                        rs.getString("principio_activo"),
                        rs.getString("concentracion"),
                        rs.getString("via_administracion"),
                        rs.getString("registro_sanitario"),
                        rs.getString("laboratorio"),
                        rs.getBoolean("estado")
                );
                listaMedicamentos.add(m);
            }
            tblMedicamentos.setItems(listaMedicamentos);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar medicamentos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblMedicamentos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idMedicamentoSeleccionado = sel.getIdMedicamento();
                cargarMedicamentoEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarMedicamentoEnFormulario(Medicamento m) {
        // Producto
        cmbProducto.getItems().stream()
                .filter(item -> item.startsWith(m.getIdProducto() + " - "))
                .findFirst()
                .ifPresent(cmbProducto::setValue);

        txtPrincipioActivo.setText(m.getPrincipioActivo());
        txtConcentracion.setText(m.getConcentracion());
        cmbViaAdministracion.setValue(m.getViaAdministracion());
        txtRegistroSanitario.setText(m.getRegistroSanitario());
        txtLaboratorio.setText(m.getLaboratorio());
        chkEstado.setSelected(m.isEstado());
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
        cargarMedicamentos();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    @FXML
    private void guardarMedicamento(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Farmacéutico")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar medicamentos", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        if (modoEdicion && idMedicamentoSeleccionado != 0) {
            actualizarMedicamento();
        } else {
            insertarMedicamento();
        }
    }

    private void insertarMedicamento() {
        String sql = "INSERT INTO tbl_MEDICAMENTO (id_producto, principio_activo, concentracion, via_administracion, registro_sanitario, laboratorio, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setearParametros(ps);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                mostrarAlerta("Éxito", "Medicamento guardado correctamente (ID: " + rs.getInt(1) + ")", Alert.AlertType.INFORMATION);
            }
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarMedicamentos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarMedicamento() {
        String sql = "UPDATE tbl_MEDICAMENTO SET id_producto=?, principio_activo=?, concentracion=?, via_administracion=?, registro_sanitario=?, laboratorio=?, estado=? WHERE id_medicamento=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(8, idMedicamentoSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Medicamento actualizado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarMedicamentos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setInt(1, obtenerIdFromCombo(cmbProducto.getValue()));
        ps.setString(2, txtPrincipioActivo.getText().trim());
        ps.setString(3, txtConcentracion.getText().trim());
        ps.setString(4, cmbViaAdministracion.getValue());
        ps.setString(5, txtRegistroSanitario.getText().trim());
        ps.setString(6, txtLaboratorio.getText().trim());
        ps.setBoolean(7, chkEstado.isSelected());
    }

    @FXML
    private void editarMedicamento(ActionEvent event) {
        if (idMedicamentoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un medicamento para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarMedicamento(event);
    }

    @FXML
    private void eliminarMedicamento(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar medicamentos", Alert.AlertType.ERROR);
            return;
        }

        if (idMedicamentoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un medicamento para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro de eliminar este medicamento?");
        conf.setContentText("Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_MEDICAMENTO WHERE id_medicamento=?")) {
            ps.setInt(1, idMedicamentoSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Medicamento eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarMedicamentos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: el medicamento tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarMedicamento(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarMedicamentos(); return; }

        listaMedicamentos.clear();
        String sql = "SELECT m.* FROM tbl_MEDICAMENTO m JOIN tbl_PRODUCTO p ON m.id_producto = p.id_producto WHERE p.nombre LIKE ? OR m.principio_activo LIKE ? OR m.laboratorio LIKE ? ORDER BY m.id_medicamento DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Medicamento m = new Medicamento(
                        rs.getInt("id_medicamento"), rs.getInt("id_producto"),
                        rs.getString("principio_activo"), rs.getString("concentracion"),
                        rs.getString("via_administracion"), rs.getString("registro_sanitario"),
                        rs.getString("laboratorio"), rs.getBoolean("estado")
                );
                listaMedicamentos.add(m);
            }
            tblMedicamentos.setItems(listaMedicamentos);
            if (listaMedicamentos.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarMedicamentos();
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idMedicamentoSeleccionado = 0;
        cmbProducto.setValue(null);
        txtPrincipioActivo.clear();
        txtConcentracion.clear();
        cmbViaAdministracion.setValue(null);
        txtRegistroSanitario.clear();
        txtLaboratorio.clear();
        chkEstado.setSelected(true);
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblMedicamentos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbProducto.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un producto", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPrincipioActivo.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El principio activo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private int obtenerIdFromCombo(String val) {
        if (val == null) return 0;
        return Integer.parseInt(val.split(" - ")[0]);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}