package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Pedido;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroPedidoController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, Integer> colId;
    @FXML private TableColumn<Pedido, Integer> colCliente;
    @FXML private TableColumn<Pedido, Integer> colEmpleado;
    @FXML private TableColumn<Pedido, Integer> colMetodoEnvio;
    @FXML private TableColumn<Pedido, LocalDateTime> colFechaPedido;
    @FXML private TableColumn<Pedido, LocalDate> colFechaEstimada;
    @FXML private TableColumn<Pedido, BigDecimal> colSubtotal;
    @FXML private TableColumn<Pedido, BigDecimal> colCostoEnvio;
    @FXML private TableColumn<Pedido, BigDecimal> colTotal;
    @FXML private TableColumn<Pedido, String> colEstado;

    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbMetodoEnvio;
    @FXML private ComboBox<String> cmbDireccionEnvio;
    @FXML private DatePicker dateFechaEstimada;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtCostoEnvio;
    @FXML private TextField txtTotal;

    private ObservableList<Pedido> pedidosList = FXCollections.observableArrayList();
    private Pedido pedidoSeleccionado = null;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarCombos();
        cargarPedidos();
        cargarClientes();
        cargarEmpleados();
        cargarMetodosEnvio();
        cargarDirecciones();
        configurarSeleccionTabla();
        configurarCalculoTotal();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colMetodoEnvio.setCellValueFactory(new PropertyValueFactory<>("idMetodoEnvio"));
        colFechaPedido.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        colFechaEstimada.setCellValueFactory(new PropertyValueFactory<>("fechaEstimada"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colCostoEnvio.setCellValueFactory(new PropertyValueFactory<>("costoEnvio"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "CONFIRMADO", "EN_CAMINO", "ENTREGADO", "CANCELADO"));
    }

    private void configurarCalculoTotal() {
        txtSubtotal.textProperty().addListener((obs, oldVal, newVal) -> calcularTotal());
        txtCostoEnvio.textProperty().addListener((obs, oldVal, newVal) -> calcularTotal());
    }

    private void calcularTotal() {
        try {
            BigDecimal subtotal = new BigDecimal(txtSubtotal.getText().isEmpty() ? "0" : txtSubtotal.getText());
            BigDecimal costoEnvio = new BigDecimal(txtCostoEnvio.getText().isEmpty() ? "0" : txtCostoEnvio.getText());
            BigDecimal total = subtotal.add(costoEnvio);
            txtTotal.setText(total.toString());
        } catch (NumberFormatException e) {
            txtTotal.setText("0.00");
        }
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

    private void cargarMetodosEnvio() {
        cmbMetodoEnvio.getItems().clear();
        String sql = "SELECT id_metodo_envio, nombre FROM tbl_METODO_ENVIO WHERE estado = 1";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_metodo_envio") + " - " + rs.getString("nombre");
                cmbMetodoEnvio.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar métodos de envío: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDirecciones() {
        cmbDireccionEnvio.getItems().clear();
        String sql = "SELECT id_direccion, referencia FROM tbl_DIRECCION";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String texto = rs.getInt("id_direccion") + " - " + rs.getString("referencia");
                cmbDireccionEnvio.getItems().add(texto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar direcciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarPedidos() {
        pedidosList.clear();
        String sql = "SELECT id_pedido, id_cliente, id_empleado, id_metodo_envio, id_dir_envio, fecha_pedido, fecha_estimada, subtotal, costo_envio, total, estado FROM tbl_PEDIDO ORDER BY fecha_pedido DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_cliente"),
                        rs.getInt("id_empleado"),
                        rs.getInt("id_metodo_envio"),
                        rs.getObject("id_dir_envio") != null ? rs.getInt("id_dir_envio") : null,
                        rs.getTimestamp("fecha_pedido").toLocalDateTime(),
                        rs.getDate("fecha_estimada") != null ? rs.getDate("fecha_estimada").toLocalDate() : null,
                        rs.getBigDecimal("subtotal"),
                        rs.getBigDecimal("costo_envio"),
                        rs.getBigDecimal("total"),
                        rs.getString("estado")
                );
                pedidosList.add(p);
            }
            tblPedidos.setItems(pedidosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar pedidos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getIdSeleccionado(ComboBox<String> combo) {
        if (combo.getValue() == null) return 0;
        String seleccion = combo.getValue().split(" - ")[0];
        return Integer.parseInt(seleccion);
    }

    private Integer getIdDireccionSeleccionado() {
        if (cmbDireccionEnvio.getValue() == null) return null;
        return Integer.parseInt(cmbDireccionEnvio.getValue().split(" - ")[0]);
    }

    private void configurarSeleccionTabla() {
        tblPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                pedidoSeleccionado = newSelection;
                cargarPedidoEnFormulario(newSelection);
            }
        });
    }

    private void cargarPedidoEnFormulario(Pedido pedido) {
        for (String item : cmbCliente.getItems()) {
            if (item.startsWith(String.valueOf(pedido.getIdCliente()) + " -")) {
                cmbCliente.setValue(item);
                break;
            }
        }

        for (String item : cmbEmpleado.getItems()) {
            if (item.startsWith(String.valueOf(pedido.getIdEmpleado()) + " -")) {
                cmbEmpleado.setValue(item);
                break;
            }
        }

        for (String item : cmbMetodoEnvio.getItems()) {
            if (item.startsWith(String.valueOf(pedido.getIdMetodoEnvio()) + " -")) {
                cmbMetodoEnvio.setValue(item);
                break;
            }
        }

        if (pedido.getIdDirEnvio() != null) {
            for (String item : cmbDireccionEnvio.getItems()) {
                if (item.startsWith(String.valueOf(pedido.getIdDirEnvio()) + " -")) {
                    cmbDireccionEnvio.setValue(item);
                    break;
                }
            }
        }

        dateFechaEstimada.setValue(pedido.getFechaEstimada());
        cmbEstado.setValue(pedido.getEstado());
        txtSubtotal.setText(pedido.getSubtotal().toString());
        txtCostoEnvio.setText(pedido.getCostoEnvio().toString());
        txtTotal.setText(pedido.getTotal().toString());
    }

    @FXML
    public void guardarPedido(ActionEvent event) {
        if (!validarCampos()) return;

        int idCliente = getIdSeleccionado(cmbCliente);
        int idEmpleado = getIdSeleccionado(cmbEmpleado);
        int idMetodoEnvio = getIdSeleccionado(cmbMetodoEnvio);
        Integer idDirEnvio = getIdDireccionSeleccionado();
        LocalDateTime fechaPedido = LocalDateTime.now();
        LocalDate fechaEstimada = dateFechaEstimada.getValue();
        BigDecimal subtotal = new BigDecimal(txtSubtotal.getText());
        BigDecimal costoEnvio = new BigDecimal(txtCostoEnvio.getText());
        BigDecimal total = new BigDecimal(txtTotal.getText());
        String estado = cmbEstado.getValue();

        String sql = "INSERT INTO tbl_PEDIDO (id_cliente, id_empleado, id_metodo_envio, id_dir_envio, fecha_pedido, fecha_estimada, subtotal, costo_envio, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idEmpleado);
            pstmt.setInt(3, idMetodoEnvio);
            if (idDirEnvio != null) pstmt.setInt(4, idDirEnvio);
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setTimestamp(5, Timestamp.valueOf(fechaPedido));
            pstmt.setDate(6, fechaEstimada != null ? Date.valueOf(fechaEstimada) : null);
            pstmt.setBigDecimal(7, subtotal);
            pstmt.setBigDecimal(8, costoEnvio);
            pstmt.setBigDecimal(9, total);
            pstmt.setString(10, estado);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    System.out.println("Pedido insertado con ID: " + nuevoId);
                }
                mostrarAlerta("Éxito", "Pedido guardado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarPedidos();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarPedido(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un pedido de la tabla para editar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        int idCliente = getIdSeleccionado(cmbCliente);
        int idEmpleado = getIdSeleccionado(cmbEmpleado);
        int idMetodoEnvio = getIdSeleccionado(cmbMetodoEnvio);
        Integer idDirEnvio = getIdDireccionSeleccionado();
        LocalDate fechaEstimada = dateFechaEstimada.getValue();
        BigDecimal subtotal = new BigDecimal(txtSubtotal.getText());
        BigDecimal costoEnvio = new BigDecimal(txtCostoEnvio.getText());
        BigDecimal total = new BigDecimal(txtTotal.getText());
        String estado = cmbEstado.getValue();

        String sql = "UPDATE tbl_PEDIDO SET id_cliente = ?, id_empleado = ?, id_metodo_envio = ?, id_dir_envio = ?, fecha_estimada = ?, subtotal = ?, costo_envio = ?, total = ?, estado = ? WHERE id_pedido = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idEmpleado);
            pstmt.setInt(3, idMetodoEnvio);
            if (idDirEnvio != null) pstmt.setInt(4, idDirEnvio);
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setDate(5, fechaEstimada != null ? Date.valueOf(fechaEstimada) : null);
            pstmt.setBigDecimal(6, subtotal);
            pstmt.setBigDecimal(7, costoEnvio);
            pstmt.setBigDecimal(8, total);
            pstmt.setString(9, estado);
            pstmt.setInt(10, pedidoSeleccionado.getIdPedido());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("Éxito", "Pedido actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarPedidos();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarPedido(ActionEvent event) {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un pedido para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este pedido?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_PEDIDO WHERE id_pedido = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, pedidoSeleccionado.getIdPedido());
                pstmt.executeUpdate();

                mostrarAlerta("Éxito", "Pedido eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarPedidos();
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
        cmbMetodoEnvio.setValue(null);
        cmbDireccionEnvio.setValue(null);
        dateFechaEstimada.setValue(null);
        cmbEstado.setValue(null);
        txtSubtotal.setText("0.00");
        txtCostoEnvio.setText("0.00");
        txtTotal.setText("0.00");
        pedidoSeleccionado = null;
        tblPedidos.getSelectionModel().clearSelection();
    }

    @FXML
    public void buscarPedido(ActionEvent event) {
        String busqueda = txtBuscar.getText();
        pedidosList.clear();

        String sql = "SELECT id_pedido, id_cliente, id_empleado, id_metodo_envio, id_dir_envio, fecha_pedido, fecha_estimada, subtotal, costo_envio, total, estado " +
                "FROM tbl_PEDIDO WHERE CAST(id_pedido AS VARCHAR) LIKE ? OR estado LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_cliente"),
                        rs.getInt("id_empleado"),
                        rs.getInt("id_metodo_envio"),
                        rs.getObject("id_dir_envio") != null ? rs.getInt("id_dir_envio") : null,
                        rs.getTimestamp("fecha_pedido").toLocalDateTime(),
                        rs.getDate("fecha_estimada") != null ? rs.getDate("fecha_estimada").toLocalDate() : null,
                        rs.getBigDecimal("subtotal"),
                        rs.getBigDecimal("costo_envio"),
                        rs.getBigDecimal("total"),
                        rs.getString("estado")
                );
                pedidosList.add(p);
            }
            tblPedidos.setItems(pedidosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void mostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarPedidos();
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
        if (cmbMetodoEnvio.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un método de envío", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un estado", Alert.AlertType.ERROR);
            return false;
        }
        try {
            new BigDecimal(txtSubtotal.getText());
            new BigDecimal(txtCostoEnvio.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los montos deben ser números válidos", Alert.AlertType.ERROR);
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