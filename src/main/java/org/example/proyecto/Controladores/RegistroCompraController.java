package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.DetalleCompra;
import org.example.proyecto.Modelos.Producto;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroCompraController implements Initializable {

    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private ComboBox<String> cmbMetodoPago, cmbEstado;
    @FXML private TextField txtNumOrden, txtPrecioUnit, txtLote;
    @FXML private DatePicker dateFechaCompra;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TableView<DetalleCompra> tblProductos;
    @FXML private TableColumn<DetalleCompra, String> colProducto, colLote;
    @FXML private TableColumn<DetalleCompra, Integer> colCantidad;
    @FXML private TableColumn<DetalleCompra, Double> colPrecioUnit, colSubtotal;
    @FXML private Label lblTotalCompra;
    @FXML private Button btnAgregarProducto, btnCancelar, btnRegistrarCompra;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ObservableList<DetalleCompra> listaDetalles = FXCollections.observableArrayList();
    private Connection conexion;
    private double totalCompra = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarSpinner();
        configurarTabla();
        configurarComboBoxes();
        cargarProveedores();
        cargarProductos();
        dateFechaCompra.setValue(LocalDate.now());
        generarNumeroOrden();
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1);
        spnCantidad.setValueFactory(valueFactory);
        spnCantidad.setEditable(true);
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnit.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void configurarComboBoxes() {
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
                "Transferencia Bancaria", "Cheque", "Efectivo",
                "Tarjeta de Crédito", "Crédito 30 días", "Crédito 60 días"
        ));

        cmbEstado.setItems(FXCollections.observableArrayList(
                "Pendiente", "En Tránsito", "Recibida", "Completada", "Cancelada"
        ));

        cmbEstado.setValue("Pendiente");
    }

    private void generarNumeroOrden() {
        txtNumOrden.setText("ORD-" + System.currentTimeMillis());
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

    private void cargarProductos() {
        listaProductos.clear();
        String sql = "SELECT * FROM tbl_PRODUCTO ORDER BY nombre";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecioCompra(rs.getDouble("precio_compra"));

                listaProductos.add(producto);
            }

            cmbProducto.setItems(listaProductos);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProducto() {
        if (cmbProducto.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un producto", Alert.AlertType.WARNING);
            return;
        }

        if (txtPrecioUnit.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese el precio unitario", Alert.AlertType.WARNING);
            return;
        }

        try {
            Producto producto = cmbProducto.getValue();
            int cantidad = spnCantidad.getValue();
            double precioUnitario = Double.parseDouble(txtPrecioUnit.getText().trim());
            String lote = txtLote.getText().trim();

            // Verificar si el producto ya está en la tabla
            boolean existe = false;
            for (DetalleCompra detalle : listaDetalles) {
                if (detalle.getIdProducto() == producto.getIdProducto()) {
                    detalle.setCantidad(detalle.getCantidad() + cantidad);
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                DetalleCompra detalle = new DetalleCompra(
                        producto.getIdProducto(),
                        producto.getNombre(),
                        cantidad,
                        precioUnitario,
                        lote
                );
                listaDetalles.add(detalle);
            }

            tblProductos.setItems(listaDetalles);
            tblProductos.refresh();
            calcularTotal();
            limpiarCamposProducto();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El precio debe ser un número válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarProducto() {
        DetalleCompra seleccionado = tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        listaDetalles.remove(seleccionado);
        calcularTotal();
    }

    @FXML
    private void registrarCompra() {
        if (!validarCampos()) {
            return;
        }

        if (listaDetalles.isEmpty()) {
            mostrarAlerta("Advertencia", "Agregue productos a la compra", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Compra");
        confirmacion.setHeaderText("¿Registrar esta compra?");
        confirmacion.setContentText("Total: RD$ " + String.format("%.2f", totalCompra));

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            guardarCompra();
        }
    }

    private void guardarCompra() {
        String sqlCompra = "INSERT INTO tbl_COMPRA (numero_orden, fecha_compra, id_proveedor, metodo_pago, estado, total) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, txtNumOrden.getText().trim());
            pstmt.setDate(2, Date.valueOf(dateFechaCompra.getValue()));
            pstmt.setInt(3, cmbProveedor.getValue().getIdProveedor());
            pstmt.setString(4, cmbMetodoPago.getValue());
            pstmt.setString(5, cmbEstado.getValue());
            pstmt.setDouble(6, totalCompra);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idCompra = rs.getInt(1);

                    // Guardar detalles y actualizar stock
                    String sqlDetalle = "INSERT INTO tbl_DETALLE_COMPRA (id_compra, id_producto, cantidad, precio_unitario, lote, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    String sqlActualizarStock = "UPDATE tbl_PRODUCTO SET stock = stock + ? WHERE id_producto = ?";

                    try (PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlDetalle);
                         PreparedStatement pstmtStock = conexion.prepareStatement(sqlActualizarStock)) {

                        for (DetalleCompra detalle : listaDetalles) {
                            // Insertar detalle
                            pstmtDetalle.setInt(1, idCompra);
                            pstmtDetalle.setInt(2, detalle.getIdProducto());
                            pstmtDetalle.setInt(3, detalle.getCantidad());
                            pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                            pstmtDetalle.setString(5, detalle.getLote());
                            pstmtDetalle.setDouble(6, detalle.getSubtotal());
                            pstmtDetalle.executeUpdate();

                            // Actualizar stock
                            pstmtStock.setInt(1, detalle.getCantidad());
                            pstmtStock.setInt(2, detalle.getIdProducto());
                            pstmtStock.executeUpdate();
                        }
                    }

                    mostrarAlerta("Éxito", " Compra registrada correctamente\nNúmero: " + txtNumOrden.getText(), Alert.AlertType.INFORMATION);
                    limpiarTodo();
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar compra: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Cancelar compra?");
        confirmacion.setContentText("Se perderán todos los datos ingresados");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarTodo();
        }
    }

    private void calcularTotal() {
        totalCompra = 0.0;
        for (DetalleCompra detalle : listaDetalles) {
            totalCompra += detalle.getSubtotal();
        }
        lblTotalCompra.setText("RD$ " + String.format("%.2f", totalCompra));
    }

    private void limpiarCamposProducto() {
        cmbProducto.setValue(null);
        txtPrecioUnit.clear();
        txtLote.clear();
        spnCantidad.getValueFactory().setValue(1);
    }

    private void limpiarTodo() {
        generarNumeroOrden();
        dateFechaCompra.setValue(LocalDate.now());
        cmbProveedor.setValue(null);
        cmbMetodoPago.setValue(null);
        cmbEstado.setValue("Pendiente");
        limpiarCamposProducto();
        listaDetalles.clear();
        totalCompra = 0.0;
        lblTotalCompra.setText("RD$ 0.00");
    }

    private boolean validarCampos() {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor", Alert.AlertType.WARNING);
            cmbProveedor.requestFocus();
            return false;
        }

        if (dateFechaCompra.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione una fecha", Alert.AlertType.WARNING);
            dateFechaCompra.requestFocus();
            return false;
        }

        if (cmbMetodoPago.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un método de pago", Alert.AlertType.WARNING);
            cmbMetodoPago.requestFocus();
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