package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Producto;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CatalogoProductosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecioVenta;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colEstado;

    @FXML private Label lblTotalRegistros;
    @FXML private Label lblStockTotal;
    @FXML private Label lblProductosActivos;

    private Connection conexion;
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        if (conexion == null) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
            return;
        }

        configurarTabla();
        configurarFiltros();
        cargarProductos();
        cargarCategorias();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));

        // Columna de estado con colores
        colEstado.setCellValueFactory(cellData -> {
            boolean estado = cellData.getValue().isEstado();
            return new javafx.beans.property.SimpleStringProperty(estado ? "Activo" : "Inactivo");
        });

        // Formatear precio
        colPrecioVenta.setCellFactory(col -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText("RD$ " + String.format("%,.2f", item));
            }
        });

        // Formatear estado con colores
        colEstado.setCellFactory(col -> new TableCell<Producto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Activo")) {
                        setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tblProductos.setItems(listaProductos);
    }

    private void configurarFiltros() {
        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbEstado.setValue("Todos");

        cmbCategoria.valueProperty().addListener((obs, old, val) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, old, val) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, old, val) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (filteredList == null) return;

        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String categoria = cmbCategoria.getValue();
        String estado = cmbEstado.getValue();

        filteredList.setPredicate(producto -> {
            // Filtro por búsqueda
            if (!busqueda.isEmpty()) {
                boolean coincide = (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(busqueda)) ||
                        (producto.getNombreCategoria() != null && producto.getNombreCategoria().toLowerCase().contains(busqueda));
                if (!coincide) return false;
            }

            // Filtro por categoría
            if (categoria != null && !categoria.equals("Todos") && !categoria.isEmpty()) {
                if (producto.getNombreCategoria() == null || !producto.getNombreCategoria().equals(categoria)) {
                    return false;
                }
            }

            // Filtro por estado
            if (estado != null && !estado.equals("Todos")) {
                if (estado.equals("Activos") && !producto.isEstado()) return false;
                if (estado.equals("Inactivos") && producto.isEstado()) return false;
            }

            return true;
        });

        actualizarContadores();
    }

    private void cargarCategorias() {
        ObservableList<String> categorias = FXCollections.observableArrayList();
        categorias.add("Todos");
        String sql = "SELECT DISTINCT nombre FROM tbl_CATEGORIA_PRODUCTO WHERE estado = 1 ORDER BY nombre";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }
            cmbCategoria.setItems(categorias);
            cmbCategoria.setValue("Todos");
        } catch (SQLException e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        listaProductos.clear();

        String sql = "SELECT p.id_producto, p.id_categoria, p.nombre, p.precio_venta, " +
                "p.stock_actual, p.estado, " +
                "c.nombre as nombre_categoria " +
                "FROM tbl_PRODUCTO p " +
                "LEFT JOIN tbl_CATEGORIA_PRODUCTO c ON p.id_categoria = c.id_categoria " +
                "ORDER BY p.nombre";

        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setIdCategoria(rs.getInt("id_categoria"));
                prod.setNombre(rs.getString("nombre"));
                prod.setPrecioVenta(rs.getDouble("precio_venta"));
                prod.setStockActual(rs.getInt("stock_actual"));
                prod.setEstado(rs.getBoolean("estado"));
                prod.setNombreCategoria(rs.getString("nombre_categoria"));

                listaProductos.add(prod);
            }

            filteredList = new FilteredList<>(listaProductos, p -> true);
            tblProductos.setItems(filteredList);
            actualizarContadores();

            System.out.println("✅ Productos cargados: " + listaProductos.size());

        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarContadores() {
        if (filteredList == null) return;

        int total = filteredList.size();
        int stockTotal = 0;
        int activos = 0;

        for (Producto p : filteredList) {
            stockTotal += p.getStockActual();
            if (p.isEstado()) activos++;
        }

        lblTotalRegistros.setText("Total: " + total + " productos");
        lblStockTotal.setText("📦 Stock total: " + String.format("%,d", stockTotal) + " unidades");
        lblProductosActivos.setText("✅ Productos activos: " + activos);
    }

    @FXML
    private void buscarProducto() {
        aplicarFiltros();
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cmbCategoria.setValue("Todos");
        cmbEstado.setValue("Todos");
        aplicarFiltros();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}