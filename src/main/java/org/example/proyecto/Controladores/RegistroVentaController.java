package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Venta;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroVentaController implements Initializable {

    // ── Campos del formulario ──────────────────────────────────────
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbEstado;

    // ── Tabla ──────────────────────────────────────────────────────
    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Integer> colIdCliente;
    @FXML private TableColumn<Venta, Integer> colIdEmpleado;
    @FXML private TableColumn<Venta, Integer> colIdComprobante;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, BigDecimal> colSubtotal;
    @FXML private TableColumn<Venta, BigDecimal> colDescuento;
    @FXML private TableColumn<Venta, BigDecimal> colItbis;
    @FXML private TableColumn<Venta, BigDecimal> colTotal;
    @FXML private TableColumn<Venta, String> colEstado;

    private int idVentaSeleccionada = 0;
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conexion = new ConexionBD().EstablecerConexion();


        // Cargar clientes desde BD
        cargarClientes();

        // Cargar empleados desde BD
        cargarEmpleados();

        // Cargar comprobantes desde BD
        cargarComprobantes();

        dateFecha.setValue(LocalDate.now());

        configurarTabla();
        cargarTabla();

        // Listener para selección en tabla
        tblVentas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, sel) -> {
                    if (sel != null) {
                        idVentaSeleccionada = sel.getIdVenta();
                        rellenarFormulario(sel);
                    }
                });
    }

    private void cargarClientes() {
        ObservableList<String> clientes = FXCollections.observableArrayList();
        String sql = "SELECT id_cliente, nombres FROM tbl_CLIENTE WHERE estado = 1 ORDER BY nombres";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(rs.getInt("id_cliente") + " - " + rs.getString("nombres"));
            }
            cmbCliente.setItems(clientes);
        } catch (SQLException e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> empleados = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            }
            cmbEmpleado.setItems(empleados);
        } catch (SQLException e) {
            mostrarError("Error al cargar empleados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarComprobantes() {
        ObservableList<String> comprobantes = FXCollections.observableArrayList();

        // Agregar opción "NINGUNO" por defecto
        comprobantes.add("NINGUNO");

        try {
            // Verificar qué columnas existen en la tabla
            String sql = "SELECT id_comprobante, nfc FROM tbl_COMPROBANTE_FISCAL WHERE estado = 'EMITIDO' ORDER BY nfc";

            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                boolean hayDatos = false;
                while (rs.next()) {
                    int id = rs.getInt("id_comprobante");
                    String nfc = rs.getString("nfc");
                    if (nfc != null && !nfc.trim().isEmpty()) {
                        comprobantes.add(id + " - " + nfc);
                        hayDatos = true;
                    } else {
                        comprobantes.add(String.valueOf(id));
                        hayDatos = true;
                    }
                }

                if (!hayDatos) {
                    System.out.println("No hay comprobantes fiscales registrados");
                }

                cmbComprobante.setItems(comprobantes);

            } catch (SQLException e) {
                // Si la columna nfc no existe, intentar con otra columna
                System.out.println("Error con columna nfc, intentando con nombre...");
                try {
                    String sql2 = "SELECT id_comprobante, nfc FROM tbl_COMPROBANTE_FISCAL WHERE estado = 'EMITIDO' ORDER BY nfc";
                    try (Statement stmt2 = conexion.createStatement();
                         ResultSet rs2 = stmt2.executeQuery(sql2)) {
                        while (rs2.next()) {
                            comprobantes.add(rs2.getInt("id_comprobante") + " - " + rs2.getString("nfc"));
                        }
                        cmbComprobante.setItems(comprobantes);
                    }
                } catch (SQLException e2) {
                    // Si no encuentra la tabla o columnas, solo mostrar IDs
                    try {
                        String sql3 = "SELECT id_comprobante FROM tbl_COMPROBANTE_FISCAL WHERE estado = 'Activo' ORDER BY id_comprobante";
                        try (Statement stmt3 = conexion.createStatement();
                             ResultSet rs3 = stmt3.executeQuery(sql3)) {
                            while (rs3.next()) {
                                comprobantes.add(String.valueOf(rs3.getInt("id_comprobante")));
                            }
                            cmbComprobante.setItems(comprobantes);
                        }
                    } catch (SQLException e3) {
                        System.out.println("Tabla tbl_COMPROBANTE_FISCAL no encontrada o no tiene datos");
                        cmbComprobante.setItems(comprobantes);
                    }
                }
            }

            // Seleccionar NINGUNO por defecto
            cmbComprobante.setValue("NINGUNO");

        } catch (Exception e) {
            System.out.println("Error al cargar comprobantes: " + e.getMessage());
            cmbComprobante.setItems(comprobantes);
            cmbComprobante.setValue("NINGUNO");
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colIdEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colIdComprobante.setCellValueFactory(new PropertyValueFactory<>("idComprobante"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colItbis.setCellValueFactory(new PropertyValueFactory<>("itbis"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblVentas.setItems(listaVentas);
    }

    private void cargarTabla() {
        listaVentas.clear();
        String sql = "SELECT * FROM tbl_VENTA ORDER BY id_venta DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta venta = new Venta(
                        rs.getInt("id_venta"),
                        rs.getObject("id_cliente") != null ? rs.getInt("id_cliente") : null,
                        rs.getInt("id_empleado"),
                        rs.getObject("id_comprobante") != null ? rs.getInt("id_comprobante") : null,
                        rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null,
                        rs.getBigDecimal("subtotal"),
                        rs.getBigDecimal("descuento"),
                        rs.getBigDecimal("itbis"),
                        rs.getBigDecimal("total"),
                        rs.getString("estado")
                );
                listaVentas.add(venta);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar las ventas:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rellenarFormulario(Venta v) {
        // Rellenar cliente
        if (v.getIdCliente() != null) {
            String clienteStr = v.getIdCliente() + " - ";
            for (String c : cmbCliente.getItems()) {
                if (c.startsWith(clienteStr)) {
                    cmbCliente.setValue(c);
                    break;
                }
            }
        }

        // Rellenar empleado
        String empleadoStr = v.getIdEmpleado() + " - ";
        for (String e : cmbEmpleado.getItems()) {
            if (e.startsWith(empleadoStr)) {
                cmbEmpleado.setValue(e);
                break;
            }
        }

        // Rellenar comprobante
        if (v.getIdComprobante() != null) {
            String comprobanteStr = v.getIdComprobante() + " - ";
            for (String comp : cmbComprobante.getItems()) {
                if (comp.startsWith(comprobanteStr)) {
                    cmbComprobante.setValue(comp);
                    break;
                }
            }
        } else {
            cmbComprobante.setValue("NINGUNO");
        }

        dateFecha.setValue(v.getFecha() != null ? v.getFecha().toLocalDate() : LocalDate.now());
        txtSubtotal.setText(v.getSubtotal() != null ? v.getSubtotal().toPlainString() : "");
        txtDescuento.setText(v.getDescuento() != null ? v.getDescuento().toPlainString() : "");
        txtItbis.setText(v.getItbis() != null ? v.getItbis().toPlainString() : "");
        txtTotal.setText(v.getTotal() != null ? v.getTotal().toPlainString() : "");
        cmbEstado.setValue(v.getEstado());
    }

    @FXML
    private void guardarVenta(ActionEvent event) {
        if (idVentaSeleccionada == 0) {
            registrarVenta();
        } else {
            actualizarVenta();
        }
    }

    private Integer obtenerIdFromCombo(String comboValue) {
        if (comboValue == null || comboValue.equals("NINGUNO")) return null;
        try {
            return Integer.parseInt(comboValue.split(" - ")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private void registrarVenta() {
        if (!validar()) return;

        Integer idCliente = obtenerIdFromCombo(cmbCliente.getValue());
        Integer idEmpleado = obtenerIdFromCombo(cmbEmpleado.getValue());
        Integer idComprobante = obtenerIdFromCombo(cmbComprobante.getValue());

        String sql = "INSERT INTO tbl_VENTA (id_cliente, id_empleado, id_comprobante, fecha, " +
                "subtotal, descuento, itbis, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (idCliente != null) ps.setInt(1, idCliente);
            else ps.setNull(1, Types.INTEGER);

            ps.setInt(2, idEmpleado);

            if (idComprobante != null) ps.setInt(3, idComprobante);
            else ps.setNull(3, Types.INTEGER);

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setBigDecimal(5, new BigDecimal(txtSubtotal.getText().trim()));
            ps.setBigDecimal(6, new BigDecimal(txtDescuento.getText().trim()));
            ps.setBigDecimal(7, new BigDecimal(txtItbis.getText().trim()));
            ps.setBigDecimal(8, new BigDecimal(txtTotal.getText().trim()));
            ps.setString(9, cmbEstado.getValue());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int idGenerado = keys.next() ? keys.getInt(1) : -1;

            mostrarExito("Venta registrada correctamente.\nID generado: " + idGenerado);
            limpiarCampos();
            cargarTabla();

        } catch (SQLException e) {
            mostrarError("Error al registrar la venta:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarVenta() {

    }

    @FXML
    private void eliminarVenta(ActionEvent event) {
        if (idVentaSeleccionada == 0) {
            mostrarError("Seleccione una venta de la tabla");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro?");
        conf.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = conf.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "DELETE FROM tbl_VENTA WHERE id_venta = ?")) {
                ps.setInt(1, idVentaSeleccionada);
                ps.executeUpdate();

                mostrarExito("Venta eliminada correctamente");
                limpiarCampos();
                cargarTabla();

            } catch (SQLException e) {
                mostrarError("Error al eliminar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        cmbCliente.setValue(null);
        cmbEmpleado.setValue(null);
        cmbComprobante.setValue("NINGUNO");
        dateFecha.setValue(LocalDate.now());
        txtSubtotal.clear();
        txtDescuento.clear();
        txtItbis.clear();
        txtTotal.clear();
        cmbEstado.setValue(null);
        idVentaSeleccionada = 0;
        tblVentas.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarVenta(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarTabla();
            return;
        }

        ObservableList<Venta> filtrados = FXCollections.observableArrayList();
        for (Venta v : listaVentas) {
            if (String.valueOf(v.getIdVenta()).contains(busqueda) ||
                    (v.getIdCliente() != null && String.valueOf(v.getIdCliente()).contains(busqueda)) ||
                    String.valueOf(v.getIdEmpleado()).contains(busqueda)) {
                filtrados.add(v);
            }
        }
        tblVentas.setItems(filtrados);
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        cargarTabla();
        txtBuscar.clear();
    }

    private boolean validar() {
        if (cmbCliente.getValue() == null) {
            mostrarError("Seleccione un cliente.");
            cmbCliente.requestFocus();
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarError("Seleccione un empleado.");
            cmbEmpleado.requestFocus();
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarError("Seleccione una fecha.");
            dateFecha.requestFocus();
            return false;
        }
        if (!esDecimalNoNegativo(txtSubtotal.getText())) {
            mostrarError("Subtotal debe ser un número decimal ≥ 0.");
            txtSubtotal.requestFocus();
            return false;
        }
        if (!esDecimalNoNegativo(txtDescuento.getText())) {
            mostrarError("Descuento debe ser un número decimal ≥ 0.");
            txtDescuento.requestFocus();
            return false;
        }
        if (!esDecimalNoNegativo(txtItbis.getText())) {
            mostrarError("ITBIS debe ser un número decimal ≥ 0.");
            txtItbis.requestFocus();
            return false;
        }
        if (!esDecimalNoNegativo(txtTotal.getText())) {
            mostrarError("Total debe ser un número decimal ≥ 0.");
            txtTotal.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarError("Seleccione un estado.");
            cmbEstado.requestFocus();
            return false;
        }
        return true;
    }

    private boolean esDecimalNoNegativo(String texto) {
        try {
            return new BigDecimal(texto.trim()).compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void editarVenta(ActionEvent actionEvent) {
        if (!validar()) return;

        Integer idCliente = obtenerIdFromCombo(cmbCliente.getValue());
        Integer idEmpleado = obtenerIdFromCombo(cmbEmpleado.getValue());
        Integer idComprobante = obtenerIdFromCombo(cmbComprobante.getValue());

        String sql = "UPDATE tbl_VENTA SET " +
                "id_cliente = ?, id_empleado = ?, id_comprobante = ?, fecha = ?, " +
                "subtotal = ?, descuento = ?, itbis = ?, total = ?, estado = ? " +
                "WHERE id_venta = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (idCliente != null) ps.setInt(1, idCliente);
            else ps.setNull(1, Types.INTEGER);

            ps.setInt(2, idEmpleado);

            if (idComprobante != null) ps.setInt(3, idComprobante);
            else ps.setNull(3, Types.INTEGER);

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setBigDecimal(5, new BigDecimal(txtSubtotal.getText().trim()));
            ps.setBigDecimal(6, new BigDecimal(txtDescuento.getText().trim()));
            ps.setBigDecimal(7, new BigDecimal(txtItbis.getText().trim()));
            ps.setBigDecimal(8, new BigDecimal(txtTotal.getText().trim()));
            ps.setString(9, cmbEstado.getValue());
            ps.setInt(10, idVentaSeleccionada);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                mostrarExito("Venta actualizada correctamente.");
                limpiarCampos();
                cargarTabla();
            } else {
                mostrarError("No se encontró la venta con ID: " + idVentaSeleccionada);
            }

        } catch (SQLException e) {
            mostrarError("Error al actualizar:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

}