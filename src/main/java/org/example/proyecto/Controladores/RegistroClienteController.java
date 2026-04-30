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
import org.example.proyecto.Modelos.Cliente;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroClienteController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, Integer> colPuntos;
    @FXML private TableColumn<Cliente, Boolean> colEstado;

    // Campos del formulario
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPuntosFidelidad;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox<String> cmbDireccion;
    @FXML private ComboBox<String> cmbEstado;

    // Botones
    @FXML private Button btnConsultar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();
    private Connection conexion;
    private Cliente clienteSeleccionado = null;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarCombos();
        cargarDirecciones();
        configurarTabla();
        configurarBotonesPorRol();

        // Inicialmente la tabla está oculta
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);

        // Deshabilitar botones de edición al inicio
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false); // Guardar habilitado para nuevo registro
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula_rnc"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos_fidelidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Listener para seleccionar cliente de la tabla
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarClienteEnFormulario(newSelection);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true); // Deshabilitar guardar en modo edición
            }
        });
    }

    private void cargarClientes() {
        clientesList.clear();
        String sql = "SELECT id_cliente, cedula_rnc, nombres, apellidos, telefono, email, puntos_fidelidad, estado FROM tbl_CLIENTE ORDER BY id_cliente DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setCedula_rnc(rs.getString("cedula_rnc"));
                c.setNombres(rs.getString("nombres"));
                c.setApellidos(rs.getString("apellidos"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setPuntos_fidelidad(rs.getInt("puntos_fidelidad"));
                c.setEstado(rs.getBoolean("estado"));
                clientesList.add(c);
            }
            tblClientes.setItems(clientesList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void buscarCliente() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarClientes();
            return;
        }

        clientesList.clear();
        String sql = "SELECT id_cliente, cedula_rnc, nombres, apellidos, telefono, email, puntos_fidelidad, estado " +
                "FROM tbl_CLIENTE WHERE cedula_rnc LIKE ? OR nombres LIKE ? OR apellidos LIKE ? OR email LIKE ? OR telefono LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);
            pstmt.setString(4, patron);
            pstmt.setString(5, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setCedula_rnc(rs.getString("cedula_rnc"));
                c.setNombres(rs.getString("nombres"));
                c.setApellidos(rs.getString("apellidos"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setPuntos_fidelidad(rs.getInt("puntos_fidelidad"));
                c.setEstado(rs.getBoolean("estado"));
                clientesList.add(c);
            }
            tblClientes.setItems(clientesList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void mostrarTodos() {
        txtBuscar.clear();
        cargarClientes();
    }

    @FXML
    public void abrirConsulta() {
        consultaPanel.setVisible(true);
        consultaPanel.setManaged(true);
        cargarClientes();
    }

    @FXML
    public void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarCombos() {
        cmbSexo.setItems(FXCollections.observableArrayList("M", "F"));
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
    }

    private void cargarDirecciones() {
        cmbDireccion.getItems().clear();
        String sql = "SELECT id_direccion, numero FROM tbl_DIRECCION";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_direccion") + " - " + rs.getString("numero");
                cmbDireccion.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar direcciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

        // Solo Administrador puede editar/eliminar
        if (!"Administrador".equals(rol)) {
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
            return;
        }

        btnEditar.setDisable(!habilitar);
        btnEliminar.setDisable(!habilitar);
    }

    private void cargarClienteEnFormulario(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        txtCedula.setText(cliente.getCedula_rnc());
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtPuntosFidelidad.setText(String.valueOf(cliente.getPuntos_fidelidad()));
        cmbSexo.setValue(String.valueOf(cliente.getSexo()));
        cmbEstado.setValue(cliente.isEstado() ? "Activo" : "Inactivo");

        for (String item : cmbDireccion.getItems()) {
            if (item.startsWith(String.valueOf(cliente.getIdDireccion()) + " -")) {
                cmbDireccion.setValue(item);
                break;
            }
        }
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        String seleccion = combo.getValue().split(" - ")[0];
        return Integer.parseInt(seleccion);
    }

    @FXML
    public void guardarCliente(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar clientes", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        int idDireccion = getIdSeleccionado(cmbDireccion);
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        int puntos = Integer.parseInt(txtPuntosFidelidad.getText());
        boolean estado = "Activo".equals(cmbEstado.getValue());
        Timestamp fechaRegistro = Timestamp.valueOf(LocalDateTime.now());

        String sql = "INSERT INTO tbl_CLIENTE (id_direccion, cedula_rnc, nombres, apellidos, sexo, telefono, email, fecha_registro, puntos_fidelidad, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idDireccion);
            pstmt.setString(2, cedula);
            pstmt.setString(3, nombres);
            pstmt.setString(4, apellidos);
            pstmt.setString(5, String.valueOf(sexo));
            pstmt.setString(6, telefono);
            pstmt.setString(7, email);
            pstmt.setTimestamp(8, fechaRegistro);
            pstmt.setInt(9, puntos);
            pstmt.setBoolean(10, estado);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Cliente guardado correctamente", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarClientes(); // Recargar la tabla si está visible
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarCliente(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar clientes", Alert.AlertType.ERROR);
            return;
        }

        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un cliente para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idDireccion = getIdSeleccionado(cmbDireccion);
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        int puntos = Integer.parseInt(txtPuntosFidelidad.getText());
        boolean estado = "Activo".equals(cmbEstado.getValue());

        String sql = "UPDATE tbl_CLIENTE SET id_direccion = ?, cedula_rnc = ?, nombres = ?, apellidos = ?, sexo = ?, telefono = ?, email = ?, puntos_fidelidad = ?, estado = ? WHERE id_cliente = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idDireccion);
            pstmt.setString(2, cedula);
            pstmt.setString(3, nombres);
            pstmt.setString(4, apellidos);
            pstmt.setString(5, String.valueOf(sexo));
            pstmt.setString(6, telefono);
            pstmt.setString(7, email);
            pstmt.setInt(8, puntos);
            pstmt.setBoolean(9, estado);
            pstmt.setInt(10, clienteSeleccionado.getIdCliente());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Cliente actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarClientes(); // Recargar la tabla
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarCliente(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar clientes", Alert.AlertType.ERROR);
            return;
        }

        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un cliente para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este cliente?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_CLIENTE WHERE id_cliente = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, clienteSeleccionado.getIdCliente());
                pstmt.executeUpdate();

                mostrarAlerta("Éxito", "Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarClientes(); // Recargar la tabla
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void limpiarCampos(ActionEvent event) {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        txtCedula.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtPuntosFidelidad.setText("0");
        cmbSexo.setValue(null);
        cmbDireccion.setValue(null);
        cmbEstado.setValue(null);
        clienteSeleccionado = null;
        modoEdicion = false;

        // Deshabilitar botones de edición y habilitar guardar
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);

        // Limpiar selección de la tabla
        tblClientes.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtCedula.getText().isEmpty()) {
            mostrarAlerta("Error", "La cédula es obligatoria", Alert.AlertType.ERROR);
            return false;
        }
        if (txtNombres.getText().isEmpty()) {
            mostrarAlerta("Error", "Los nombres son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        if (txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Error", "El teléfono es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbDireccion.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una dirección", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}