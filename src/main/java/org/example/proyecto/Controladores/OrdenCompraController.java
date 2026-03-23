package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.DetalleCompra;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OrdenCompraController implements Initializable {

    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private TextField txtProducto, txtBuscar;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TableView<DetalleCompra> tblDetalles;
    @FXML private TableColumn<DetalleCompra, String> colProducto;
    @FXML private TableColumn<DetalleCompra, Integer> colCantidad;
    @FXML private TableColumn<DetalleCompra, Double> colPrecioEst, colSubtotal;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar, btnEnviar, btnCancelar;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ObservableList<DetalleCompra> listaDetalles = FXCollections.observableArrayList();
    private Connection conexion;
    private double totalOrden = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarSpinner();
        configurarTabla();
        cargarProveedores();
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1);
        spnCantidad.setValueFactory(valueFactory);
        spnCantidad.setEditable(true);
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioEst.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
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
                proveedor.setRnc(rs.getString("rnc"));
                proveedor.setTelefono(rs.getString("telefono"));

                listaProveedores.add(proveedor);
            }

            cmbProveedor.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProducto() {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor primero", Alert.AlertType.WARNING);
            return;
        }

        String nombreProducto = txtProducto.getText().trim();
        if (nombreProducto.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese el nombre del producto", Alert.AlertType.WARNING);
            return;
        }

        int cantidad = spnCantidad.getValue();

        // Buscar el producto en la base de datos
        String sql = "SELECT * FROM tbl_PRODUCTO WHERE nombre LIKE ? LIMIT 1";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nombreProducto + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idProducto = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                double precioCompra = rs.getDouble("precio_compra");

                // Verificar si el producto ya está en la tabla
                boolean existe = false;
                for (DetalleCompra detalle : listaDetalles) {
                    if (detalle.getIdProducto() == idProducto) {
                        detalle.setCantidad(detalle.getCantidad() + cantidad);
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    DetalleCompra detalle = new DetalleCompra(idProducto, nombre, cantidad, precioCompra, "");
                    listaDetalles.add(detalle);
                }

                tblDetalles.setItems(listaDetalles);
                calcularTotal();
                limpiarCamposProducto();

            } else {
                mostrarAlerta("Advertencia", "Producto no encontrado", Alert.AlertType.WARNING);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar producto: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarProducto() {
        DetalleCompra seleccionado = tblDetalles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        listaDetalles.remove(seleccionado);
        calcularTotal();
    }

    @FXML
    private void enviarRequerimiento() {
        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Seleccione un proveedor", Alert.AlertType.WARNING);
            return;
        }

        if (listaDetalles.isEmpty()) {
            mostrarAlerta("Advertencia", "Agregue productos a la orden", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Orden");
        confirmacion.setHeaderText("¿Enviar orden de compra?");
        confirmacion.setContentText("Proveedor: " + cmbProveedor.getValue().getNombre() + "\nTotal: RD$ " + String.format("%.2f", totalOrden));

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            guardarOrdenCompra();
        }
    }

    private void guardarOrdenCompra() {
        String numeroOrden = "ORD-" + System.currentTimeMillis();
        String sqlCompra = "INSERT INTO tbl_COMPRA (numero_orden, fecha_compra, id_proveedor, estado, total) VALUES (?, CURDATE(), ?, 'Pendiente', ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, numeroOrden);
            pstmt.setInt(2, cmbProveedor.getValue().getIdProveedor());
            pstmt.setDouble(3, totalOrden);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idCompra = rs.getInt(1);

                    // Guardar detalles
                    String sqlDetalle = "INSERT INTO tbl_DETALLE_COMPRA (id_compra, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlDetalle)) {
                        for (DetalleCompra detalle : listaDetalles) {
                            pstmtDetalle.setInt(1, idCompra);
                            pstmtDetalle.setInt(2, detalle.getIdProducto());
                            pstmtDetalle.setInt(3, detalle.getCantidad());
                            pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                            pstmtDetalle.setDouble(5, detalle.getSubtotal());
                            pstmtDetalle.executeUpdate();
                        }
                    }

                    mostrarAlerta("Éxito", " Orden de compra enviada\nNúmero: " + numeroOrden, Alert.AlertType.INFORMATION);
                    limpiarTodo();
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar orden: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Cancelar orden?");
        confirmacion.setContentText("Se perderán todos los datos ingresados");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            limpiarTodo();
        }
    }

    private void calcularTotal() {
        totalOrden = 0.0;
        for (DetalleCompra detalle : listaDetalles) {
            totalOrden += detalle.getSubtotal();
        }
        lblTotal.setText("RD$ " + String.format("%.2f", totalOrden));
    }

    private void limpiarCamposProducto() {
        txtProducto.clear();
        spnCantidad.getValueFactory().setValue(1);
        txtProducto.requestFocus();
    }

    private void limpiarTodo() {
        cmbProveedor.setValue(null);
        txtProducto.clear();
        spnCantidad.getValueFactory().setValue(1);
        listaDetalles.clear();
        totalOrden = 0.0;
        lblTotal.setText("RD$ 0.00");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}