package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Pago;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroPagoController implements Initializable {

    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtNumFactura, txtMonto, txtBuscar;
    @FXML private DatePicker dateFechaFactura, dateFechaPago;
    @FXML private TableView<Pago> tblPagos;
    @FXML private TableColumn<Pago, Integer> colId;
    @FXML private TableColumn<Pago, String> colProveedor, colNumFactura, colMetodoPago, colEstado;
    @FXML private TableColumn<Pago, LocalDate> colFechaPago;
    @FXML private TableColumn<Pago, Double> colMonto;
    @FXML private Label lblTotalPagar, lblEstado;
    @FXML private Button btnNuevo, btnRegistrarPago, btnCancelar;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    private Connection conexion;
    private int idPagoSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarComboBoxes();
        cargarProveedores();
        cargarPagos();
        configurarSeleccionTabla();
        dateFechaPago.setValue(LocalDate.now());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colNumFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarComboBoxes() {
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
                "Transferencia Bancaria", "Cheque", "Efectivo", "Tarjeta de Crédito"
        ));
    }

    private void configurarSeleccionTabla() {
        tblPagos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatosEnFormulario(Pago pago) {
        idPagoSeleccionado = pago.getIdPago();

        // Buscar y seleccionar el proveedor
        for (Proveedor p : cmbProveedor.getItems()) {
            if (p.getIdProveedor() == pago.getIdProveedor()) {
                cmbProveedor.setValue(p);
                break;
            }
        }

        txtNumFactura.setText(pago.getNumeroFactura());
        dateFechaFactura.setValue(pago.getFechaFactura());
        dateFechaPago.setValue(pago.getFechaPago());
        cmbMetodoPago.setValue(pago.getMetodoPago());
        txtMonto.setText(String.valueOf(pago.getMonto()));
        lblTotalPagar.setText("RD$ " + String.format("%.2f", pago.getMonto()));
        lblEstado.setText(pago.getEstado());
    }

    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT * FROM tbl_PROVEEDOR ORDER BY nombre";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));

                listaProveedores.add(proveedor);
            }

            cmbProveedor.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarPagos() {
        listaPagos.clear();
        String sql = "SELECT p.*, prov.nombre AS nombre_proveedor " +
                "FROM tbl_PAGO_PROVEEDOR p " +
                "INNER JOIN tbl_PROVEEDOR prov ON p.id_proveedor = prov.id_proveedor " +
                "ORDER BY p.id_pago DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdProveedor(rs.getInt("id_proveedor"));
                pago.setNombreProveedor(rs.getString("nombre_proveedor"));
                pago.setNumeroFactura(rs.getString("numero_factura"));

                Date fechaFactura = rs.getDate("fecha_factura");
                if (fechaFactura != null) {
                    pago.setFechaFactura(fechaFactura.toLocalDate());
                }

                Date fechaPago = rs.getDate("fecha_pago");
                if (fechaPago != null) {
                    pago.setFechaPago(fechaPago.toLocalDate());
                }

                pago.setMetodoPago(rs.getString("metodo_pago"));
                pago.setMonto(rs.getDouble("monto"));
                pago.setEstado(rs.getString("estado"));

                listaPagos.add(pago);
            }

            tblPagos.setItems(listaPagos);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar pagos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarPago() {
        String busqueda = txtBuscar.getText().trim();

        if (busqueda.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese un término de búsqueda", Alert.AlertType.WARNING);
            return;
        }

        listaPagos.clear();
        String sql = "SELECT p.*, prov.nombre AS nombre_proveedor " +
                "FROM tbl_PAGO_PROVEEDOR p " +
                "INNER JOIN tbl_PROVEEDOR prov ON p.id_proveedor = prov.id_proveedor " +
                "WHERE prov.nombre LIKE ? OR p.numero_factura LIKE ? " +
                "ORDER BY p.id_pago DESC";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String parametro = "%" + busqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdProveedor(rs.getInt("id_proveedor"));
                pago.setNombreProveedor(rs.getString("nombre_proveedor"));
                pago.setNumeroFactura(rs.getString("numero_factura"));

                Date fechaFactura = rs.getDate("fecha_factura");
                if (fechaFactura != null) {
                    pago.setFechaFactura(fechaFactura.toLocalDate());
                }

                Date fechaPago = rs.getDate("fecha_pago");
                if (fechaPago != null) {
                    pago.setFechaPago(fechaPago.toLocalDate());
                }

                pago.setMetodoPago(rs.getString("metodo_pago"));
                pago.setMonto(rs.getDouble("monto"));
                pago.setEstado(rs.getString("estado"));

                listaPagos.add(pago);
            }

            tblPagos.setItems(listaPagos);

            if (listaPagos.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarPagos();
    }

    @FXML
    private void nuevoPago() {
        limpiarCampos();
        cmbProveedor.requestFocus();
    }

    @FXML
    private void registrarPago() {
        if (!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Pago");
        confirmacion.setHeaderText("¿Registrar este pago?");
        confirmacion.setContentText("Monto: RD$ " + txtMonto.getText());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            guardarPago();
        }
    }

    private void guardarPago() {
        String sql = "INSERT INTO tbl_PAGO_PROVEEDOR (id_proveedor, numero_factura, fecha_factura, fecha_pago, metodo_pago, monto, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Completado')";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, cmbProveedor.getValue().getIdProveedor());
            pstmt.setString(2, txtNumFactura.getText().trim());
            pstmt.setDate(3, dateFechaFactura.getValue() != null ? Date.valueOf(dateFechaFactura.getValue()) : null);
            pstmt.setDate(4, Date.valueOf(dateFechaPago.getValue()));
            pstmt.setString(5, cmbMetodoPago.getValue());
            pstmt.setDouble(6, Double.parseDouble(txtMonto.getText().trim()));

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", " Pago registrado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarPagos();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al registrar pago: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto debe ser un número válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancelar() {
        limpiarCampos();
    }

    @FXML
    private void calcularTotal() {
        try {
            if (!txtMonto.getText().trim().isEmpty()) {
                double monto = Double.parseDouble(txtMonto.getText().trim());
                lblTotalPagar.setText("RD$ " + String.format("%.2f", monto));
            }
        } catch (NumberFormatException e) {
            lblTotalPagar.setText("RD$ 0.00");
        }
    }

    private void limpiarCampos() {
        idPagoSeleccionado = 0;
        cmbProveedor.setValue(null);
        txtNumFactura.clear();
        dateFechaFactura.setValue(null);
        dateFechaPago.setValue(LocalDate.now());
        cmbMetodoPago.setValue(null);
        txtMonto.clear();
        txtBuscar.clear();
        lblTotalPagar.setText("RD$ 0.00");
        lblEstado.setText("Pendiente");
        tblPagos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor", Alert.AlertType.WARNING);
            cmbProveedor.requestFocus();
            return false;
        }

        if (dateFechaPago.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione la fecha de pago", Alert.AlertType.WARNING);
            dateFechaPago.requestFocus();
            return false;
        }

        if (cmbMetodoPago.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un método de pago", Alert.AlertType.WARNING);
            cmbMetodoPago.requestFocus();
            return false;
        }

        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese el monto del pago", Alert.AlertType.WARNING);
            txtMonto.requestFocus();
            return false;
        }

        try {
            double monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) {
                mostrarAlerta("Advertencia", "El monto debe ser mayor a 0", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto debe ser un número válido", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}