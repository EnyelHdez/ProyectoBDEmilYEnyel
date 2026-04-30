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
import org.example.proyecto.Modelos.Proveedor;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroProveedorController implements Initializable {

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

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtNombreComercial;
    @FXML private TextField txtRNC;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtContacto;
    @FXML private TextField txtDireccion;
    @FXML private CheckBox chkEstado;

    // Tabla
    @FXML private TableView<Proveedor> tblProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colRazonSocial;
    @FXML private TableColumn<Proveedor, String> colNombreComercial;
    @FXML private TableColumn<Proveedor, String> colRnc;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, String> colEstado;

    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Connection conexion;
    private int idProveedorSeleccionado = 0;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
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
            case "Almacenista":
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

        if (!"Administrador".equals(rol) && !"Almacenista".equals(rol)) {
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
        cargarProveedores();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colNombreComercial.setCellValueFactory(new PropertyValueFactory<>("nombreComercial"));
        colRnc.setCellValueFactory(new PropertyValueFactory<>("rnc"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoTexto"));
        tblProveedores.setItems(listaProveedores);
    }

    private void configurarSeleccionTabla() {
        tblProveedores.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        cargarDatosEnFormulario(newVal);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                });
    }

    private void cargarDatosEnFormulario(Proveedor p) {
        idProveedorSeleccionado = p.getIdProveedor();
        txtNombre.setText(p.getRazonSocial() != null ? p.getRazonSocial() : "");
        txtNombreComercial.setText(p.getNombreComercial() != null ? p.getNombreComercial() : "");
        txtRNC.setText(p.getRnc() != null ? p.getRnc() : "");
        txtTelefono.setText(p.getTelefono() != null ? p.getTelefono() : "");
        txtEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        txtContacto.setText(p.getContacto() != null ? p.getContacto() : "");
        txtDireccion.setText(p.getDireccion() != null ? p.getDireccion() : "");
        chkEstado.setSelected("Activo".equals(p.getEstadoTexto()));
    }

    @FXML
    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR ORDER BY id_proveedor DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaProveedores.add(mapearProveedor(rs));
            }
            tblProveedores.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("id_proveedor"));
        p.setRazonSocial(rs.getString("razon_social"));
        p.setNombreComercial(rs.getString("nombre_comercial"));
        p.setRnc(rs.getString("rnc"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setContacto(rs.getString("contacto"));
        p.setDireccion(rs.getString("direccion"));
        p.setEstadoTexto(rs.getString("estado_temp"));
        return p;
    }

    @FXML
    private void guardarProveedor(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Almacenista")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar proveedores", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_PROVEEDOR (rnc, razon_social, nombre_comercial, " +
                "telefono, email, contacto, direccion, estado_temp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Proveedor guardado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarProveedores();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarProveedor(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar proveedores", Alert.AlertType.ERROR);
            return;
        }

        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setHeaderText(null);
        conf.setContentText("¿Desea guardar los cambios en este proveedor?");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        String sql = "UPDATE tbl_PROVEEDOR SET rnc=?, razon_social=?, nombre_comercial=?, " +
                "telefono=?, email=?, contacto=?, direccion=?, estado_temp=? " +
                "WHERE id_proveedor=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(9, idProveedorSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Proveedor actualizado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarProveedores();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarProveedor(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar proveedores", Alert.AlertType.ERROR);
            return;
        }

        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea eliminar este proveedor?\nEsta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_PROVEEDOR WHERE id_proveedor=?")) {
            ps.setInt(1, idProveedorSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Proveedor eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarProveedores();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: el proveedor tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarProveedor(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarProveedores(); return; }

        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR " +
                "WHERE razon_social LIKE ? OR nombre_comercial LIKE ? OR rnc LIKE ? OR telefono LIKE ? " +
                "ORDER BY id_proveedor DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) listaProveedores.add(mapearProveedor(rs));
            tblProveedores.setItems(listaProveedores);

            if (listaProveedores.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados para: " + filtro, Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarProveedores();
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        ps.setString(1, txtRNC.getText().trim());
        ps.setString(2, txtNombre.getText().trim());
        ps.setString(3, txtNombreComercial.getText().trim());
        ps.setString(4, txtTelefono.getText().trim());
        ps.setString(5, txtEmail.getText().trim());
        ps.setString(6, txtContacto.getText().trim());
        ps.setString(7, txtDireccion.getText().trim());
        ps.setString(8, chkEstado.isSelected() ? "Activo" : "Inactivo");
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idProveedorSeleccionado = 0;
        txtNombre.clear();
        txtNombreComercial.clear();
        txtRNC.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtContacto.clear();
        txtDireccion.clear();
        chkEstado.setSelected(true);
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblProveedores.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isBlank()) {
            mostrarAlerta("Validación", "La razón social es obligatoria", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }
        if (txtRNC.getText().isBlank()) {
            mostrarAlerta("Validación", "El RNC es obligatorio", Alert.AlertType.WARNING);
            txtRNC.requestFocus();
            return false;
        }
        if (txtTelefono.getText().isBlank()) {
            mostrarAlerta("Validación", "El teléfono es obligatorio", Alert.AlertType.WARNING);
            txtTelefono.requestFocus();
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