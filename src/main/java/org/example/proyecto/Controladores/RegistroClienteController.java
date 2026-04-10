package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Cliente;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroClienteController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, LocalDateTime> colFechaRegistro;
    @FXML private TableColumn<Cliente, Integer> colPuntos;
    @FXML private TableColumn<Cliente, Boolean> colEstado;

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dateFechaNacimiento;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox<String> cmbDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPuntosFidelidad;
    @FXML private ComboBox<String> cmbEstado;

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado = null;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarCombos();
        cargarClientes();
        cargarDirecciones();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula_rnc"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fecha_registro"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos_fidelidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
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

    private void cargarClientes() {
        clientesList.clear();
        String sql = "SELECT id_cliente, id_direccion, cedula_rnc, nombres, apellidos, fecha_nacimiento, sexo, telefono, email, fecha_registro, puntos_fidelidad, estado FROM tbl_CLIENTE";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setIdDireccion(rs.getInt("id_direccion"));
                c.setCedula_rnc(rs.getString("cedula_rnc"));
                c.setNombres(rs.getString("nombres"));
                c.setApellidos(rs.getString("apellidos"));
                c.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                c.setSexo(rs.getString("sexo") != null ? rs.getString("sexo").charAt(0) : 'M');
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setFecha_registro(LocalDate.from(rs.getTimestamp("fecha_registro").toLocalDateTime()));
                c.setPuntos_fidelidad(rs.getInt("puntos_fidelidad"));
                c.setEstado(rs.getBoolean("estado"));
                clientesList.add(c);
            }
            tblClientes.setItems(clientesList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        String seleccion = combo.getValue().split(" - ")[0];
        return Integer.parseInt(seleccion);
    }

    private void configurarSeleccionTabla() {
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                cargarClienteEnFormulario(newSelection);
            }
        });
    }

    private void cargarClienteEnFormulario(Cliente cliente) {
        txtCedula.setText(cliente.getCedula_rnc());
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        if (cliente.getFecha_nacimiento() != null) ;
        cmbSexo.setValue(String.valueOf(cliente.getSexo()));

        for (String item : cmbDireccion.getItems()) {
            if (item.startsWith(String.valueOf(cliente.getIdDireccion()) + " -")) {
                cmbDireccion.setValue(item);
                break;
            }
        }

        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtPuntosFidelidad.setText(String.valueOf(cliente.getPuntos_fidelidad()));
        cmbEstado.setValue(cliente.isEstado() ? "Activo" : "Inactivo");
    }

    @FXML
    public void guardarCliente(ActionEvent event) {
        if (!validarCampos()) return;

        int idDireccion = getIdSeleccionado(cmbDireccion);
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        java.sql.Date fechaNacimiento = dateFechaNacimiento.getValue() != null ? java.sql.Date.valueOf(dateFechaNacimiento.getValue()) : null;
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        int puntos = Integer.parseInt(txtPuntosFidelidad.getText());
        boolean estado = "Activo".equals(cmbEstado.getValue());
        Timestamp fechaRegistro = Timestamp.valueOf(LocalDateTime.now());

        // INSERT sin id_cliente (es IDENTITY)
        String sql = "INSERT INTO tbl_CLIENTE (id_direccion, cedula_rnc, nombres, apellidos, fecha_nacimiento, sexo, telefono, email, fecha_registro, puntos_fidelidad, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idDireccion);
            pstmt.setString(2, cedula);
            pstmt.setString(3, nombres);
            pstmt.setString(4, apellidos);
            pstmt.setDate(5, fechaNacimiento);
            pstmt.setString(6, String.valueOf(sexo));
            pstmt.setString(7, telefono);
            pstmt.setString(8, email);
            pstmt.setTimestamp(9, fechaRegistro);
            pstmt.setInt(10, puntos);
            pstmt.setBoolean(11, estado);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                // Obtener el ID generado automáticamente
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    System.out.println("Cliente insertado con ID: " + nuevoId);
                }
                mostrarAlerta("Éxito", "Cliente guardado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarClientes();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarCliente(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un cliente de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idDireccion = getIdSeleccionado(cmbDireccion);
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        java.sql.Date fechaNacimiento = dateFechaNacimiento.getValue() != null ? java.sql.Date.valueOf(dateFechaNacimiento.getValue()) : null;
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        int puntos = Integer.parseInt(txtPuntosFidelidad.getText());
        boolean estado = "Activo".equals(cmbEstado.getValue());

        // UPDATE sin id_cliente ni fecha_registro
        String sql = "UPDATE tbl_CLIENTE SET id_direccion = ?, cedula_rnc = ?, nombres = ?, apellidos = ?, fecha_nacimiento = ?, sexo = ?, telefono = ?, email = ?, puntos_fidelidad = ?, estado = ? WHERE id_cliente = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idDireccion);
            pstmt.setString(2, cedula);
            pstmt.setString(3, nombres);
            pstmt.setString(4, apellidos);
            pstmt.setDate(5, fechaNacimiento);
            pstmt.setString(6, String.valueOf(sexo));
            pstmt.setString(7, telefono);
            pstmt.setString(8, email);
            pstmt.setInt(9, puntos);
            pstmt.setBoolean(10, estado);
            pstmt.setInt(11, clienteSeleccionado.getIdCliente());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Cliente actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarClientes();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarCliente(ActionEvent event) {
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
                limpiarCampos();
                cargarClientes();
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void limpiarCampos(ActionEvent event) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombres.clear();
        txtApellidos.clear();
        dateFechaNacimiento.setValue(null);
        cmbSexo.setValue(null);
        cmbDireccion.setValue(null);
        txtTelefono.clear();
        txtEmail.clear();
        txtPuntosFidelidad.setText("0");
        cmbEstado.setValue(null);
        clienteSeleccionado = null;
        tblClientes.getSelectionModel().clearSelection();
    }

    @FXML
    public void buscarCliente(ActionEvent event) {
        String busqueda = txtBuscar.getText();
        clientesList.clear();

        String sql = "SELECT id_cliente, id_direccion, cedula_rnc, nombres, apellidos, fecha_nacimiento, sexo, telefono, email, fecha_registro, puntos_fidelidad, estado " +
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
                c.setIdDireccion(rs.getInt("id_direccion"));
                c.setCedula_rnc(rs.getString("cedula_rnc"));
                c.setNombres(rs.getString("nombres"));
                c.setApellidos(rs.getString("apellidos"));
                c.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                c.setSexo(rs.getString("sexo") != null ? rs.getString("sexo").charAt(0) : 'M');
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setFecha_registro(LocalDate.from(rs.getTimestamp("fecha_registro").toLocalDateTime()));
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
    public void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarClientes();
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