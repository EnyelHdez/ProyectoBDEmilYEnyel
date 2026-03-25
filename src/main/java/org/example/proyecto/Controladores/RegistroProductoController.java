package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.CategoriaProducto;
import org.example.proyecto.Modelos.Producto;
import org.example.proyecto.Modelos.Proveedor;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroProductoController implements Initializable {

    // ── Campos de búsqueda ───────────────────────────────────
    @FXML private TextField txtBuscar;

    // ── Campos del formulario ────────────────────────────────
    @FXML private TextField  txtNombre, txtCodigoBarra, txtDescripcion;
    @FXML private TextField  txtPrecioCosto, txtPrecioVenta, txtPorcentajeItbis;
    @FXML private ComboBox<CategoriaProducto> cbCategoria;
    @FXML private ComboBox<Proveedor> cbProveedor;
    @FXML private Spinner<Integer>    spnStockActual, spnStockMinimo;
    @FXML private CheckBox   chkAplicaItbis, chkRequiereReceta, chkEstado;

    // ── Tabla ────────────────────────────────────────────────
    @FXML private TableView<Producto>              tblProductos;
    @FXML private TableColumn<Producto, Integer>   colId, colStockActual, colStockMinimo;
    @FXML private TableColumn<Producto, String>    colNombre, colCodigoBarra, colCategoria,
            colProveedor;
    @FXML private TableColumn<Producto, Double>    colPrecioCosto, colPrecioVenta;
    @FXML private TableColumn<Producto, Boolean>   colRequiereReceta, colAplicaItbis, colEstado;

    // ── Estado interno ───────────────────────────────────────
    private final ObservableList<Producto>  listaProductos   = FXCollections.observableArrayList();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private final ObservableList<CategoriaProducto> listaCategorias  = FXCollections.observableArrayList();
    private Connection conexion;
    private int idProductoSeleccionado = 0;

    // ─────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarSpinners();
        configurarTabla();
        cargarCategorias();
        cargarProveedores();
        cargarProductos();
        configurarSeleccionTabla();
    }

    private void configurarSpinners() {
        spnStockActual.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0));
        spnStockActual.setEditable(true);

        spnStockMinimo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0));
        spnStockMinimo.setEditable(true);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCodigoBarra.setCellValueFactory(new PropertyValueFactory<>("codigoBarra"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colPrecioCosto.setCellValueFactory(new PropertyValueFactory<>("precioCosto"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colRequiereReceta.setCellValueFactory(new PropertyValueFactory<>("requiereReceta"));
        colAplicaItbis.setCellValueFactory(new PropertyValueFactory<>("aplicaItbis"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarSeleccionTabla() {
        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) cargarDatosEnFormulario(newVal);
                });
    }

    private void cargarDatosEnFormulario(Producto p) {
        idProductoSeleccionado = p.getIdProducto();
        txtNombre.setText(p.getNombre());
        txtCodigoBarra.setText(p.getCodigoBarra());
        txtDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        txtPrecioCosto.setText(String.valueOf(p.getPrecioCosto()));
        txtPrecioVenta.setText(String.valueOf(p.getPrecioVenta()));
        txtPorcentajeItbis.setText(String.valueOf(p.getPorcentajeItbis()));
        spnStockActual.getValueFactory().setValue(p.getStockActual());
        spnStockMinimo.getValueFactory().setValue(p.getStockMinimo());
        chkAplicaItbis.setSelected(p.isAplicaItbis());
        chkRequiereReceta.setSelected(p.isRequiereReceta());
        chkEstado.setSelected(p.isEstado());

        // Seleccionar categoría en el ComboBox
        listaCategorias.stream()
                .filter(c -> c.getIdCategoria() == p.getIdCategoria())
                .findFirst()
                .ifPresent(cbCategoria::setValue);

        // Seleccionar proveedor en el ComboBox (puede ser null)
        if (p.getIdProveedor() != null) {
            listaProveedores.stream()
                    .filter(prov -> prov.getIdProveedor() == p.getIdProveedor())
                    .findFirst()
                    .ifPresent(cbProveedor::setValue);
        } else {
            cbProveedor.setValue(null);
        }
    }

    // ─────────────────────────────────────────────────────────
    // CARGA DE DATOS
    // ─────────────────────────────────────────────────────────

    private void cargarCategorias() {
        listaCategorias.clear();
        String sql = "SELECT id_categoria, nombre FROM tbl_CATEGORIA_PRODUCTO WHERE estado = 1 ORDER BY nombre";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CategoriaProducto c = new CategoriaProducto();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre"));
                listaCategorias.add(c);
            }
            cbCategoria.setItems(listaCategorias);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar categorías: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarProveedores() {
        listaProveedores.clear();
        String sql = "SELECT id_proveedor, razon_social, rnc, telefono " +
                "FROM tbl_PROVEEDOR WHERE estado_temp = 1 ORDER BY razon_social";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setRazonSocial(rs.getString("razon_social"));
                p.setRnc(rs.getString("rnc"));
                p.setTelefono(rs.getString("telefono"));
                listaProveedores.add(p);
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

        String sql = "SELECT p.id_producto, p.id_categoria, p.id_proveedor, " +
                "       p.codigo_barra, p.nombre, p.descripcion, " +
                "       p.precio_costo, p.precio_venta, " +
                "       p.stock_actual, p.stock_minimo, " +
                "       p.requiere_receta, p.aplica_itbis, " +
                "       p.porcentaje_itbis, p.estado, " +
                "       cat.nombre AS nombre_categoria, " +
                "       prov.razon_social AS nombre_proveedor " +
                "FROM tbl_PRODUCTO p " +
                "LEFT JOIN tbl_CATEGORIA_PRODUCTO cat  ON p.id_categoria = cat.id_categoria " +
                "LEFT JOIN tbl_PROVEEDOR  prov ON p.id_proveedor = prov.id_proveedor " +
                "ORDER BY p.id_producto DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaProductos.add(mapearProducto(rs));
            }
            tblProductos.setItems(listaProductos);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /** Mapea un ResultSet a un objeto Producto (reutilizable en búsqueda). */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setIdCategoria(rs.getInt("id_categoria"));

        int idProv = rs.getInt("id_proveedor");
        p.setIdProveedor(rs.wasNull() ? null : idProv);

        p.setCodigoBarra(rs.getString("codigo_barra"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioCosto(rs.getDouble("precio_costo"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setStockActual(rs.getInt("stock_actual"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setRequiereReceta(rs.getBoolean("requiere_receta"));
        p.setAplicaItbis(rs.getBoolean("aplica_itbis"));
        p.setPorcentajeItbis(rs.getDouble("porcentaje_itbis"));
        p.setEstado(rs.getBoolean("estado"));
        p.setNombreCategoria(rs.getString("nombre_categoria"));
        p.setNombreProveedor(rs.getString("nombre_proveedor"));
        return p;
    }

    // ─────────────────────────────────────────────────────────
    // BÚSQUEDA
    // ─────────────────────────────────────────────────────────

    @FXML
    private void buscarProducto() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarProductos(); return; }

        listaProductos.clear();

        String sql = "SELECT p.id_producto, p.id_categoria, p.id_proveedor, " +
                "       p.codigo_barra, p.nombre, p.descripcion, " +
                "       p.precio_costo, p.precio_venta, " +
                "       p.stock_actual, p.stock_minimo, " +
                "       p.requiere_receta, p.aplica_itbis, " +
                "       p.porcentaje_itbis, p.estado, " +
                "       cat.nombre AS nombre_categoria, " +
                "       prov.razon_social AS nombre_proveedor " +
                "FROM tbl_PRODUCTO p " +
                "LEFT JOIN tbl_CATEGORIA_PRODUCTO cat  ON p.id_categoria = cat.id_categoria " +
                "LEFT JOIN tbl_PROVEEDOR  prov ON p.id_proveedor = prov.id_proveedor " +
                "WHERE p.nombre LIKE ? OR p.codigo_barra LIKE ? " +
                "ORDER BY p.id_producto DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) listaProductos.add(mapearProducto(rs));

            tblProductos.setItems(listaProductos);

            if (listaProductos.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados", Alert.AlertType.INFORMATION);

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

    // ─────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────

    @FXML
    private void nuevoProducto() {
        limpiarCampos();
        txtNombre.requestFocus();
    }

    @FXML
    private void guardarProducto() {
        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_PRODUCTO " +
                "(id_categoria, id_proveedor, codigo_barra, nombre, descripcion, " +
                " precio_costo, precio_venta, stock_actual, stock_minimo, " +
                " requiere_receta, aplica_itbis, porcentaje_itbis, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps, false);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Producto registrado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProductos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void editarProducto() {
        if (idProductoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setContentText("¿Desea guardar los cambios en este producto?");
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) return;

        String sql = "UPDATE tbl_PRODUCTO SET " +
                "id_categoria=?, id_proveedor=?, codigo_barra=?, nombre=?, descripcion=?, " +
                "precio_costo=?, precio_venta=?, stock_actual=?, stock_minimo=?, " +
                "requiere_receta=?, aplica_itbis=?, porcentaje_itbis=?, estado=? " +
                "WHERE id_producto=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps, true);
            ps.setInt(14, idProductoSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Producto actualizado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProductos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarProducto() {
        if (idProductoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un producto de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setContentText("¿Está seguro que desea eliminar este producto? Esta acción no se puede deshacer.");
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement(
                "DELETE FROM tbl_PRODUCTO WHERE id_producto=?")) {
            ps.setInt(1, idProductoSeleccionado);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Producto eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarProductos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: el producto tiene registros asociados", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────

    /**
     * Setea los 13 parámetros compartidos entre INSERT y UPDATE.
     * isUpdate=true añade el índice 14 (id_producto) desde el caller.
     */
    private void setearParametros(PreparedStatement ps, boolean isUpdate) throws SQLException {
        ps.setInt(1, cbCategoria.getValue().getIdCategoria());

        Proveedor prov = cbProveedor.getValue();
        if (prov != null) ps.setInt(2, prov.getIdProveedor());
        else              ps.setNull(2, Types.INTEGER);

        ps.setString(3, txtCodigoBarra.getText().trim());
        ps.setString(4, txtNombre.getText().trim());

        String desc = txtDescripcion.getText().trim();
        ps.setString(5, desc.isEmpty() ? null : desc);

        ps.setDouble(6, Double.parseDouble(txtPrecioCosto.getText().trim()));
        ps.setDouble(7, Double.parseDouble(txtPrecioVenta.getText().trim()));
        ps.setInt(8, spnStockActual.getValue());
        ps.setInt(9, spnStockMinimo.getValue());
        ps.setBoolean(10, chkRequiereReceta.isSelected());
        ps.setBoolean(11, chkAplicaItbis.isSelected());

        String itbis = txtPorcentajeItbis.getText().trim();
        ps.setDouble(12, itbis.isEmpty() ? 0.0 : Double.parseDouble(itbis));

        ps.setBoolean(13, chkEstado.isSelected());
    }

    @FXML
    private void limpiarCampos() {
        idProductoSeleccionado = 0;
        txtNombre.clear();
        txtCodigoBarra.clear();
        txtDescripcion.clear();
        txtPrecioCosto.clear();
        txtPrecioVenta.clear();
        txtPorcentajeItbis.clear();
        spnStockActual.getValueFactory().setValue(0);
        spnStockMinimo.getValueFactory().setValue(0);
        chkAplicaItbis.setSelected(false);
        chkRequiereReceta.setSelected(false);
        chkEstado.setSelected(true);
        cbCategoria.setValue(null);
        cbProveedor.setValue(null);
        txtBuscar.clear();
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre del producto es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus(); return false;
        }
        if (cbCategoria.getValue() == null) {
            mostrarAlerta("Validación", "Debe seleccionar una categoría", Alert.AlertType.WARNING);
            cbCategoria.requestFocus(); return false;
        }
        if (txtPrecioCosto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El precio de costo es obligatorio", Alert.AlertType.WARNING);
            txtPrecioCosto.requestFocus(); return false;
        }
        if (txtPrecioVenta.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El precio de venta es obligatorio", Alert.AlertType.WARNING);
            txtPrecioVenta.requestFocus(); return false;
        }
        try {
            double costo = Double.parseDouble(txtPrecioCosto.getText().trim());
            double venta = Double.parseDouble(txtPrecioVenta.getText().trim());
            if (costo < 0 || venta < 0) {
                mostrarAlerta("Validación", "Los precios no pueden ser negativos", Alert.AlertType.WARNING);
                return false;
            }
            if (venta < costo) {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                conf.setTitle("Precio menor al de costo");
                conf.setContentText("El precio de venta es menor al de costo. ¿Desea continuar de todas formas?");
                Optional<ButtonType> r = conf.showAndWait();
                return r.isPresent() && r.get() == ButtonType.OK;
            }
            if (!txtPorcentajeItbis.getText().trim().isEmpty()) {
                double itbis = Double.parseDouble(txtPorcentajeItbis.getText().trim());
                if (itbis < 0 || itbis > 100) {
                    mostrarAlerta("Validación", "El porcentaje de ITBIS debe estar entre 0 y 100", Alert.AlertType.WARNING);
                    txtPorcentajeItbis.requestFocus(); return false;
                }
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "Los campos numéricos deben tener valores válidos (Ej: 150.00)", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}