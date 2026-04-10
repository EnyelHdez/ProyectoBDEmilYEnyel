package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Envio;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroEnvioController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Envio> tblEnvios;
    @FXML private TableColumn<Envio, Integer> colId;
    @FXML private TableColumn<Envio, Integer> colIdPedido;
    @FXML private TableColumn<Envio, String> colNroGuia;
    @FXML private TableColumn<Envio, String> colTransportista;
    @FXML private TableColumn<Envio, LocalDateTime> colFechaDespacho;
    @FXML private TableColumn<Envio, LocalDateTime> colFechaEntrega;
    @FXML private TableColumn<Envio, String> colEstado;
    @FXML private TableColumn<Envio, String> colObservacion;

    @FXML private ComboBox<String> cmbPedido;
    @FXML private TextField txtNroGuia;
    @FXML private TextField txtTransportista;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dateFechaDespacho;
    @FXML private DatePicker dateFechaEntrega;
    @FXML private TextField txtObservacion;

    private ObservableList<Envio> enviosList = FXCollections.observableArrayList();
    private Envio envioSeleccionado = null;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarCombos();
        cargarEnvios();
        cargarPedidos();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));
        colIdPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colNroGuia.setCellValueFactory(new PropertyValueFactory<>("nroGuia"));
        colTransportista.setCellValueFactory(new PropertyValueFactory<>("transportista"));
        colFechaDespacho.setCellValueFactory(new PropertyValueFactory<>("fechaDespacho"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));
    }

    private void configurarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "EN_TRANSITO", "ENTREGADO", "DEVUELTO"));
    }

    private void cargarPedidos() {
        cmbPedido.getItems().clear();
        String sql = "SELECT id_pedido FROM tbl_PEDIDO";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cmbPedido.getItems().add(String.valueOf(rs.getInt("id_pedido")));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar pedidos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarEnvios() {
        enviosList.clear();
        String sql = "SELECT id_envio, id_pedido, nro_guia, fecha_despacho, fecha_entrega, transportista, observacion, estado FROM tbl_ENVIO";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Envio e = new Envio(
                        rs.getInt("id_envio"),
                        rs.getInt("id_pedido"),
                        rs.getString("nro_guia"),
                        rs.getTimestamp("fecha_despacho") != null ? rs.getTimestamp("fecha_despacho").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_entrega") != null ? rs.getTimestamp("fecha_entrega").toLocalDateTime() : null,
                        rs.getString("transportista"),
                        rs.getString("observacion"),
                        rs.getString("estado")
                );
                enviosList.add(e);
            }
            tblEnvios.setItems(enviosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar envíos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarSeleccionTabla() {
        tblEnvios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                envioSeleccionado = newSelection;
                cargarEnvioEnFormulario(newSelection);
            }
        });
    }

    private void cargarEnvioEnFormulario(Envio envio) {
        cmbPedido.setValue(String.valueOf(envio.getIdPedido()));
        txtNroGuia.setText(envio.getNroGuia());
        txtTransportista.setText(envio.getTransportista());
        cmbEstado.setValue(envio.getEstado());
        if (envio.getFechaDespacho() != null) {
            dateFechaDespacho.setValue(envio.getFechaDespacho().toLocalDate());
        }
        if (envio.getFechaEntrega() != null) {
            dateFechaEntrega.setValue(envio.getFechaEntrega().toLocalDate());
        }
        txtObservacion.setText(envio.getObservacion());
    }

    @FXML
    public void guardarEnvio(ActionEvent event) {
        if (!validarCampos()) return;

        int idPedido = Integer.parseInt(cmbPedido.getValue());
        String nroGuia = txtNroGuia.getText();
        String transportista = txtTransportista.getText();
        String estado = cmbEstado.getValue();
        LocalDateTime fechaDespacho = dateFechaDespacho.getValue() != null ? LocalDateTime.of(dateFechaDespacho.getValue(), LocalTime.now()) : null;
        LocalDateTime fechaEntrega = dateFechaEntrega.getValue() != null ? LocalDateTime.of(dateFechaEntrega.getValue(), LocalTime.now()) : null;
        String observacion = txtObservacion.getText();

        // INSERT sin id_envio (es IDENTITY)
        String sql = "INSERT INTO tbl_ENVIO (id_pedido, nro_guia, fecha_despacho, fecha_entrega, transportista, observacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idPedido);
            pstmt.setString(2, nroGuia);
            pstmt.setTimestamp(3, fechaDespacho != null ? Timestamp.valueOf(fechaDespacho) : null);
            pstmt.setTimestamp(4, fechaEntrega != null ? Timestamp.valueOf(fechaEntrega) : null);
            pstmt.setString(5, transportista);
            pstmt.setString(6, observacion);
            pstmt.setString(7, estado);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    System.out.println("Envío insertado con ID: " + nuevoId);
                }
                mostrarAlerta("Éxito", "Envío guardado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEnvios();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarEnvio(ActionEvent event) {
        if (envioSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un envío de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idPedido = Integer.parseInt(cmbPedido.getValue());
        String nroGuia = txtNroGuia.getText();
        String transportista = txtTransportista.getText();
        String estado = cmbEstado.getValue();
        LocalDateTime fechaDespacho = dateFechaDespacho.getValue() != null ? LocalDateTime.of(dateFechaDespacho.getValue(), LocalTime.now()) : null;
        LocalDateTime fechaEntrega = dateFechaEntrega.getValue() != null ? LocalDateTime.of(dateFechaEntrega.getValue(), LocalTime.now()) : null;
        String observacion = txtObservacion.getText();

        String sql = "UPDATE tbl_ENVIO SET id_pedido = ?, nro_guia = ?, fecha_despacho = ?, fecha_entrega = ?, transportista = ?, observacion = ?, estado = ? WHERE id_envio = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idPedido);
            pstmt.setString(2, nroGuia);
            pstmt.setTimestamp(3, fechaDespacho != null ? Timestamp.valueOf(fechaDespacho) : null);
            pstmt.setTimestamp(4, fechaEntrega != null ? Timestamp.valueOf(fechaEntrega) : null);
            pstmt.setString(5, transportista);
            pstmt.setString(6, observacion);
            pstmt.setString(7, estado);
            pstmt.setInt(8, envioSeleccionado.getIdEnvio());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Envío actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEnvios();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarEnvio(ActionEvent event) {
        if (envioSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un envío para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este envío?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_ENVIO WHERE id_envio = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, envioSeleccionado.getIdEnvio());
                pstmt.executeUpdate();

                mostrarAlerta("Éxito", "Envío eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEnvios();
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
        cmbPedido.setValue(null);
        txtNroGuia.clear();
        txtTransportista.clear();
        cmbEstado.setValue(null);
        dateFechaDespacho.setValue(null);
        dateFechaEntrega.setValue(null);
        txtObservacion.clear();
        envioSeleccionado = null;
        tblEnvios.getSelectionModel().clearSelection();
    }

    @FXML
    public void buscarEnvio(ActionEvent event) {
        String busqueda = txtBuscar.getText();
        enviosList.clear();

        String sql = "SELECT id_envio, id_pedido, nro_guia, fecha_despacho, fecha_entrega, transportista, observacion, estado " +
                "FROM tbl_ENVIO WHERE nro_guia LIKE ? OR transportista LIKE ? OR estado LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Envio e = new Envio(
                        rs.getInt("id_envio"),
                        rs.getInt("id_pedido"),
                        rs.getString("nro_guia"),
                        rs.getTimestamp("fecha_despacho") != null ? rs.getTimestamp("fecha_despacho").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_entrega") != null ? rs.getTimestamp("fecha_entrega").toLocalDateTime() : null,
                        rs.getString("transportista"),
                        rs.getString("observacion"),
                        rs.getString("estado")
                );
                enviosList.add(e);
            }
            tblEnvios.setItems(enviosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarEnvios();
    }

    private boolean validarCampos() {
        if (cmbPedido.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un pedido", Alert.AlertType.ERROR);
            return false;
        }
        if (txtNroGuia.getText().isEmpty()) {
            mostrarAlerta("Error", "El número de guía es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        if (txtTransportista.getText().isEmpty()) {
            mostrarAlerta("Error", "El transportista es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un estado", Alert.AlertType.ERROR);
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