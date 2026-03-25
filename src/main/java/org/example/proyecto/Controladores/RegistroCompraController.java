package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Compra;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroCompraController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private ComboBox<String> cmbComprobante;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtNroFacturaProv;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtItbis;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, Integer> colId;
    @FXML private TableColumn<Compra, Integer> colProveedor;
    @FXML private TableColumn<Compra, Integer> colEmpleado;
    @FXML private TableColumn<Compra, Integer> colComprobante;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colNroFactura;
    @FXML private TableColumn<Compra, BigDecimal> colSubtotal;
    @FXML private TableColumn<Compra, BigDecimal> colDescuento;
    @FXML private TableColumn<Compra, BigDecimal> colItbis;
    @FXML private TableColumn<Compra, BigDecimal> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;



    private int idCompraSeleccionada = 0;
    private final ObservableList<Compra> listaCompras = FXCollections.observableArrayList();
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            // Configurar ComboBoxes
            cmbEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE", "RECIBIDA", "ANULADA"
            ));

            // Cargar proveedores (ajusta los nombres de columnas según tu BD)
            cargarProveedores();


            // Cargar empleados (ajusta los nombres de columnas según tu BD)
            cargarEmpleados();

            // Comprobante opcional - si no existe la tabla, omitir
            cargarComprobantes();

            dateFecha.setValue(LocalDate.now());

            configurarTabla();
            cargarTabla();

            // Listener para selección en tabla
            tblCompras.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, sel) -> {
                        if (sel != null) {
                            idCompraSeleccionada = sel.getIdCompra();
                            rellenarFormulario(sel);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al inicializar: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        ObservableList<String> proveedores = FXCollections.observableArrayList();
        String sql = "SELECT id_proveedor, razon_social FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY razon_social";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                proveedores.add(rs.getInt("id_proveedor") + " - " + rs.getString("razon_social"));
            }
            cmbProveedor.setItems(proveedores);
        } catch (SQLException e) {

            try {
                String sql2 = "SELECT id_proveedor, nombre_comercial FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY nombre_comercial";
                try (Statement stmt2 = conexion.createStatement();
                     ResultSet rs2 = stmt2.executeQuery(sql2)) {
                    while (rs2.next()) {
                        proveedores.add(rs2.getInt("id_proveedor") + " - " + rs2.getString("nombre_comercial"));
                    }
                    cmbProveedor.setItems(proveedores);
                }
            } catch (SQLException e2) {
                try {
                    String sql3 = "SELECT id_proveedor FROM tbl_PROVEEDOR WHERE estado_temp = 'Activo' ORDER BY id_proveedor";
                    try (Statement stmt3 = conexion.createStatement();
                         ResultSet rs3 = stmt3.executeQuery(sql3)) {
                        while (rs3.next()) {
                            proveedores.add(String.valueOf(rs3.getInt("id_proveedor")));
                        }
                        cmbProveedor.setItems(proveedores);
                    }
                } catch (SQLException e3) {
                    mostrarError("Error al cargar proveedores: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarEmpleados() {
        ObservableList<String> empleados = FXCollections.observableArrayList();

        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO WHERE estado_temp = 'Activo' ORDER BY nombres";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            }
            cmbEmpleado.setItems(empleados);
        } catch (SQLException e) {

            try {
                String sql2 = "SELECT id_empleado, nombres FROM tbl_EMPLEADO WHERE estado_temp = 'Activo' ORDER BY nombres";
                try (Statement stmt2 = conexion.createStatement();
                     ResultSet rs2 = stmt2.executeQuery(sql2)) {
                    while (rs2.next()) {
                        empleados.add(rs2.getInt("id_empleado") + " - " + rs2.getString("nombres"));
                    }
                    cmbEmpleado.setItems(empleados);
                }
            } catch (SQLException e2) {
                try {
                    String sql3 = "SELECT id_empleado FROM tbl_EMPLEADO WHERE estado_temp = 'Activo' ORDER BY id_empleado";
                    try (Statement stmt3 = conexion.createStatement();
                         ResultSet rs3 = stmt3.executeQuery(sql3)) {
                        while (rs3.next()) {
                            empleados.add(String.valueOf(rs3.getInt("id_empleado")));
                        }
                        cmbEmpleado.setItems(empleados);
                    }
                } catch (SQLException e3) {
                    mostrarError("Error al cargar empleados: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    private void cargarComprobantes() {
        ObservableList<String> comprobantes = FXCollections.observableArrayList();

        // Agregar opción "NINGUNO" por defecto
        comprobantes.add("NINGUNO");

        try {
            // Primero verificar si la tabla existe
            String sql = "SELECT id_comprobante, ncf FROM tbl_COMPROBANTE_FISCAL";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                boolean hayDatos = false;
                while (rs.next()) {
                    int id = rs.getInt("id_comprobante");
                    String nfc = rs.getString("ncf");
                    if (nfc != null && !nfc.trim().isEmpty()) {
                        comprobantes.add(id + " - " + nfc);
                    } else {
                        comprobantes.add(String.valueOf(id));
                    }
                    hayDatos = true;
                }

                if (hayDatos) {
                    System.out.println("Comprobantes cargados correctamente: " + (comprobantes.size() - 1) + " registros");
                } else {
                    System.out.println("La tabla tbl_COMPROBANTE_FISCAL está vacía");
                }

            } catch (SQLException e) {
                System.out.println("Error al consultar tbl_COMPROBANTE_FISCAL: " + e.getMessage());

                // Intentar con otro nombre de tabla
                try {
                    String sql2 = "SELECT id_comprobante_FISCAL, ncf FROM tbl_COMPROBANTE WHERE estado = 'EMITIDO'";
                    try (Statement stmt2 = conexion.createStatement();
                         ResultSet rs2 = stmt2.executeQuery(sql2)) {
                        while (rs2.next()) {
                            comprobantes.add(rs2.getInt("id_comprobante") + " - " + rs2.getString("ncf"));
                        }
                        System.out.println("Comprobantes cargados desde tbl_COMPROBANTE");
                    }
                } catch (SQLException e2) {
                    System.out.println("Tabla tbl_COMPROBANTE tampoco existe: " + e2.getMessage());
                }
            }

            cmbComprobante.setItems(comprobantes);
            cmbComprobante.setValue("NINGUNO");

        } catch (Exception e) {
            System.out.println("Error general al cargar comprobantes: " + e.getMessage());
            cmbComprobante.setItems(comprobantes);
            cmbComprobante.setValue("NINGUNO");
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colComprobante.setCellValueFactory(new PropertyValueFactory<>("idComprobante"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colNroFactura.setCellValueFactory(new PropertyValueFactory<>("nroFacturaProv"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colItbis.setCellValueFactory(new PropertyValueFactory<>("itbis"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblCompras.setItems(listaCompras);
    }

    private void cargarTabla() {
        listaCompras.clear();
        String sql = "SELECT * FROM tbl_COMPRA ORDER BY id_compra DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setIdCompra(rs.getInt("id_compra"));
                compra.setIdProveedor(rs.getInt("id_proveedor"));
                compra.setIdEmpleado(rs.getInt("id_empleado"));

                // Manejar valores null
                Object idComprobanteObj = rs.getObject("id_comprobante");
                if (idComprobanteObj != null) {
                    compra.setIdComprobante((Integer) idComprobanteObj);
                }

                Timestamp fechaTs = rs.getTimestamp("fecha");
                if (fechaTs != null) {
                    compra.setFecha(fechaTs.toLocalDateTime());
                }

                compra.setNroFacturaProv(rs.getString("nro_factura_prov"));
                compra.setSubtotal(rs.getBigDecimal("subtotal"));
                compra.setDescuento(rs.getBigDecimal("descuento"));
                compra.setItbis(rs.getBigDecimal("itbis"));
                compra.setTotal(rs.getBigDecimal("total"));
                compra.setEstado(rs.getString("estado"));

                listaCompras.add(compra);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar las compras:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rellenarFormulario(Compra c) {
        // Rellenar proveedor
        String proveedorStr = c.getIdProveedor() + " - ";
        boolean found = false;
        for (String p : cmbProveedor.getItems()) {
            if (p.startsWith(proveedorStr)) {
                cmbProveedor.setValue(p);
                found = true;
                break;
            }
        }
        if (!found && cmbProveedor.getItems().size() > 0) {
            // Si no encuentra el formato con nombre, intentar solo con ID
            String idStr = String.valueOf(c.getIdProveedor());
            for (String p : cmbProveedor.getItems()) {
                if (p.equals(idStr)) {
                    cmbProveedor.setValue(p);
                    break;
                }
            }
        }

        // Rellenar empleado
        String empleadoStr = c.getIdEmpleado() + " - ";
        found = false;
        for (String e : cmbEmpleado.getItems()) {
            if (e.startsWith(empleadoStr)) {
                cmbEmpleado.setValue(e);
                found = true;
                break;
            }
        }
        if (!found) {
            String idStr = String.valueOf(c.getIdEmpleado());
            for (String e : cmbEmpleado.getItems()) {
                if (e.equals(idStr)) {
                    cmbEmpleado.setValue(e);
                    break;
                }
            }
        }

        // Rellenar comprobante
        if (c.getIdComprobante() != null && c.getIdComprobante() > 0) {
            String comprobanteStr = c.getIdComprobante() + " - ";
            found = false;
            for (String comp : cmbComprobante.getItems()) {
                if (comp.startsWith(comprobanteStr)) {
                    cmbComprobante.setValue(comp);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cmbComprobante.setValue("NINGUNO");
            }
        } else {
            cmbComprobante.setValue("NINGUNO");
        }

        dateFecha.setValue(c.getFecha() != null ? c.getFecha().toLocalDate() : LocalDate.now());
        txtNroFacturaProv.setText(c.getNroFacturaProv() != null ? c.getNroFacturaProv() : "");
        txtSubtotal.setText(c.getSubtotal() != null ? c.getSubtotal().toPlainString() : "");
        txtDescuento.setText(c.getDescuento() != null ? c.getDescuento().toPlainString() : "");
        txtItbis.setText(c.getItbis() != null ? c.getItbis().toPlainString() : "");
        txtTotal.setText(c.getTotal() != null ? c.getTotal().toPlainString() : "");
        cmbEstado.setValue(c.getEstado());
    }

    @FXML
    private void guardarCompra(ActionEvent event) {
        if (idCompraSeleccionada == 0) {
            NuevaCompra();
        } else {
            actualizarCompra();
        }
    }

    private Integer obtenerIdFromCombo(String comboValue) {
        if (comboValue == null || comboValue.equals("NINGUNO")) return null;
        try {
            // Si tiene formato "ID - Nombre"
            if (comboValue.contains(" - ")) {
                return Integer.parseInt(comboValue.split(" - ")[0]);
            }
            // Si es solo ID
            return Integer.parseInt(comboValue);
        } catch (Exception e) {
            return null;
        }
    }


    private void actualizarCompra() {
        if (!validar()) return;

        Integer idProveedor = obtenerIdFromCombo(cmbProveedor.getValue());
        Integer idEmpleado = obtenerIdFromCombo(cmbEmpleado.getValue());
        Integer idComprobante = obtenerIdFromCombo(cmbComprobante.getValue());

        String sql = "UPDATE tbl_COMPRA SET " +
                "id_proveedor = ?, id_empleado = ?, id_comprobante = ?, fecha = ?, " +
                "nro_factura_prov = ?, subtotal = ?, descuento = ?, itbis = ?, total = ?, estado = ? " +
                "WHERE id_compra = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idEmpleado);

            if (idComprobante != null && idComprobante > 0) {
                ps.setInt(3, idComprobante);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setString(5, txtNroFacturaProv.getText().trim().isEmpty() ? null : txtNroFacturaProv.getText().trim());
            ps.setBigDecimal(6, new BigDecimal(txtSubtotal.getText().trim()));
            ps.setBigDecimal(7, new BigDecimal(txtDescuento.getText().trim()));
            ps.setBigDecimal(8, new BigDecimal(txtItbis.getText().trim()));
            ps.setBigDecimal(9, new BigDecimal(txtTotal.getText().trim()));
            ps.setString(10, cmbEstado.getValue());
            ps.setInt(11, idCompraSeleccionada);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                mostrarExito("Compra actualizada correctamente.");
                limpiarCampos();
                cargarTabla();
            } else {
                mostrarError("No se encontró la compra con ID: " + idCompraSeleccionada);
            }

        } catch (SQLException e) {
            mostrarError("Error al actualizar:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarCompra(ActionEvent event) {
        if (idCompraSeleccionada == 0) {
            mostrarError("Seleccione una compra de la tabla");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro?");
        conf.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = conf.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "DELETE FROM tbl_COMPRA WHERE id_compra = ?")) {
                ps.setInt(1, idCompraSeleccionada);
                ps.executeUpdate();

                mostrarExito("Compra eliminada correctamente");
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
        cmbProveedor.setValue(null);
        cmbEmpleado.setValue(null);
        cmbComprobante.setValue("NINGUNO");
        dateFecha.setValue(LocalDate.now());
        txtNroFacturaProv.clear();
        txtSubtotal.clear();
        txtDescuento.clear();
        txtItbis.clear();
        txtTotal.clear();
        cmbEstado.setValue(null);
        idCompraSeleccionada = 0;
        tblCompras.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarCompra(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarTabla();
            return;
        }

        ObservableList<Compra> filtrados = FXCollections.observableArrayList();
        for (Compra c : listaCompras) {
            if (String.valueOf(c.getIdCompra()).contains(busqueda) ||
                    String.valueOf(c.getIdProveedor()).contains(busqueda) ||
                    (c.getNroFacturaProv() != null && c.getNroFacturaProv().toLowerCase().contains(busqueda))) {
                filtrados.add(c);
            }
        }
        tblCompras.setItems(filtrados);
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        cargarTabla();
        txtBuscar.clear();
    }

    private boolean validar() {
        if (cmbProveedor.getValue() == null) {
            mostrarError("Seleccione un proveedor.");
            cmbProveedor.requestFocus();
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
        if (texto == null || texto.trim().isEmpty()) return false;
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

    public void NuevaCompra() {
        if (!validar()) return;

        Integer idProveedor = obtenerIdFromCombo(cmbProveedor.getValue());
        Integer idEmpleado = obtenerIdFromCombo(cmbEmpleado.getValue());
        Integer idComprobante = obtenerIdFromCombo(cmbComprobante.getValue());

        String sql = "INSERT INTO tbl_COMPRA (id_proveedor, id_empleado, id_comprobante, fecha, " +
                "nro_factura_prov, subtotal, descuento, itbis, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idEmpleado);

            if (idComprobante != null && idComprobante > 0) {
                ps.setInt(3, idComprobante);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateFecha.getValue(), LocalTime.now())));
            ps.setString(5, txtNroFacturaProv.getText().trim().isEmpty() ? null : txtNroFacturaProv.getText().trim());
            ps.setBigDecimal(6, new BigDecimal(txtSubtotal.getText().trim()));
            ps.setBigDecimal(7, new BigDecimal(txtDescuento.getText().trim()));
            ps.setBigDecimal(8, new BigDecimal(txtItbis.getText().trim()));
            ps.setBigDecimal(9, new BigDecimal(txtTotal.getText().trim()));
            ps.setString(10, cmbEstado.getValue());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int idGenerado = keys.next() ? keys.getInt(1) : -1;

            mostrarExito("Compra registrada correctamente.\nID generado: " + idGenerado);
            limpiarCampos();
            cargarTabla();

        } catch (SQLException e) {
            mostrarError("Error al registrar la compra:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}