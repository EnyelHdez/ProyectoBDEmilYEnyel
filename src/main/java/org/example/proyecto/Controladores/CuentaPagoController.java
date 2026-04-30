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
import org.example.proyecto.Modelos.CuentaPago;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class CuentaPagoController implements Initializable {

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
    @FXML private TableView<CuentaPago> tblCuentas;
    @FXML private TableColumn<CuentaPago, Integer> colId;
    @FXML private TableColumn<CuentaPago, String> colNombre;
    @FXML private TableColumn<CuentaPago, String> colDescripcion;
    @FXML private TableColumn<CuentaPago, Boolean> colEstado;

    // Formulario
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private CheckBox chkEstado;

    private Connection conexion;
    private int idCuentaSeleccionada = 0;
    private final ObservableList<CuentaPago> listaCuentas = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarTabla();
        cargarCuentas();
        configurarSeleccionTabla();
        configurarBotonesPorRol();

        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCuentaPago"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<CuentaPago, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(item ? "Activo" : "Inactivo");
                    setStyle(item ? "-fx-text-fill: #2E7D32; -fx-font-weight: bold;" : "-fx-text-fill: #C62828; -fx-font-weight: bold;");
                }
            }
        });
        tblCuentas.setItems(listaCuentas);
    }

    private void cargarCuentas() {
        listaCuentas.clear();
        String sql = "SELECT * FROM tbl_CUENTA_PAGO ORDER BY id_cuenta_pago DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                CuentaPago c = new CuentaPago(
                        rs.getInt("id_cuenta_pago"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("estado")
                );
                listaCuentas.add(c);
            }
            tblCuentas.setItems(listaCuentas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar cuentas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblCuentas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idCuentaSeleccionada = sel.getIdCuentaPago();
                cargarCuentaEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarCuentaEnFormulario(CuentaPago c) {
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion());
        chkEstado.setSelected(c.isEstado());
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
        cargarCuentas();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    @FXML
    private void guardarCuenta(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar cuentas", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        if (modoEdicion && idCuentaSeleccionada != 0) {
            actualizarCuenta();
        } else {
            insertarCuenta();
        }
    }

    private void insertarCuenta() {
        String sql = "INSERT INTO tbl_CUENTA_PAGO (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setearParametros(ps);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                mostrarAlerta("Éxito", "Cuenta guardada correctamente (ID: " + rs.getInt(1) + ")", Alert.AlertType.INFORMATION);
            }
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarCuentas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarCuenta() {
        String sql = "UPDATE tbl_CUENTA_PAGO SET nombre=?, descripcion=?, estado=? WHERE id_cuenta_pago=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(4, idCuentaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Cuenta actualizada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarCuentas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setString(1, txtNombre.getText().trim());
        ps.setString(2, txtDescripcion.getText().trim());
        ps.setBoolean(3, chkEstado.isSelected());
    }

    @FXML
    private void editarCuenta(ActionEvent event) {
        if (idCuentaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una cuenta para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarCuenta(event);
    }

    @FXML
    private void eliminarCuenta(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar cuentas", Alert.AlertType.ERROR);
            return;
        }

        if (idCuentaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una cuenta para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro de eliminar esta cuenta?");
        conf.setContentText("Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_CUENTA_PAGO WHERE id_cuenta_pago=?")) {
            ps.setInt(1, idCuentaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Cuenta eliminada correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) cargarCuentas();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la cuenta tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarCuenta(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarCuentas(); return; }

        listaCuentas.clear();
        String sql = "SELECT * FROM tbl_CUENTA_PAGO WHERE nombre LIKE ? ORDER BY id_cuenta_pago DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CuentaPago c = new CuentaPago(
                        rs.getInt("id_cuenta_pago"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("estado")
                );
                listaCuentas.add(c);
            }
            tblCuentas.setItems(listaCuentas);
            if (listaCuentas.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarCuentas();
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idCuentaSeleccionada = 0;
        txtNombre.clear();
        txtDescripcion.clear();
        chkEstado.setSelected(true);
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblCuentas.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
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
