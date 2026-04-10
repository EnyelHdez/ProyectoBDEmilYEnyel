package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Empleado;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroEmpleadoController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Empleado> tblEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colCedula;
    @FXML private TableColumn<Empleado, String> colNombres;
    @FXML private TableColumn<Empleado, String> colApellidos;
    @FXML private TableColumn<Empleado, Integer> colCargo;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colEmail;
    @FXML private TableColumn<Empleado, LocalDate> colFechaIngreso;
    @FXML private TableColumn<Empleado, BigDecimal> colSalario;
    @FXML private TableColumn<Empleado, Character> colEstado;

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dateFechaNacimiento;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSalarioBase;
    @FXML private DatePicker dateFechaIngreso;
    @FXML private ComboBox<String> cmbDireccion;
    @FXML private ComboBox<String> cmbEstado;

    private ObservableList<Empleado> empleadosList = FXCollections.observableArrayList();
    private Empleado empleadoSeleccionado = null;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConexionBD conexionBD = new ConexionBD();
        conexion = conexionBD.EstablecerConexion();

        if (conexion == null) {
            mostrarAlerta("Error", "No se pudo establecer conexión con la base de datos", Alert.AlertType.ERROR);
            return;
        }

        configurarTabla();
        configurarCombos();
        cargarEmpleados();
        cargarCargos();
        cargarDirecciones();
        configurarSeleccionTabla();
        dateFechaIngreso.setValue(LocalDate.now());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("idCargo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado_temp"));
    }

    private void configurarCombos() {
        cmbSexo.setItems(FXCollections.observableArrayList("M", "F"));
        cmbEstado.setItems(FXCollections.observableArrayList("A", "I"));
    }

    private void cargarCargos() {
        cmbCargo.getItems().clear();
        String sql = "SELECT id_cargo, nombre FROM tbl_CARGO WHERE estado = 1";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_cargo") + " - " + rs.getString("nombre");
                cmbCargo.getItems().add(texto);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar cargos: " + e.getMessage());
        }
    }

    private void cargarDirecciones() {
        cmbDireccion.getItems().clear();
        String sql = "SELECT id_direccion, referencia FROM tbl_DIRECCION";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_direccion") + " - " + rs.getString("referencia");
                cmbDireccion.getItems().add(texto);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar direcciones: " + e.getMessage());
        }
    }

    private void cargarEmpleados() {
        empleadosList.clear();

        if (conexion == null) {
            System.err.println("ERROR: conexion es null");
            mostrarAlerta("Error", "No hay conexión a la base de datos", Alert.AlertType.ERROR);
            return;
        }

        String sql = "SELECT id_empleado, id_cargo, id_direccion, cedula, nombres, apellidos, fecha_nacimiento, sexo, telefono, email, fecha_ingreso, salario_base, estado_temp FROM tbl_EMPLEADO";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String estadoStr = rs.getString("estado_temp");
                char estadoChar = (estadoStr != null && !estadoStr.isEmpty()) ? estadoStr.charAt(0) : 'A';

                Empleado e = new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getInt("id_cargo"),
                        rs.getObject("id_direccion") != null ? rs.getInt("id_direccion") : null,
                        rs.getString("cedula") != null ? rs.getString("cedula") : "",
                        rs.getString("nombres") != null ? rs.getString("nombres") : "",
                        rs.getString("apellidos") != null ? rs.getString("apellidos") : "",
                        rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null,
                        rs.getString("sexo") != null ? rs.getString("sexo").charAt(0) : 'M',
                        rs.getString("telefono") != null ? rs.getString("telefono") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getDate("fecha_ingreso") != null ? rs.getDate("fecha_ingreso").toLocalDate() : null,
                        rs.getBigDecimal("salario_base") != null ? rs.getBigDecimal("salario_base") : BigDecimal.ZERO,
                        estadoChar
                );
                empleadosList.add(e);
            }

            tblEmpleados.setItems(empleadosList);

        } catch (SQLException e) {
            System.err.println("Error SQL al cargar empleados: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        String seleccion = combo.getValue().split(" - ")[0];
        return Integer.parseInt(seleccion);
    }

    private Integer getIdDireccionSeleccionado() {
        if (cmbDireccion.getValue() == null) return null;
        return Integer.parseInt(cmbDireccion.getValue().split(" - ")[0]);
    }

    private void configurarSeleccionTabla() {
        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empleadoSeleccionado = newSelection;
                cargarEmpleadoEnFormulario(newSelection);
            }
        });
    }

    private void cargarEmpleadoEnFormulario(Empleado empleado) {
        txtCedula.setText(empleado.getCedula());
        txtNombres.setText(empleado.getNombres());
        txtApellidos.setText(empleado.getApellidos());
        if (empleado.getFechaNacimiento() != null) {
            dateFechaNacimiento.setValue(empleado.getFechaNacimiento());
        }
        cmbSexo.setValue(String.valueOf(empleado.getSexo()));

        for (String item : cmbCargo.getItems()) {
            if (item.startsWith(String.valueOf(empleado.getIdCargo()) + " -")) {
                cmbCargo.setValue(item);
                break;
            }
        }

        if (empleado.getIdDireccion() != null) {
            for (String item : cmbDireccion.getItems()) {
                if (item.startsWith(String.valueOf(empleado.getIdDireccion()) + " -")) {
                    cmbDireccion.setValue(item);
                    break;
                }
            }
        }

        txtTelefono.setText(empleado.getTelefono());
        txtEmail.setText(empleado.getEmail());
        txtSalarioBase.setText(empleado.getSalarioBase().toString());
        if (empleado.getFechaIngreso() != null) {
            dateFechaIngreso.setValue(empleado.getFechaIngreso());
        }
        cmbEstado.setValue(String.valueOf(empleado.getEstado_temp()));
    }

    @FXML
    public void guardarEmpleado(ActionEvent event) {
        if (!validarCampos()) return;

        int idCargo = getIdSeleccionado(cmbCargo);
        Integer idDireccion = getIdDireccionSeleccionado();
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        LocalDate fechaNacimiento = dateFechaNacimiento.getValue();
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        LocalDate fechaIngreso = dateFechaIngreso.getValue();
        BigDecimal salario = new BigDecimal(txtSalarioBase.getText());
        char estado = cmbEstado.getValue() != null ? cmbEstado.getValue().charAt(0) : 'A';

        // INSERT sin id_empleado porque es IDENTITY
        String sql = "INSERT INTO tbl_EMPLEADO (id_cargo, id_direccion, cedula, nombres, apellidos, fecha_nacimiento, sexo, telefono, email, fecha_ingreso, salario_base, estado_temp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idCargo);
            if (idDireccion != null) pstmt.setInt(2, idDireccion);
            else pstmt.setNull(2, Types.INTEGER);
            pstmt.setString(3, cedula);
            pstmt.setString(4, nombres);
            pstmt.setString(5, apellidos);
            pstmt.setDate(6, fechaNacimiento != null ? Date.valueOf(fechaNacimiento) : null);
            pstmt.setString(7, String.valueOf(sexo));
            pstmt.setString(8, telefono);
            pstmt.setString(9, email);
            pstmt.setDate(10, Date.valueOf(fechaIngreso));
            pstmt.setBigDecimal(11, salario);
            pstmt.setString(12, String.valueOf(estado));

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    System.out.println("Empleado insertado con ID: " + nuevoId);
                }
                mostrarAlerta("Éxito", "Empleado guardado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEmpleados();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarEmpleado(ActionEvent event) {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un empleado de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idCargo = getIdSeleccionado(cmbCargo);
        Integer idDireccion = getIdDireccionSeleccionado();
        String cedula = txtCedula.getText();
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        LocalDate fechaNacimiento = dateFechaNacimiento.getValue();
        char sexo = cmbSexo.getValue() != null ? cmbSexo.getValue().charAt(0) : 'M';
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        LocalDate fechaIngreso = dateFechaIngreso.getValue();
        BigDecimal salario = new BigDecimal(txtSalarioBase.getText());
        char estado = cmbEstado.getValue() != null ? cmbEstado.getValue().charAt(0) : 'A';

        String sql = "UPDATE tbl_EMPLEADO SET id_cargo = ?, id_direccion = ?, cedula = ?, nombres = ?, apellidos = ?, fecha_nacimiento = ?, sexo = ?, telefono = ?, email = ?, fecha_ingreso = ?, salario_base = ?, estado_temp = ? WHERE id_empleado = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCargo);
            if (idDireccion != null) pstmt.setInt(2, idDireccion);
            else pstmt.setNull(2, Types.INTEGER);
            pstmt.setString(3, cedula);
            pstmt.setString(4, nombres);
            pstmt.setString(5, apellidos);
            pstmt.setDate(6, fechaNacimiento != null ? Date.valueOf(fechaNacimiento) : null);
            pstmt.setString(7, String.valueOf(sexo));
            pstmt.setString(8, telefono);
            pstmt.setString(9, email);
            pstmt.setDate(10, Date.valueOf(fechaIngreso));
            pstmt.setBigDecimal(11, salario);
            pstmt.setString(12, String.valueOf(estado));
            pstmt.setInt(13, empleadoSeleccionado.getIdEmpleado());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Empleado actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEmpleados();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarEmpleado(ActionEvent event) {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un empleado para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este empleado?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_EMPLEADO WHERE id_empleado = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, empleadoSeleccionado.getIdEmpleado());
                pstmt.executeUpdate();

                mostrarAlerta("Éxito", "Empleado eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEmpleados();
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
        cmbCargo.setValue(null);
        cmbDireccion.setValue(null);
        txtTelefono.clear();
        txtEmail.clear();
        txtSalarioBase.setText("0.00");
        dateFechaIngreso.setValue(LocalDate.now());
        cmbEstado.setValue(null);
        empleadoSeleccionado = null;
        tblEmpleados.getSelectionModel().clearSelection();
    }

    @FXML
    public void buscarEmpleado(ActionEvent event) {
        String busqueda = txtBuscar.getText();

        if (busqueda.isEmpty()) {
            cargarEmpleados();
            return;
        }

        ObservableList<Empleado> filtrados = FXCollections.observableArrayList();

        for (Empleado e : empleadosList) {
            if (e.getCedula().toLowerCase().contains(busqueda.toLowerCase()) ||
                    e.getNombres().toLowerCase().contains(busqueda.toLowerCase()) ||
                    e.getApellidos().toLowerCase().contains(busqueda.toLowerCase()) ||
                    e.getEmail().toLowerCase().contains(busqueda.toLowerCase()) ||
                    e.getTelefono().contains(busqueda)) {
                filtrados.add(e);
            }
        }

        tblEmpleados.setItems(filtrados);

        if (filtrados.isEmpty()) {
            mostrarAlerta("Información", "No se encontraron empleados con ese criterio", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    public void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarEmpleados();
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
        if (txtApellidos.getText().isEmpty()) {
            mostrarAlerta("Error", "Los apellidos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbCargo.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un cargo", Alert.AlertType.ERROR);
            return false;
        }
        if (txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Error", "El teléfono es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        if (txtEmail.getText().isEmpty()) {
            mostrarAlerta("Error", "El email es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        if (dateFechaIngreso.getValue() == null) {
            mostrarAlerta("Error", "La fecha de ingreso es obligatoria", Alert.AlertType.ERROR);
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