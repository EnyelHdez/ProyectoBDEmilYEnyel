package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.proyecto.Conexion.ConexionBD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ResourceBundle;

public class VentaConSeguroController implements Initializable {

    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbProducto;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private Label lblSeguroInfo;
    @FXML private Label lblPorcentajeCobertura;
    @FXML private Label lblLimiteAnual;
    @FXML private Label lblAcumuladoAnual;

    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotalSinSeguro;
    @FXML private TextField txtMontoSeguro;
    @FXML private TextField txtMontoPaciente;

    private Connection conexion;
    private int idSeguroActual = 0;
    private BigDecimal porcentajeCoberturaActual = BigDecimal.ZERO;
    private BigDecimal limiteAnualActual = null;
    private int idClienteSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarClientes();
            cargarEmpleados();
            cargarProductos();
            cargarComprobantes();

            cmbEstado.setItems(FXCollections.observableArrayList("COMPLETADA", "PENDIENTE", "ANULADA"));
            cmbEstado.setValue("COMPLETADA");

            dateFecha.setValue(LocalDate.now());

            configurarListeners();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarListeners() {
        // Cuando se selecciona un cliente, cargar su seguro
        cmbCliente.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                idClienteSeleccionado = Integer.parseInt(newVal.split(" - ")[0]);
                cargarSeguroCliente(idClienteSeleccionado);
            } else {
                limpiarInfoSeguro();
            }
        });

        // Calcular totales cuando cambian los montos
        txtSubtotal.textProperty().addListener((obs, oldVal, newVal) -> calcularTotales());
        txtDescuento.textProperty().addListener((obs, oldVal, newVal) -> calcularTotales());
        txtItbis.textProperty().addListener((obs, oldVal, newVal) -> calcularTotales());

        // Cargar precio del producto seleccionado
        cmbProducto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.equals("NINGUNO")) {
                cargarPrecioProducto(Integer.parseInt(newVal.split(" - ")[0]));
            }
        });
    }

    private void cargarClientes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_cliente, nombres FROM tbl_CLIENTE WHERE estado = 1 ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_cliente") + " - " + rs.getString("nombres"));
            }
            cmbCliente.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            }
            cmbEmpleado.setItems(lista);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarProductos() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNO");
        String sql = "SELECT id_producto, nombre, precio_venta FROM tbl_PRODUCTO WHERE estado = 1 ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_producto") + " - " + rs.getString("nombre"));
            }
            cmbProducto.setItems(lista);
            cmbProducto.setValue("NINGUNO");
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarComprobantes() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add("NINGUNO");
        String sql = "SELECT id_comprobante, ncf FROM tbl_COMPROBANTE_FISCAL ORDER BY ncf";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt("id_comprobante") + " - " + rs.getString("ncf"));
            }
        } catch (SQLException e) {
            System.out.println("Nota: tabla tbl_COMPROBANTE_FISCAL no disponible: " + e.getMessage());
        }
        cmbComprobante.setItems(lista);
        cmbComprobante.setValue("NINGUNO");
    }

    private void cargarPrecioProducto(int idProducto) {
        String sql = "SELECT precio_venta FROM tbl_PRODUCTO WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtSubtotal.setText(rs.getBigDecimal("precio_venta").toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarSeguroCliente(int idCliente) {
        String sql = "SELECT id_seguro, porcentaje_cob, limite_anual, vigencia_inicio, vigencia_fin " +
                "FROM tbl_SEGURO_MEDICO WHERE id_cliente = ? AND estado = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LocalDate hoy = LocalDate.now();
                LocalDate vigenciaFin = rs.getDate("vigencia_fin").toLocalDate();

                if (hoy.isAfter(vigenciaFin)) {
                    lblSeguroInfo.setText("SEGURO VENCIDO");
                    lblPorcentajeCobertura.setText("0%");
                    idSeguroActual = 0;
                    return;
                }

                idSeguroActual = rs.getInt("id_seguro");
                porcentajeCoberturaActual = rs.getBigDecimal("porcentaje_cob");
                limiteAnualActual = rs.getBigDecimal("limite_anual");

                lblSeguroInfo.setText("Seguro Activo - " + porcentajeCoberturaActual + "% de cobertura");
                lblPorcentajeCobertura.setText(porcentajeCoberturaActual + "%");

                if (limiteAnualActual != null && limiteAnualActual.compareTo(BigDecimal.ZERO) > 0) {
                    lblLimiteAnual.setText("RD$ " + limiteAnualActual.toString());

                    // Obtener acumulado anual
                    BigDecimal acumulado = obtenerAcumuladoAnual(idCliente, Year.now().getValue());
                    lblAcumuladoAnual.setText("RD$ " + acumulado.toString());
                } else {
                    lblLimiteAnual.setText("Sin límite");
                    lblAcumuladoAnual.setText("N/A");
                }

                calcularTotales();
            } else {
                limpiarInfoSeguro();
                mostrarAlerta("Sin seguro", "El cliente no tiene un seguro médico activo", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            limpiarInfoSeguro();
            e.printStackTrace();
        }
    }

    private BigDecimal obtenerAcumuladoAnual(int idCliente, int year) {
        String sql = "SELECT COALESCE(SUM(vs.monto_seguro), 0) as acumulado " +
                "FROM tbl_VENTA_SEGURO vs " +
                "INNER JOIN tbl_VENTA v ON vs.id_venta = v.id_venta " +
                "WHERE v.id_cliente = ? AND YEAR(v.fecha) = ? AND vs.estado = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("acumulado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private void limpiarInfoSeguro() {
        idSeguroActual = 0;
        porcentajeCoberturaActual = BigDecimal.ZERO;
        lblSeguroInfo.setText("Sin seguro médico");
        lblPorcentajeCobertura.setText("0%");
        lblLimiteAnual.setText("N/A");
        lblAcumuladoAnual.setText("N/A");
    }

    private void calcularTotales() {
        try {
            BigDecimal subtotal = new BigDecimal(txtSubtotal.getText().isEmpty() ? "0" : txtSubtotal.getText());
            BigDecimal descuento = new BigDecimal(txtDescuento.getText().isEmpty() ? "0" : txtDescuento.getText());
            BigDecimal itbis = new BigDecimal(txtItbis.getText().isEmpty() ? "0" : txtItbis.getText());

            BigDecimal totalSinSeguro = subtotal.subtract(descuento).add(itbis);
            txtTotalSinSeguro.setText(totalSinSeguro.setScale(2, RoundingMode.HALF_UP).toString());

            if (idSeguroActual > 0 && totalSinSeguro.compareTo(BigDecimal.ZERO) > 0) {
                // Calcular monto del seguro
                BigDecimal montoSeguro = totalSinSeguro.multiply(
                        porcentajeCoberturaActual.divide(new BigDecimal("100"))
                );

                // Verificar límite anual
                if (limiteAnualActual != null && limiteAnualActual.compareTo(BigDecimal.ZERO) > 0) {
                    int yearActual = Year.now().getValue();
                    BigDecimal acumulado = obtenerAcumuladoAnual(idClienteSeleccionado, yearActual);
                    BigDecimal disponible = limiteAnualActual.subtract(acumulado);

                    if (montoSeguro.compareTo(disponible) > 0) {
                        montoSeguro = disponible;
                    }
                    if (montoSeguro.compareTo(BigDecimal.ZERO) < 0) {
                        montoSeguro = BigDecimal.ZERO;
                    }
                }

                BigDecimal montoPaciente = totalSinSeguro.subtract(montoSeguro);

                txtMontoSeguro.setText(montoSeguro.setScale(2, RoundingMode.HALF_UP).toString());
                txtMontoPaciente.setText(montoPaciente.setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                txtMontoSeguro.setText("0.00");
                txtMontoPaciente.setText(totalSinSeguro.setScale(2, RoundingMode.HALF_UP).toString());
            }
        } catch (NumberFormatException e) {
            // Ignorar errores de formato
        }
    }

    @FXML
    private void guardarVentaConSeguro() {
        if (!validarCampos()) {
            return;
        }

        try {
            // 1. Insertar la venta
            String sqlVenta = "INSERT INTO tbl_VENTA (id_cliente, id_empleado, id_comprobante, id_producto, " +
                    "fecha, subtotal, descuento, itbis, total, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement psVenta = conexion.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            // id_cliente
            psVenta.setInt(1, idClienteSeleccionado);
            // id_empleado
            psVenta.setInt(2, Integer.parseInt(cmbEmpleado.getValue().split(" - ")[0]));
            // id_comprobante
            String compVal = cmbComprobante.getValue();
            if (compVal != null && !compVal.equals("NINGUNO")) {
                psVenta.setInt(3, Integer.parseInt(compVal.split(" - ")[0]));
            } else {
                psVenta.setNull(3, Types.INTEGER);
            }
            // id_producto
            String prodVal = cmbProducto.getValue();
            if (prodVal != null && !prodVal.equals("NINGUNO")) {
                psVenta.setInt(4, Integer.parseInt(prodVal.split(" - ")[0]));
            } else {
                psVenta.setNull(4, Types.INTEGER);
            }
            // fecha
            psVenta.setTimestamp(5, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            // subtotal
            psVenta.setBigDecimal(6, new BigDecimal(txtSubtotal.getText()));
            // descuento
            String desc = txtDescuento.getText().trim();
            psVenta.setBigDecimal(7, desc.isEmpty() ? BigDecimal.ZERO : new BigDecimal(desc));
            // itbis
            String itbis = txtItbis.getText().trim();
            psVenta.setBigDecimal(8, itbis.isEmpty() ? BigDecimal.ZERO : new BigDecimal(itbis));
            // total (lo que paga el paciente)
            psVenta.setBigDecimal(9, new BigDecimal(txtMontoPaciente.getText()));
            // estado
            psVenta.setString(10, cmbEstado.getValue());

            psVenta.executeUpdate();

            ResultSet rs = psVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rs.next()) {
                idVenta = rs.getInt(1);
            }

            if (idVenta > 0 && idSeguroActual > 0) {
                // 2. Insertar en ventas_seguro
                String sqlSeguro = "INSERT INTO tbl_VENTA_SEGURO (id_venta, id_seguro, monto_seguro, monto_paciente, estado) " +
                        "VALUES (?, ?, ?, ?, 1)";
                PreparedStatement psSeguro = conexion.prepareStatement(sqlSeguro);
                psSeguro.setInt(1, idVenta);
                psSeguro.setInt(2, idSeguroActual);
                psSeguro.setBigDecimal(3, new BigDecimal(txtMontoSeguro.getText()));
                psSeguro.setBigDecimal(4, new BigDecimal(txtMontoPaciente.getText()));
                psSeguro.executeUpdate();

                mostrarAlerta("Éxito", "Venta con seguro registrada correctamente", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la venta", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un cliente", Alert.AlertType.WARNING);
            return false;
        }
        if (idSeguroActual == 0) {
            mostrarAlerta("Validación", "El cliente no tiene un seguro médico válido", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un empleado", Alert.AlertType.WARNING);
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione una fecha", Alert.AlertType.WARNING);
            return false;
        }
        if (txtSubtotal.getText().isEmpty()) {
            mostrarAlerta("Validación", "Ingrese un subtotal", Alert.AlertType.WARNING);
            return false;
        }
        try {
            BigDecimal subtotal = new BigDecimal(txtSubtotal.getText());
            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("Validación", "El subtotal debe ser mayor a 0", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "Subtotal inválido", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cmbCliente.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}