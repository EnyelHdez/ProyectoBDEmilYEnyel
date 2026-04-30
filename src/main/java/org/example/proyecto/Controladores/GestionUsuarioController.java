package org.example.proyecto.Controladores;

import org.example.proyecto.DAO.UsuarioDAO;
import org.example.proyecto.Modelos.Usuario;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class GestionUsuarioController {

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
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colIdUsuario;
    @FXML private TableColumn<Usuario, String> colNombreUsuario;
    @FXML private TableColumn<Usuario, String> colNombreCompleto;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colCargo;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colFechaRegistro;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, String> colUltimoAcceso;

    // Campos del formulario
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dateFechaRegistro;

    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaUsuarios;
    private FilteredList<Usuario> filteredList;
    private Usuario usuarioSeleccionado;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        listaUsuarios = FXCollections.observableArrayList();

        // Configurar columnas
        configurarColumnas();

        // Configurar ComboBoxes
        configurarComboBoxes();

        // Configurar DatePicker
        configurarDatePickers();

        // Configurar selección de tabla
        configurarSeleccionTabla();

        // Configurar permisos por rol
        configurarBotonesPorRol();

        // Configurar búsqueda
        configurarBusqueda();

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

        // Solo Administrador puede gestionar usuarios
        if (rol.equals("Administrador")) {
            puedeEditar = true;
            puedeEliminar = true;
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
        cargarUsuarios();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarSeleccionTabla() {
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        cargarUsuarioEnFormulario(newSelection);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                }
        );
    }

    private void configurarColumnas() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistroStr"));
        colEstado.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            String estado = usuario.isEstado() ? "Activo" : "Inactivo";
            return new javafx.beans.property.SimpleStringProperty(estado);
        });
        colUltimoAcceso.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            String ultimoAcceso = "";
            if (usuario.getUltimoAcceso() != null) {
                ultimoAcceso = usuario.getUltimoAcceso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            return new javafx.beans.property.SimpleStringProperty(ultimoAcceso);
        });
    }

    private void configurarComboBoxes() {
        cmbCargo.getItems().addAll(
                "Administrador",
                "Farmacéutico",
                "Cajero",
                "Almacenista",
                "Auxiliar"
        );
        cmbEstado.getItems().addAll("Activo", "Inactivo");
        cmbEstado.setValue("Activo");
    }

    private void configurarDatePickers() {
        dateFechaRegistro.setValue(java.time.LocalDate.now());
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        listaUsuarios.addAll(usuarioDAO.obtenerTodosUsuarios());
        tblUsuarios.setItems(listaUsuarios);
    }

    private void configurarBusqueda() {
        filteredList = new FilteredList<>(listaUsuarios, p -> true);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (usuario.getNombreUsuario().toLowerCase().contains(lowerCaseFilter)) return true;
                if (usuario.getNombreCompleto().toLowerCase().contains(lowerCaseFilter)) return true;
                if (usuario.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
                if (usuario.getCargo().toLowerCase().contains(lowerCaseFilter)) return true;
                if (usuario.getTelefono() != null && usuario.getTelefono().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<Usuario> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tblUsuarios.comparatorProperty());
        tblUsuarios.setItems(sortedData);
    }

    private void cargarUsuarioEnFormulario(Usuario usuario) {
        txtNombreUsuario.setText(usuario.getNombreUsuario());
        txtNombreCompleto.setText(usuario.getNombreCompleto());
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        cmbCargo.setValue(usuario.getCargo());
        cmbEstado.setValue(usuario.isEstado() ? "Activo" : "Inactivo");

        if (usuario.getFechaRegistro() != null) {
            dateFechaRegistro.setValue(usuario.getFechaRegistro().toLocalDate());
        }

        txtContrasena.clear();
        txtConfirmarContrasena.clear();
        usuarioSeleccionado = usuario;
    }

    @FXML
    private void buscarUsuario() {
        String busqueda = txtBuscar.getText();
        if (busqueda != null && !busqueda.isEmpty()) {
            filteredList.setPredicate(usuario -> {
                String lowerCaseFilter = busqueda.toLowerCase();
                return usuario.getNombreUsuario().toLowerCase().contains(lowerCaseFilter) ||
                        usuario.getNombreCompleto().toLowerCase().contains(lowerCaseFilter) ||
                        usuario.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        usuario.getCargo().toLowerCase().contains(lowerCaseFilter);
            });
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        filteredList.setPredicate(usuario -> true);
    }

    @FXML
    private void guardarUsuario() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar usuarios", AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        if (modoEdicion && usuarioSeleccionado != null) {
            // Actualizar usuario existente
            usuarioSeleccionado.setNombreUsuario(txtNombreUsuario.getText().trim());
            usuarioSeleccionado.setNombreCompleto(txtNombreCompleto.getText().trim());
            usuarioSeleccionado.setEmail(txtEmail.getText().trim());
            usuarioSeleccionado.setCargo(cmbCargo.getValue());
            usuarioSeleccionado.setTelefono(txtTelefono.getText().trim());
            usuarioSeleccionado.setEstado(cmbEstado.getValue().equals("Activo"));

            if (!txtContrasena.getText().isEmpty()) {
                usuarioSeleccionado.setContrasena(txtContrasena.getText());
            }

            if (usuarioDAO.actualizarUsuario(usuarioSeleccionado)) {
                mostrarAlerta("Éxito", "Usuario actualizado correctamente", AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarUsuarios();
                }
                modoEdicion = false;
                usuarioSeleccionado = null;
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el usuario", AlertType.ERROR);
            }
        } else {
            // Verificar si el usuario ya existe
            if (usuarioDAO.existeUsuario(txtNombreUsuario.getText().trim())) {
                mostrarAlerta("Error", "El nombre de usuario ya existe", AlertType.ERROR);
                txtNombreUsuario.requestFocus();
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(txtNombreUsuario.getText().trim());
            nuevoUsuario.setContrasena(txtContrasena.getText());
            nuevoUsuario.setNombreCompleto(txtNombreCompleto.getText().trim());
            nuevoUsuario.setEmail(txtEmail.getText().trim());
            nuevoUsuario.setCargo(cmbCargo.getValue());
            nuevoUsuario.setTelefono(txtTelefono.getText().trim());
            nuevoUsuario.setEstado(cmbEstado.getValue().equals("Activo"));
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setUltimoAcceso(null);

            if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
                mostrarAlerta("Éxito", "Usuario registrado correctamente", AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarUsuarios();
                }
            } else {
                mostrarAlerta("Error", "No se pudo registrar el usuario", AlertType.ERROR);
            }
        }
    }

    @FXML
    private void editarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            cargarUsuarioEnFormulario(seleccionado);
            modoEdicion = true;
        } else {
            mostrarAlerta("Advertencia", "Seleccione un usuario para editar", AlertType.WARNING);
        }
    }

    @FXML
    private void eliminarUsuario() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar usuarios", AlertType.ERROR);
            return;
        }

        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de eliminar este usuario?");
            alert.setContentText("Usuario: " + seleccionado.getNombreUsuario() + "\nEsta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (usuarioDAO.eliminarUsuario(seleccionado.getIdUsuario())) {
                    mostrarAlerta("Éxito", "Usuario eliminado correctamente", AlertType.INFORMATION);
                    limpiarCamposInterno();
                    if (consultaPanel.isVisible()) {
                        cargarUsuarios();
                    }
                    modoEdicion = false;
                    usuarioSeleccionado = null;
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el usuario", AlertType.ERROR);
                }
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione un usuario para eliminar", AlertType.WARNING);
        }
    }

    @FXML
    private void limpiarCampos() {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        txtNombreUsuario.clear();
        txtNombreCompleto.clear();
        txtEmail.clear();
        txtContrasena.clear();
        txtConfirmarContrasena.clear();
        txtTelefono.clear();
        cmbCargo.setValue(null);
        cmbEstado.setValue("Activo");
        dateFechaRegistro.setValue(java.time.LocalDate.now());
        modoEdicion = false;
        usuarioSeleccionado = null;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombreUsuario.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Ingrese un nombre de usuario", AlertType.ERROR);
            txtNombreUsuario.requestFocus();
            return false;
        }
        if (txtNombreUsuario.getText().trim().length() < 3) {
            mostrarAlerta("Error", "El nombre de usuario debe tener al menos 3 caracteres", AlertType.ERROR);
            txtNombreUsuario.requestFocus();
            return false;
        }
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Ingrese el nombre completo", AlertType.ERROR);
            txtNombreCompleto.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Ingrese el email", AlertType.ERROR);
            txtEmail.requestFocus();
            return false;
        }
        if (!txtEmail.getText().contains("@") || !txtEmail.getText().contains(".")) {
            mostrarAlerta("Error", "Ingrese un email válido (ejemplo@correo.com)", AlertType.ERROR);
            txtEmail.requestFocus();
            return false;
        }
        if (cmbCargo.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un cargo", AlertType.ERROR);
            cmbCargo.requestFocus();
            return false;
        }
        if (!modoEdicion || (modoEdicion && !txtContrasena.getText().isEmpty())) {
            if (txtContrasena.getText().isEmpty()) {
                mostrarAlerta("Error", "Ingrese una contraseña", AlertType.ERROR);
                txtContrasena.requestFocus();
                return false;
            }
            if (txtContrasena.getText().length() < 4) {
                mostrarAlerta("Error", "La contraseña debe tener al menos 4 caracteres", AlertType.ERROR);
                txtContrasena.requestFocus();
                return false;
            }
            if (!txtContrasena.getText().equals(txtConfirmarContrasena.getText())) {
                mostrarAlerta("Error", "Las contraseñas no coinciden", AlertType.ERROR);
                txtConfirmarContrasena.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}