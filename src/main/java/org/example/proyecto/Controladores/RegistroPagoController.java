package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Pago;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroPagoController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;

    // Botones de acción
    @FXML private Button btnConsultar;
    @FXML private Button btnCancelar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnRegistrarPago;

    // Campos del formulario
    @FXML private TextField txtIdCuentaPago;
    @FXML private TextField txtReferencia;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dateFecha;
    @FXML private ToggleButton tglEstado;

    // Tabla
    @FXML private TableView<Pago> tblPagos;
    @FXML private TableColumn<Pago, Integer> colId;
    @FXML private TableColumn<Pago, Integer> colIdCuenta;
    @FXML private TableColumn<Pago, String> colReferencia;
    @FXML private TableColumn<Pago, LocalDateTime> colFecha;
    @FXML private TableColumn<Pago, BigDecimal> colMonto;
    @FXML private TableColumn<Pago, Boolean> colEstado;

    // Labels
    @FXML private Label lblTotalPagar;
    @FXML private Label lblIdPagoActual;
    @FXML private Label lblEstadoHint;

    private final ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Connection conexion;
    private int idPagoSeleccionado = 0;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();
        configurarTabla();
        configurarSeleccionTabla();
        configurarBotonesPorRol();
        dateFecha.setValue(LocalDate.now());

        // Inicialmente el panel de consulta está oculto
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);

        // Botones de edición deshabilitados al inicio
        habilitarBotonesEdicion(false);
        btnRegistrarPago.setDisable(false);
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();

        boolean puedeEditar = false;
        boolean puedeEliminar = false;

        switch (rol) {
            case "Administrador":
                puedeEditar = true;
                puedeEliminar = true;
                break;
            case "Cajero":
                puedeEditar = false;
                puedeEliminar = false;
                break;
            default:
                puedeEditar = false;
                puedeEliminar = false;
                break;
        }

        btnEditar.setVisible(puedeEditar);
        btnEditar.setManaged(puedeEditar);
        btnEliminar.setVisible(puedeEliminar);
        btnEliminar.setManaged(puedeEliminar);
    }

    private void habilitarBotonesEdicion(boolean habilitar) {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();

        if (!"Administrador".equals(rol)) {
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
            return;
        }

        btnEditar.setDisable(!habilitar);
        btnEliminar.setDisable(!habilitar);
    }

    @FXML
    private void abrirConsulta() {
        consultaPanel.setVisible(true);
        consultaPanel.setManaged(true);
        cargarPagos();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colIdCuenta.setCellValueFactory(new PropertyValueFactory<>("idCuentaPago"));
        colReferencia.setCellValueFactory(new PropertyValueFactory<>("referencia"));

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(FMT));
            }
        });

        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMonto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "RD$ " + String.format("%.2f", item));
            }
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else if (item) {
                    setText("Activo");
                    setStyle("-fx-text-fill: #1A7A40; -fx-font-weight: bold;");
                } else {
                    setText("Inactivo");
                    setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void configurarSeleccionTabla() {
        tblPagos.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, nuevo) -> {
                    if (nuevo != null) {
                        cargarEnFormulario(nuevo);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnRegistrarPago.setDisable(true);
                    }
                });
    }

    private void cargarPagos() {
        listaPagos.clear();
        String sql = "SELECT * FROM tbl_PAGO ORDER BY id_pago DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) listaPagos.add(mapearPago(rs));
            tblPagos.setItems(listaPagos);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar pagos:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Pago mapearPago(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_pago");
        int idCuenta = rs.getInt("id_cuenta_pago");
        BigDecimal monto = rs.getBigDecimal("monto");
        String referencia = rs.getString("referencia");
        boolean estado = rs.getBoolean("estado");
        LocalDateTime fecha = null;
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) fecha = ts.toLocalDateTime();
        return new Pago(id, idCuenta, monto, fecha, referencia, estado);
    }

    private void cargarEnFormulario(Pago p) {
        idPagoSeleccionado = p.getIdPago();
        txtIdCuentaPago.setText(String.valueOf(p.getIdCuentaPago()));
        txtReferencia.setText(p.getReferencia());
        txtMonto.setText(p.getMonto() != null ? p.getMonto().toPlainString() : "");
        if (p.getFecha() != null) dateFecha.setValue(p.getFecha().toLocalDate());
        boolean activo = p.isEstado();
        tglEstado.setSelected(activo);
        actualizarToggleEstilo(activo);
        actualizarTotal(p.getMonto());
        lblIdPagoActual.setText("ID: " + p.getIdPago());
    }

    @FXML
    private void buscarPago() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese un término de búsqueda.", Alert.AlertType.WARNING);
            return;
        }

        listaPagos.clear();
        String sql = "SELECT * FROM tbl_PAGO WHERE referencia LIKE ? OR CAST(id_cuenta_pago AS CHAR) LIKE ? ORDER BY id_pago DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String param = "%" + busqueda + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) listaPagos.add(mapearPago(rs));
            tblPagos.setItems(listaPagos);

            if (listaPagos.isEmpty())
                mostrarAlerta("Sin resultados", "No se encontraron pagos para: " + busqueda, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarPagos();
    }

    @FXML
    private void registrarPago() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para registrar pagos", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;
        BigDecimal monto = new BigDecimal(txtMonto.getText().trim());

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar Pago");
        conf.setHeaderText("¿Desea registrar este pago?");
        conf.setContentText("Monto: RD$ " + String.format("%.2f", monto));
        Optional<ButtonType> res = conf.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) guardarPago();
    }

    @FXML
    private void editarPago() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar pagos", Alert.AlertType.ERROR);
            return;
        }

        if (idPagoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un pago de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setHeaderText("¿Desea guardar los cambios en el pago ID: " + idPagoSeleccionado + "?");
        conf.setContentText("Monto: RD$ " + String.format("%.2f", new BigDecimal(txtMonto.getText().trim())));
        Optional<ButtonType> res = conf.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) actualizarPago();
    }

    @FXML
    private void eliminarPago() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar pagos", Alert.AlertType.ERROR);
            return;
        }

        if (idPagoSeleccionado == 0) {
            mostrarAlerta("Advertencia", "Seleccione un pago de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText("¿Está seguro que desea eliminar el pago ID: " + idPagoSeleccionado + "?");
        conf.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_PAGO WHERE id_pago = ?")) {
                ps.setInt(1, idPagoSeleccionado);
                if (ps.executeUpdate() > 0) {
                    mostrarAlerta("✔ Éxito", "Pago eliminado correctamente.", Alert.AlertType.INFORMATION);
                    limpiarCamposInterno();
                    if (consultaPanel.isVisible()) {
                        cargarPagos();
                    }
                }
            } catch (SQLException e) {
                mostrarAlerta("Error de BD", "No se pudo eliminar el pago:\n" + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void cancelar() {
        limpiarCamposInterno();
    }

    @FXML
    private void calcularTotal() {
        try {
            String texto = txtMonto.getText().trim();
            if (!texto.isEmpty()) actualizarTotal(new BigDecimal(texto));
        } catch (NumberFormatException e) {
            actualizarTotal(BigDecimal.ZERO);
        }
    }

    @FXML
    private void toggleEstado() {
        actualizarToggleEstilo(tglEstado.isSelected());
    }

    private void guardarPago() {
        String sql = "INSERT INTO tbl_PAGO (id_cuenta_pago, monto, fecha, referencia, estado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            LocalDateTime fechaHora = LocalDateTime.of(dateFecha.getValue(), LocalTime.now());
            ps.setInt(1, Integer.parseInt(txtIdCuentaPago.getText().trim()));
            ps.setBigDecimal(2, new BigDecimal(txtMonto.getText().trim()));
            ps.setTimestamp(3, Timestamp.valueOf(fechaHora));
            ps.setString(4, txtReferencia.getText().trim());
            ps.setBoolean(5, tglEstado.isSelected());

            if (ps.executeUpdate() > 0) {
                mostrarAlerta("✔ Éxito", "Pago registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarPagos();
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "No se pudo registrar el pago:\n" + e.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "Revise los campos numéricos.", Alert.AlertType.ERROR);
        }
    }

    private void actualizarPago() {
        String sql = "UPDATE tbl_PAGO SET id_cuenta_pago = ?, monto = ?, fecha = ?, referencia = ?, estado = ? WHERE id_pago = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            LocalDateTime fechaHora = LocalDateTime.of(dateFecha.getValue(), LocalTime.now());
            ps.setInt(1, Integer.parseInt(txtIdCuentaPago.getText().trim()));
            ps.setBigDecimal(2, new BigDecimal(txtMonto.getText().trim()));
            ps.setTimestamp(3, Timestamp.valueOf(fechaHora));
            ps.setString(4, txtReferencia.getText().trim());
            ps.setBoolean(5, tglEstado.isSelected());
            ps.setInt(6, idPagoSeleccionado);

            if (ps.executeUpdate() > 0) {
                mostrarAlerta("✔ Éxito", "Pago actualizado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCamposInterno();
                if (consultaPanel.isVisible()) {
                    cargarPagos();
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "No se pudo actualizar el pago:\n" + e.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "Revise los campos numéricos.", Alert.AlertType.ERROR);
        }
    }

    private void actualizarTotal(BigDecimal monto) {
        if (monto == null) monto = BigDecimal.ZERO;
        if (lblTotalPagar != null)
            lblTotalPagar.setText("RD$ " + String.format("%.2f", monto));
    }

    private void actualizarToggleEstilo(boolean activo) {
        if (activo) {
            tglEstado.setText("✔ Activo (true)");
            tglEstado.setStyle("-fx-background-color: #DFF5E8; -fx-text-fill: #1A7A40; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 6 18;");
            if (lblEstadoHint != null) {
                lblEstadoHint.setText("El pago se registrará como Activo");
                lblEstadoHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #5DA87A;");
            }
        } else {
            tglEstado.setText("✕ Inactivo (false)");
            tglEstado.setStyle("-fx-background-color: #FDE8E8; -fx-text-fill: #C0392B; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 6 18;");
            if (lblEstadoHint != null) {
                lblEstadoHint.setText("El pago se registrará como Inactivo");
                lblEstadoHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #C0392B;");
            }
        }
    }

    private void limpiarCamposInterno() {
        idPagoSeleccionado = 0;
        txtIdCuentaPago.clear();
        txtReferencia.clear();
        txtMonto.clear();
        dateFecha.setValue(LocalDate.now());
        tglEstado.setSelected(true);
        actualizarToggleEstilo(true);
        actualizarTotal(BigDecimal.ZERO);
        lblIdPagoActual.setText("— Nuevo —");
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnRegistrarPago.setDisable(false);
        tblPagos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtIdCuentaPago.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "Ingrese el ID de la cuenta de pago.", Alert.AlertType.WARNING);
            txtIdCuentaPago.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(txtIdCuentaPago.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "El ID de cuenta debe ser un número entero.", Alert.AlertType.ERROR);
            txtIdCuentaPago.requestFocus();
            return false;
        }
        if (dateFecha.getValue() == null) {
            mostrarAlerta("Campo requerido", "Seleccione la fecha del pago.", Alert.AlertType.WARNING);
            dateFecha.requestFocus();
            return false;
        }
        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "Ingrese el monto del pago.", Alert.AlertType.WARNING);
            txtMonto.requestFocus();
            return false;
        }
        try {
            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("Valor inválido", "El monto debe ser mayor a RD$ 0.00.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "El monto debe ser un número válido (ej: 1500.00).", Alert.AlertType.ERROR);
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