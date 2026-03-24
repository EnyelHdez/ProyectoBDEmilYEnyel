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
import java.util.ResourceBundle;

public class RegistroProveedorController implements Initializable {

    @FXML private TextField txtBuscar, txtNombre, txtNombreComercial, txtRNC,
            txtTelefono, txtEmail, txtContacto, txtDireccion;
    @FXML private CheckBox chkEstado;

    @FXML private TableView<Proveedor> tblProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String>  colRazonSocial, colNombreComercial,
            colRnc, colTelefono, colEmail,
            colContacto, colDireccion;
    @FXML private TableColumn<Proveedor, Boolean> colEstado;

    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Connection conexion;
    private int idProveedorSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        cargarProveedores();
        configurarSeleccionTabla();
    }

    // ─────────────────────────────────────────────────────────
    // CONFIGURACIÓN DE TABLA
    // ─────────────────────────────────────────────────────────

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colNombreComercial.setCellValueFactory(new PropertyValueFactory<>("nombreComercial"));
        colRnc.setCellValueFactory(new PropertyValueFactory<>("rnc"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarSeleccionTabla() {
        tblProveedores.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) cargarDatosEnFormulario(newVal);
                });
    }

    private void cargarDatosEnFormulario(Proveedor p) {
        idProveedorSeleccionado = p.getIdProveedor();
        txtNombre.setText(p.getRazonSocial());
        txtNombreComercial.setText(p.getNombreComercial());
        txtRNC.setText(p.getRnc());
        txtTelefono.setText(p.getTelefono());
        txtEmail.setText(p.getEmail());
        txtContacto.setText(p.getContacto());
        txtDireccion.setText(p.getDireccion());
        chkEstado.setSelected(p.isEstado());
    }

    // ─────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────

    @FXML
    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR ORDER BY id_proveedor DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setRazonSocial(rs.getString("razon_social"));
                p.setNombreComercial(rs.getString("nombre_comercial"));
                p.setRnc(rs.getString("rnc"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setContacto(rs.getString("contacto"));
                p.setDireccion(rs.getString("direccion"));
                p.setEstado(rs.getBoolean("estado"));
                listaProveedores.add(p);
            }
            tblProveedores.setItems(listaProveedores);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarProveedor() {
        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_PROVEEDOR (rnc, razon_social, nombre_comercial, telefono, email, contacto, direccion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, txtRNC.getText());
            ps.setString(2, txtNombre.getText());
            ps.setString(3, txtNombreComercial.getText());
            ps.setString(4, txtTelefono.getText());
            ps.setString(5, txtEmail.getText());
            ps.setString(6, txtContacto.getText());
            ps.setString(7, txtDireccion.getText());
            ps.setBoolean(8, chkEstado.isSelected());

            ps.executeUpdate();
            mostrarAlerta("Éxito", "Proveedor guardado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProveedores();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarProveedor() {
        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }

        String sql = "UPDATE tbl_PROVEEDOR SET rnc=?, razon_social=?, nombre_comercial=?, " +
                "telefono=?, email=?, contacto=?, direccion=?, estado=? WHERE id_proveedor=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, txtRNC.getText());
            ps.setString(2, txtNombre.getText());
            ps.setString(3, txtNombreComercial.getText());
            ps.setString(4, txtTelefono.getText());
            ps.setString(5, txtEmail.getText());
            ps.setString(6, txtContacto.getText());
            ps.setString(7, txtDireccion.getText());
            ps.setBoolean(8, chkEstado.isSelected());
            ps.setInt(9, idProveedorSeleccionado);

            ps.executeUpdate();
            mostrarAlerta("Actualizado", "Proveedor actualizado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProveedores();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarProveedor() {
        if (idProveedorSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }

        String sql = "DELETE FROM tbl_PROVEEDOR WHERE id_proveedor=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProveedorSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Eliminado", "Proveedor eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProveedores();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    // BÚSQUEDA
    // ─────────────────────────────────────────────────────────

    @FXML
    private void buscarProveedor() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarProveedores(); return; }

        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR " +
                "WHERE razon_social LIKE ? OR rnc LIKE ? OR telefono LIKE ? OR direccion LIKE ? " +
                "ORDER BY id_proveedor DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setRazonSocial(rs.getString("razon_social"));
                p.setNombreComercial(rs.getString("nombre_comercial"));
                p.setRnc(rs.getString("rnc"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setContacto(rs.getString("contacto"));
                p.setDireccion(rs.getString("direccion"));
                p.setEstado(rs.getBoolean("estado"));
                listaProveedores.add(p);
            }
            tblProveedores.setItems(listaProveedores);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarProveedores();
    }

    // ─────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────

    @FXML
    private void limpiarCampos() {
        idProveedorSeleccionado = 0;
        txtNombre.clear();
        txtNombreComercial.clear();
        txtRNC.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtContacto.clear();
        txtDireccion.clear();
        txtBuscar.clear();
        chkEstado.setSelected(false);
        tblProveedores.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isBlank()) {
            mostrarAlerta("Validación", "La razón social es obligatoria", Alert.AlertType.WARNING);
            return false;
        }
        if (txtRNC.getText().isBlank()) {
            mostrarAlerta("Validación", "El RNC es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}