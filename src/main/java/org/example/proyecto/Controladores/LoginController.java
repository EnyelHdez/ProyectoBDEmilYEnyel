package org.example.proyecto.Controladores;

import org.example.proyecto.DAO.UsuarioDAO;
import org.example.proyecto.Modelos.Usuario;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Button btnRegistrarse;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        lblError.setVisible(false);

        // Permitir login con Enter
        txtContrasena.setOnAction(event -> login());
    }

    @FXML
    private void login() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña");
            return;
        }

        Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, contrasena);

        if (usuarioAutenticado != null) {
            // Iniciar sesión
            SesionUsuario.getInstancia().iniciarSesion(usuarioAutenticado);

            // Cerrar ventana de login y abrir principal
            Stage stageActual = (Stage) btnLogin.getScene().getWindow();
            stageActual.close();

            abrirPantallaPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            txtContrasena.clear();
            txtContrasena.requestFocus();
        }
    }

    @FXML
    private void abrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registroUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Opcional: limpiar campos después de cerrar registro
            txtUsuario.clear();
            txtContrasena.clear();
            lblError.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el registro");
        }
    }

    private void abrirPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PantallaPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sistema de Gestión - Farmacia Kenia Carmen");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la aplicación");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
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
}