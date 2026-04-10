package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import java.sql.Types;
import org.example.proyecto.Modelos.Reclamacion;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroaReclamacionController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Reclamacion> tblReclamaciones;
    @FXML private TableColumn<Reclamacion, Integer> colId;
    @FXML private TableColumn<Reclamacion, Integer> colCliente;
    @FXML private TableColumn<Reclamacion, Integer> colEmpleado;
    @FXML private TableColumn<Reclamacion, String> colMotivo;
    @FXML private TableColumn<Reclamacion, LocalDateTime> colFecha;
    @FXML private TableColumn<Reclamacion, String> colDescripcion;
    @FXML private TableColumn<Reclamacion, String> colEstado;

    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbMotivo;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtDescripcion;

    private ObservableList<Reclamacion> reclamacionesList = FXCollections.observableArrayList();
    private Reclamacion reclamacionSeleccionada = null;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarCombos();
        cargarReclamaciones();
        cargarClientes();
        cargarEmpleados();
        cargarMotivos();
        configurarSeleccionTabla();
        dateFecha.setValue(LocalDate.now());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReclamacion"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("idMotivo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "EN_REVISION", "RESUELTA", "RECHAZADA"));
    }

    private void cargarClientes() {
        cmbCliente.getItems().clear();
        String sql = "SELECT id_cliente, nombres, apellidos FROM tbl_CLIENTE WHERE estado = 1";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_cliente") + " - " + rs.getString("nombres") + " " + rs.getString("apellidos");
                cmbCliente.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarEmpleados() {
        cmbEmpleado.getItems().clear();
        String sql = "SELECT id_empleado, nombres, apellidos FROM tbl_EMPLEADO";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_empleado") + " - " + rs.getString("nombres") + " " + rs.getString("apellidos");
                cmbEmpleado.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarMotivos() {
        cmbMotivo.getItems().clear();
        String sql = "SELECT id_motivo, nombre FROM tbl_MOTIVO WHERE estado = 1 AND tipo = 'RECLAMACION'";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_motivo") + " - " + rs.getString("nombre");
                cmbMotivo.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar motivos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarReclamaciones() {
        reclamacionesList.clear();
        String sql = "SELECT id_reclamacion, id_cliente, id_empleado, id_motivo, fecha, descripcion, estado FROM tbl_RECLAMACION ORDER BY fecha DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reclamacion r = new Reclamacion(
                        rs.getInt("id_reclamacion"),
                        rs.getInt("id_cliente"),
                        rs.getInt("id_empleado"),
                        rs.getInt("id_motivo"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                );
                reclamacionesList.add(r);
            }
            tblReclamaciones.setItems(reclamacionesList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar reclamaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        String seleccion = combo.getValue().split(" - ")[0];
        return Integer.parseInt(seleccion);
    }

    private void configurarSeleccionTabla() {
        tblReclamaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                reclamacionSeleccionada = newSelection;
                cargarReclamacionEnFormulario(newSelection);
            }
        });
    }

    private void cargarReclamacionEnFormulario(Reclamacion reclamacion) {
        for (String item : cmbCliente.getItems()) {
            if (item.startsWith(String.valueOf(reclamacion.getIdCliente()) + " -")) {
                cmbCliente.setValue(item);
                break;
            }
        }

        for (String item : cmbEmpleado.getItems()) {
            if (item.startsWith(String.valueOf(reclamacion.getIdEmpleado()) + " -")) {
                cmbEmpleado.setValue(item);
                break;
            }
        }

        for (String item : cmbMotivo.getItems()) {
            if (item.startsWith(String.valueOf(reclamacion.getIdMotivo()) + " -")) {
                cmbMotivo.setValue(item);
                break;
            }
        }

        dateFecha.setValue(reclamacion.getFecha().toLocalDate());
        cmbEstado.setValue(reclamacion.getEstado());
        txtDescripcion.setText(reclamacion.getDescripcion());
    }

    @FXML
    public void guardarReclamacion(ActionEvent event) {
        if (!validarCampos()) return;

        int idCliente = getIdSeleccionado(cmbCliente);
        int idEmpleado = getIdSeleccionado(cmbEmpleado);
        int idMotivo = getIdSeleccionado(cmbMotivo);

        // CORRECCIÓN: Usar solo la fecha seleccionada sin la hora
        LocalDate fechaSeleccionada = dateFecha.getValue();
        LocalDateTime fecha = LocalDateTime.of(fechaSeleccionada, LocalTime.of(0, 0, 0));

        String estado = cmbEstado.getValue();
        String descripcion = txtDescripcion.getText();

        // INSERT sin id_reclamacion (es IDENTITY)
        String sql = "INSERT INTO tbl_RECLAMACION (id_cliente, id_empleado, id_motivo, fecha, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idEmpleado);
            pstmt.setInt(3, idMotivo);

            // CORRECCIÓN: Manejar correctamente la fecha
            if (fecha != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(fecha));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setString(5, descripcion);
            pstmt.setString(6, estado);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    System.out.println("Reclamación insertada con ID: " + nuevoId);
                }
                mostrarAlerta("Éxito", "Reclamación guardada correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarReclamaciones();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Para ver el error detallado
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarReclamacion(ActionEvent event) {
        if (reclamacionSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una reclamación de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idCliente = getIdSeleccionado(cmbCliente);
        int idEmpleado = getIdSeleccionado(cmbEmpleado);
        int idMotivo = getIdSeleccionado(cmbMotivo);

        // CORRECCIÓN: Usar solo la fecha seleccionada
        LocalDate fechaSeleccionada = dateFecha.getValue();
        LocalDateTime fecha = LocalDateTime.of(fechaSeleccionada, LocalTime.of(0, 0, 0));

        String estado = cmbEstado.getValue();
        String descripcion = txtDescripcion.getText();

        String sql = "UPDATE tbl_RECLAMACION SET id_cliente = ?, id_empleado = ?, id_motivo = ?, fecha = ?, descripcion = ?, estado = ? WHERE id_reclamacion = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idEmpleado);
            pstmt.setInt(3, idMotivo);

            // CORRECCIÓN: Manejar correctamente la fecha
            if (fecha != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(fecha));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setString(5, descripcion);
            pstmt.setString(6, estado);
            pstmt.setInt(7, reclamacionSeleccionada.getIdReclamacion());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Reclamación actualizada correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarReclamaciones();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Para ver el error detallado
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void eliminarReclamacion(ActionEvent event) {
        if (reclamacionSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una reclamación para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta reclamación?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_RECLAMACION WHERE id_reclamacion = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, reclamacionSeleccionada.getIdReclamacion());
                pstmt.executeUpdate();

                mostrarAlerta("Éxito", "Reclamación eliminada correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarReclamaciones();
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
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbMotivo.setValue(null);
        dateFecha.setValue(LocalDate.now());
        cmbEstado.setValue(null);
        txtDescripcion.clear();
        reclamacionSeleccionada = null;
        tblReclamaciones.getSelectionModel().clearSelection();
    }

    @FXML
    public void buscarReclamacion(ActionEvent event) {
        String busqueda = txtBuscar.getText();
        reclamacionesList.clear();

        String sql = "SELECT id_reclamacion, id_cliente, id_empleado, id_motivo, fecha, descripcion, estado " +
                "FROM tbl_RECLAMACION WHERE CAST(id_reclamacion AS VARCHAR) LIKE ? OR descripcion LIKE ? OR estado LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reclamacion r = new Reclamacion(
                        rs.getInt("id_reclamacion"),
                        rs.getInt("id_cliente"),
                        rs.getInt("id_empleado"),
                        rs.getInt("id_motivo"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                );
                reclamacionesList.add(r);
            }
            tblReclamaciones.setItems(reclamacionesList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarReclamaciones();
    }

    private boolean validarCampos() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un cliente", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un empleado", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbMotivo.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un motivo", Alert.AlertType.ERROR);
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un estado", Alert.AlertType.ERROR);
            return false;
        }
        if (txtDescripcion.getText().isEmpty()) {
            mostrarAlerta("Error", "La descripción es obligatoria", Alert.AlertType.ERROR);
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