package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Producto;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroProductoController implements Initializable {

    @FXML private TextField txtBuscar, txtNombre, txtCodigo, txtPrecioCompra, txtPrecioVenta;
    @FXML private ComboBox<Proveedor> cbProveedor;
    @FXML private Spinner<Integer> spnStock;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre, colCodigo, colProveedor;
    @FXML private TableColumn<Producto, Double> colPrecioCompra, colPrecioVenta;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private Button btnNuevo, btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Connection conexion;
    private int idProductoSeleccionado = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarSpinner();
        configurarTabla();
        cargarProveedores();
        cargarProductos();
        configurarSeleccionTabla();
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        spnStock.setValueFactory(valueFactory);
        spnStock.setEditable(true);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
    }

    private void configurarSeleccionTabla() {
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatosEnFormulario(Producto producto) {
        idProductoSeleccionado = producto.getIdProducto();
        txtNombre.setText(producto.getNombre());
        txtCodigo.setText(producto.getCodigoBarras());
        txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        spnStock.getValueFactory().setValue(producto.getStock());

        // Seleccionar el proveedor en el ComboBox
        for (Proveedor p : cbProveedor.getItems()) {
            if (p.getIdProveedor() == producto.getIdProveedor()) {
                cbProveedor.setValue(p);
                break;
            }
        }
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

            cbProveedor.setItems(listaProveedores);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarProductos() {
        listaProductos.clear();
        String sql = "SELECT p.*, prov.nombre AS nombre_proveedor " +
                "FROM tbl_PRODUCTO p " +
                "LEFT JOIN tbl_PROVEEDOR prov ON p.id_proveedor = prov.id_proveedor " +
                "ORDER BY p.id_producto DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCodigoBarras(rs.getString("codigo_barras"));
                producto.setPrecioCompra(rs.getDouble("precio_compra"));
                producto.setPrecioVenta(rs.getDouble("precio_venta"));
                producto.setStock(rs.getInt("stock"));
                producto.setIdProveedor(rs.getInt("id_proveedor"));
                producto.setNombreProveedor(rs.getString("nombre_proveedor"));

                listaProductos.add(producto);
            }

            tblProductos.setItems(listaProductos);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarProducto() {
        String busqueda = txtBuscar.getText().trim();

        if (busqueda.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese un término de búsqueda", Alert.AlertType.WARNING);
            return;
        }

        listaProductos.clear();
        String sql = "SELECT p.*, prov.nombre AS nombre_proveedor " +
                "FROM tbl_PRODUCTO p " +
                "LEFT JOIN tbl_PROVEEDOR prov ON p.id_proveedor = prov.id_proveedor " +
                "WHERE p.nombre LIKE ? OR p.codigo_barras LIKE ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            String parametro = "%" + busqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCodigoBarras(rs.getString("codigo_barras"));
                producto.setPrecioCompra(rs.getDouble("precio_compra"));
                producto.setPrecioVenta(rs.getDouble("precio_venta"));
                producto.setStock(rs.getInt("stock"));
                producto.setIdProveedor(rs.getInt("id_proveedor"));
                producto.setNombreProveedor(rs.getString("nombre_proveedor"));

                listaProductos.add(producto);
            }

            tblProductos.setItems(listaProductos);

            if (listaProductos.isEmpty()) {
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
        cargarProductos();
    }

    @FXML
    private void nuevoProducto() {
        limpiarCampos();
        txtNombre.requestFocus();
    }

    @FXML
    private void guardarProducto() {
        if (!validarCampos()) {
            return;
        }

        String sql = "INSERT INTO tbl_PRODUCTO (nombre, codigo_barras, precio_compra, precio_venta, stock, id_proveedor) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, txtNombre.getText().trim());
            pstmt.setString(2, txtCodigo.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(txtPrecioCompra.getText().trim()));
            pstmt.setDouble(4, Double.parseDouble(txtPrecioVenta.getText().trim()));
            pstmt.setInt(5, spnStock.getValue());
            pstmt.setInt(6, cbProveedor.getValue().getIdProveedor());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", " Producto registrado correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarProductos();
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                mostrarAlerta("Error", " El código de barras ya está registrado", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            e.printStackTrace();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", " Los precios deben ser números válidos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarProducto() {
        if (idProductoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Edición");
        confirmacion.setHeaderText("¿Editar este producto?");
        confirmacion.setContentText("Los cambios se guardarán en la base de datos");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "UPDATE tbl_PRODUCTO SET nombre=?, codigo_barras=?, precio_compra=?, precio_venta=?, stock=?, id_proveedor=? " +
                    "WHERE id_producto=?";

            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, txtNombre.getText().trim());
                pstmt.setString(2, txtCodigo.getText().trim());
                pstmt.setDouble(3, Double.parseDouble(txtPrecioCompra.getText().trim()));
                pstmt.setDouble(4, Double.parseDouble(txtPrecioVenta.getText().trim()));
                pstmt.setInt(5, spnStock.getValue());
                pstmt.setInt(6, cbProveedor.getValue().getIdProveedor());
                pstmt.setInt(7, idProductoSeleccionado);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", " Producto actualizado", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                    cargarProductos();
                }

            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", " Los precios deben ser números válidos", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void eliminarProducto() {
        if (idProductoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar este producto?");
        confirmacion.setContentText(" Esta acción no se puede deshacer");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "DELETE FROM tbl_PRODUCTO WHERE id_producto=?";

            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idProductoSeleccionado);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", " Producto eliminado", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                    cargarProductos();
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint")) {
                    mostrarAlerta("Error", " No se puede eliminar: Tiene compras asociadas", Alert.AlertType.ERROR);
                } else {
                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        idProductoSeleccionado = 0;
        txtNombre.clear();
        txtCodigo.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        spnStock.getValueFactory().setValue(0);
        cbProveedor.setValue(null);
        txtBuscar.clear();
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El nombre del producto es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }

        if (txtPrecioCompra.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El precio de compra es obligatorio", Alert.AlertType.WARNING);
            txtPrecioCompra.requestFocus();
            return false;
        }

        if (txtPrecioVenta.getText().trim().isEmpty()) {
            mostrarAlerta("Advertencia", "El precio de venta es obligatorio", Alert.AlertType.WARNING);
            txtPrecioVenta.requestFocus();
            return false;
        }

        if (cbProveedor.getValue() == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un proveedor", Alert.AlertType.WARNING);
            cbProveedor.requestFocus();
            return false;
        }

        try {
            double precioCompra = Double.parseDouble(txtPrecioCompra.getText().trim());
            double precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());

            if (precioCompra < 0 || precioVenta < 0) {
                mostrarAlerta("Advertencia", "Los precios no pueden ser negativos", Alert.AlertType.WARNING);
                return false;
            }

            if (precioVenta < precioCompra) {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Advertencia");
                confirmacion.setHeaderText("El precio de venta es menor al de compra");
                confirmacion.setContentText("¿Desea continuar de todas formas?");

                Optional<ButtonType> resultado = confirmacion.showAndWait();
                return resultado.isPresent() && resultado.get() == ButtonType.OK;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los precios deben ser números válidos", Alert.AlertType.ERROR);
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