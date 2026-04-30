package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.HistorialReclamacion;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistorialReclamacionController implements Initializable {

    // Componentes de búsqueda
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;

    // Tabla
    @FXML private TableView<HistorialReclamacion> tblHistorial;
    @FXML private TableColumn<HistorialReclamacion, Integer> colId;
    @FXML private TableColumn<HistorialReclamacion, Integer> colIdReclamacion;
    @FXML private TableColumn<HistorialReclamacion, String> colEmpleado;
    @FXML private TableColumn<HistorialReclamacion, String> colFecha;
    @FXML private TableColumn<HistorialReclamacion, String> colAccion;
    @FXML private TableColumn<HistorialReclamacion, String> colEstadoAnterior;
    @FXML private TableColumn<HistorialReclamacion, String> colEstadoNuevo;

    private Connection conexion;
    private final ObservableList<HistorialReclamacion> listaHistorial = FXCollections.observableArrayList();
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarTabla();
        cargarHistorial();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idHistRecl"));
        colIdReclamacion.setCellValueFactory(new PropertyValueFactory<>("idReclamacion"));
        colEmpleado.setCellValueFactory(cellData -> {
            String nombre = obtenerNombreEmpleado(cellData.getValue().getIdEmpleado());
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFecha() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha().format(fmt));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
        colEstadoAnterior.setCellValueFactory(new PropertyValueFactory<>("estadoAnterior"));
        colEstadoNuevo.setCellValueFactory(new PropertyValueFactory<>("estadoNuevo"));

        tblHistorial.setItems(listaHistorial);
    }

    private String obtenerNombreEmpleado(int idEmpleado) {
        String sql = "SELECT nombres FROM tbl_EMPLEADO WHERE id_empleado = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nombres");
        } catch (SQLException e) {
            System.err.println("Error al obtener empleado: " + e.getMessage());
        }
        return "Desconocido";
    }

    private void cargarHistorial() {
        listaHistorial.clear();
        String sql = "SELECT * FROM tbl_HISTORIAL_RECLAMACION ORDER BY id_hist_recl DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                HistorialReclamacion h = new HistorialReclamacion(
                        rs.getString("estado_nuevo"),
                        rs.getString("estado_anterior"),
                        rs.getString("accion"),
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getInt("id_empleado"),
                        rs.getInt("id_reclamacion"),
                        rs.getInt("id_hist_recl")
                );
                listaHistorial.add(h);
            }
            tblHistorial.setItems(listaHistorial);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar historial: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarHistorial(ActionEvent event) {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarHistorial(); return; }

        listaHistorial.clear();
        String sql = "SELECT h.* FROM tbl_HISTORIAL_RECLAMACION h " +
                "WHERE CAST(h.id_reclamacion AS CHAR) LIKE ? " +
                "ORDER BY h.id_hist_recl DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HistorialReclamacion h = new HistorialReclamacion(
                        rs.getString("estado_nuevo"),
                        rs.getString("estado_anterior"),
                        rs.getString("accion"),
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getInt("id_empleado"),
                        rs.getInt("id_reclamacion"),
                        rs.getInt("id_hist_recl")
                );
                listaHistorial.add(h);
            }
            tblHistorial.setItems(listaHistorial);
            if (listaHistorial.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarHistorial();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}