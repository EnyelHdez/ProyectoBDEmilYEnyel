package org.example.proyecto.Controladores;

import org.example.proyecto.DAO.UsuarioDAO;
import org.example.proyecto.Modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistroUsuarioController {

    @FXML private TextField txtNombreUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private TextField txtTelefono;
    @FXML private Label lblError;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        lblError.setVisible(false);

        // Cargar cargos disponibles
        cmbCargo.getItems().addAll(
                "Administrador",
                "Farmacéutico",
                "Cajero",
                "Almacenista",
                "Auxiliar"
        );
        cmbCargo.setValue("Cajero");

        // Agregar estilos a los campos
        aplicarEstilos();
    }

    private void aplicarEstilos() {
        String estiloBase = "-fx-background-radius: 6; -fx-border-color: #BAD6EE; -fx-border-radius: 6; -fx-padding: 7 10;";
        String estiloFoco = "-fx-background-radius: 6; -fx-border-color: #1A6BAD; -fx-border-radius: 6; -fx-padding: 7 10; -fx-border-width: 2px;";

        txtNombreUsuario.setStyle(estiloBase);
        txtContrasena.setStyle(estiloBase);
        txtConfirmarContrasena.setStyle(estiloBase);
        txtNombreCompleto.setStyle(estiloBase);
        txtEmail.setStyle(estiloBase);
        txtTelefono.setStyle(estiloBase);
        cmbCargo.setStyle(estiloBase);

        // Eventos de foco
        txtNombreUsuario.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtNombreUsuario.setStyle(newVal ? estiloFoco : estiloBase);
        });
        txtContrasena.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtContrasena.setStyle(newVal ? estiloFoco : estiloBase);
        });
        txtConfirmarContrasena.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtConfirmarContrasena.setStyle(newVal ? estiloFoco : estiloBase);
        });
        txtNombreCompleto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtNombreCompleto.setStyle(newVal ? estiloFoco : estiloBase);
        });
        txtEmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtEmail.setStyle(newVal ? estiloFoco : estiloBase);
        });
        txtTelefono.focusedProperty().addListener((obs, oldVal, newVal) -> {
            txtTelefono.setStyle(newVal ? estiloFoco : estiloBase);
        });
        cmbCargo.focusedProperty().addListener((obs, oldVal, newVal) -> {
            cmbCargo.setStyle(newVal ? estiloFoco : estiloBase);
        });
    }

    @FXML
    private void registrar() {
        // Validaciones
        if (!validarCampos()) {
            return;
        }

        // Verificar si el usuario ya existe
        if (usuarioDAO.existeUsuario(txtNombreUsuario.getText().trim())) {
            mostrarError("El nombre de usuario ya existe. Por favor, elija otro.");
            txtNombreUsuario.requestFocus();
            return;
        }

        // Crear usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(txtNombreUsuario.getText().trim());
        nuevoUsuario.setContrasena(txtContrasena.getText()); // En producción, aplicar hash
        nuevoUsuario.setNombreCompleto(txtNombreCompleto.getText().trim());
        nuevoUsuario.setEmail(txtEmail.getText().trim());
        nuevoUsuario.setCargo(cmbCargo.getValue());
        nuevoUsuario.setTelefono(txtTelefono.getText().trim());
        nuevoUsuario.setEstado(true);

        if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
            mostrarExito("✅ Usuario registrado exitosamente");

            // Cerrar la ventana después de 1.5 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::cerrarVentana);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mostrarError("Error al registrar el usuario. Intente nuevamente.");
        }
    }

    private boolean validarCampos() {
        // Validar usuario
        if (txtNombreUsuario.getText().trim().isEmpty()) {
            mostrarError("Ingrese un nombre de usuario");
            txtNombreUsuario.requestFocus();
            return false;
        }

        if (txtNombreUsuario.getText().trim().length() < 3) {
            mostrarError("El nombre de usuario debe tener al menos 3 caracteres");
            txtNombreUsuario.requestFocus();
            return false;
        }

        // Validar contraseña
        if (txtContrasena.getText().isEmpty()) {
            mostrarError("Ingrese una contraseña");
            txtContrasena.requestFocus();
            return false;
        }

        if (txtContrasena.getText().length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres");
            txtContrasena.requestFocus();
            return false;
        }

        // Validar confirmación de contraseña
        if (!txtContrasena.getText().equals(txtConfirmarContrasena.getText())) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmarContrasena.requestFocus();
            return false;
        }

        // Validar nombre completo
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarError("Ingrese su nombre completo");
            txtNombreCompleto.requestFocus();
            return false;
        }

        // Validar email
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarError("Ingrese su correo electrónico");
            txtEmail.requestFocus();
            return false;
        }

        if (!txtEmail.getText().contains("@") || !txtEmail.getText().contains(".")) {
            mostrarError("Ingrese un correo electrónico válido (ejemplo@correo.com)");
            txtEmail.requestFocus();
            return false;
        }

        // Validar cargo
        if (cmbCargo.getValue() == null) {
            mostrarError("Seleccione un cargo");
            cmbCargo.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setVisible(true);

        // Ocultar error después de 3 segundos
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> lblError.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarExito(String mensaje) {
        lblError.setText(mensaje);
        lblError.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setVisible(true);
    }
}