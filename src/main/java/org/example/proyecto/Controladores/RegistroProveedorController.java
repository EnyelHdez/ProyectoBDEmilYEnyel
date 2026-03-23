package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroProveedorController implements Initializable {

    @FXML private TextField txtBuscar, txtNombre, txtRNC, txtTelefono, txtEmail, txtDireccion;
    @FXML private TableView<Proveedor> tblProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre, colRnc, colTelefono, colEmail, colDireccion;
    @FXML private Button btnNuevo, btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Connection conexion;
    private int idProveedorSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        cargarProveedores();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRnc.setCellValueFactory(new PropertyValueFactory<>("rnc"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
    }

    private void configurarSeleccionTabla() {
        tblProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatosEnFormulario(Proveedor proveedor) {
        idProveedorSeleccionado = proveedor.getIdProveedor();
        txtNombre.setText(proveedor.getNombre());
        txtRNC.setText(proveedor.getRnc());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        txtDireccion.setText(proveedor.getDireccion());
    }

    @FXML
    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR ORDER BY id_proveedor DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setRnc(rs.getString("rnc"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedor.setDireccion(rs.getString("direccion"));

                listaProveedores.add(proveedor);
            }

            tblProveedores.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarProveedor() {
        String busqueda = txtBuscar.getText().trim();

        if (busqueda.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese un término de búsqueda", Alert.AlertType.WARNING);
            return;
        }

        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR WHERE nombre LIKE ? OR rnc LIKE ? OR telefono LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String parametro = "%" + busqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            pstmt.setString(3, parametro);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setRnc(rs.getString("rnc"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedor.setDireccion(rs.getString("direccion"));

                listaProveedores.add(proveedor);
            }

            tblProveedores.setItems(listaProveedores);

            if (listaProveedores.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarProveedores();
    }

    @FXML
    private void nuevoProveedor() {
        limpiarCampos();
        txtNombre.requestFocus();
    }

    @FXML
    private void guardarProveedor() {
        if (!validarCampos()) {
            return;
        }

        String sql = "INSERT INTO tbl_PROVEEDOR (nombre, rnc, telefono, email, direccion) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, txtNombre.getText().trim());
            pstmt.setString(2, txtRNC.getText().trim());
            pstmt.setString(3, txtTelefono.getText().trim());
            pstmt.setString(4, txtEmail.getText().trim());
            pstmt.setString(5, txtDireccion.getText().trim());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", " Proveedor registrado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarProveedores();
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                mostrarAlerta("Error", " El RNC ya está registrado", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void editarProveedor() {
        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Edición");
        confirmacion.setHeaderText("¿Editar este proveedor?");
        confirmacion.setContentText("Los cambios se guardarán en la base de datos");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "UPDATE tbl_PROVEEDOR SET nombre=?, rnc=?, telefono=?, email=?, direccion=? WHERE id_proveedor=?";

            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, txtNombre.getText().trim());
                pstmt.setString(2, txtRNC.getText().trim());
                pstmt.setString(3, txtTelefono.getText().trim());
                pstmt.setString(4, txtEmail.getText().trim());
                pstmt.setString(5, txtDireccion.getText().trim());
                pstmt.setInt(6, idProveedorSeleccionado);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "✅ Proveedor actualizado", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                    cargarProveedores();
                }

            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void eliminarProveedor() {
        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar este proveedor?");
        confirmacion.setContentText("⚠️ Esta acción no se puede deshacer");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_PROVEEDOR WHERE id_proveedor=?";

            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idProveedorSeleccionado);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "✅ Proveedor eliminado", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                    cargarProveedores();
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint")) {
                    mostrarAlerta("Error", "❌ No se puede eliminar: Tiene productos o compras asociadas", Alert.AlertType.ERROR);
                } else {
                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        idProveedorSeleccionado = 0;
        txtNombre.clear();
        txtRNC.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtBuscar.clear();
        tblProveedores.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El nombre es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }

        if (txtRNC.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El RNC/Cédula es obligatorio", Alert.AlertType.WARNING);
            txtRNC.requestFocus();
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El teléfono es obligatorio", Alert.AlertType.WARNING);
            txtTelefono.requestFocus();
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}