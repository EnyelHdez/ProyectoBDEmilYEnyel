package org.example.proyecto.Controladores;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.SistemaFidelizacion;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistroFidelizacionController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;

    // Botones de acción
    @FXML private Button btnConsultar;
    @FXML private Button btnNuevoPrograma;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;

    // Botones de gestión de puntos
    @FXML private Button btnBuscarCliente;
    @FXML private Button btnRegistrarPuntos;
    @FXML private Button btnRenovarCaducidad;
    @FXML private Button btnCanjearPuntos;

    // Contadores
    @FXML private Label lblContActivo;
    @FXML private Label lblContVencido;
    @FXML private Label lblTotalPuntos;

    // Tabla de programas
    @FXML private TableView<SistemaFidelizacion> tablaFidelizacion;
    @FXML private TableColumn<SistemaFidelizacion, Integer> colId;
    @FXML private TableColumn<SistemaFidelizacion, String> colNombre;
    @FXML private TableColumn<SistemaFidelizacion, BigDecimal> colPuntosPorPeso;
    @FXML private TableColumn<SistemaFidelizacion, BigDecimal> colValorPunto;
    @FXML private TableColumn<SistemaFidelizacion, Integer> colMinimoCanje;
    @FXML private TableColumn<SistemaFidelizacion, String> colDescripcion;
    @FXML private TableColumn<SistemaFidelizacion, String> colEstado;

    // Formulario de programa
    @FXML private TextField txtIdFidelizacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPuntosPorPeso;
    @FXML private TextField txtValorPunto;
    @FXML private TextField txtMinimoCanje;
    @FXML private TextArea txtDescripcion;
    @FXML private CheckBox chkEstado;

    // Gestión de puntos por cliente
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtPuntosAcumulados;
    @FXML private DatePicker dpFechaCaducidad;
    @FXML private Label lblPuntosDisponibles;

    // Canje de puntos
    @FXML private TextField txtPuntosACanjear;
    @FXML private Label lblPuntosRestantes;

    // Detalle del cliente
    @FXML private ListView<String> listHistorial;

    private Connection conexion;
    private ObservableList<SistemaFidelizacion> listaProgramas = FXCollections.observableArrayList();
    private SistemaFidelizacion programaSeleccionado = null;
    private int idClienteActual = 0;
    private int puntosDisponiblesCliente = 0;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = new ConexionBD().EstablecerConexion();

        configurarTabla();
        configurarFiltros();
        cargarProgramas();
        configurarSeleccionTabla();
        configurarBotonesPorRol();
        actualizarContadores();

        dpFechaCaducidad.setValue(LocalDate.now().plusMonths(6));

        // Inicialmente el panel de consulta está oculto
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);

        // Botones de edición deshabilitados al inicio
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();

        boolean puedeEditar = false;
        boolean puedeEliminar = false;
        boolean puedeGestionarPuntos = false;

        switch (rol) {
            case "Administrador":
                puedeEditar = true;
                puedeEliminar = true;
                puedeGestionarPuntos = true;
                break;
            case "Cajero":
                puedeEditar = false;
                puedeEliminar = false;
                puedeGestionarPuntos = true;
                break;
            default:
                puedeEditar = false;
                puedeEliminar = false;
                puedeGestionarPuntos = false;
                break;
        }

        btnEditar.setVisible(puedeEditar);
        btnEditar.setManaged(puedeEditar);
        btnEliminar.setVisible(puedeEliminar);
        btnEliminar.setManaged(puedeEliminar);

        // Botones de gestión de puntos
        btnRegistrarPuntos.setDisable(!puedeGestionarPuntos);
        btnRenovarCaducidad.setDisable(!puedeGestionarPuntos);
        btnCanjearPuntos.setDisable(!puedeGestionarPuntos);
        btnBuscarCliente.setDisable(!puedeGestionarPuntos);
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
        cargarProgramas();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idFidelizacion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuntosPorPeso.setCellValueFactory(new PropertyValueFactory<>("puntosPorPeso"));
        colValorPunto.setCellValueFactory(new PropertyValueFactory<>("valorPunto"));
        colMinimoCanje.setCellValueFactory(new PropertyValueFactory<>("minimoCanje"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isEstado() ? "Activo" : "Inactivo"));

        tablaFidelizacion.setItems(listaProgramas);
    }

    private void configurarFiltros() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbFiltroEstado.setValue("Todos");
    }

    private void configurarSeleccionTabla() {
        tablaFidelizacion.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                programaSeleccionado = sel;
                cargarProgramaEnFormulario(sel);
                habilitarBotonesEdicion(true);
                modoEdicion = true;
                btnGuardar.setDisable(true);
            }
        });
    }

    private void cargarProgramas() {
        listaProgramas.clear();
        String sql = "SELECT * FROM tbl_SISTEMA_FIDELIZACION ORDER BY id_fidelizacion DESC";

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                listaProgramas.add(mapearPrograma(rs));
            }
            tablaFidelizacion.setItems(listaProgramas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar programas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private SistemaFidelizacion mapearPrograma(ResultSet rs) throws SQLException {
        return new SistemaFidelizacion(
                rs.getInt("id_fidelizacion"),
                rs.getString("nombre"),
                rs.getBigDecimal("puntos_por_peso"),
                rs.getBigDecimal("valor_punto"),
                rs.getInt("minimo_canje"),
                rs.getString("descripcion"),
                rs.getBoolean("estado")
        );
    }

    private void cargarProgramaEnFormulario(SistemaFidelizacion p) {
        txtIdFidelizacion.setText(String.valueOf(p.getIdFidelizacion()));
        txtNombre.setText(p.getNombre());
        txtPuntosPorPeso.setText(p.getPuntosPorPeso() != null ? p.getPuntosPorPeso().toString() : "");
        txtValorPunto.setText(p.getValorPunto() != null ? p.getValorPunto().toString() : "");
        txtMinimoCanje.setText(String.valueOf(p.getMinimoCanje()));
        txtDescripcion.setText(p.getDescripcion());
        chkEstado.setSelected(p.isEstado());
    }

    @FXML
    private void buscarFidelizacion() {
        String busqueda = txtBusqueda.getText().trim();
        String filtroEstado = cmbFiltroEstado.getValue();

        listaProgramas.clear();
        StringBuilder sql = new StringBuilder("SELECT * FROM tbl_SISTEMA_FIDELIZACION WHERE 1=1");

        if (!busqueda.isEmpty()) {
            sql.append(" AND (CAST(id_fidelizacion AS CHAR) LIKE '%").append(busqueda).append("%'")
                    .append(" OR nombre LIKE '%").append(busqueda).append("%')");
        }

        if ("Activos".equals(filtroEstado)) {
            sql.append(" AND estado = 1");
        } else if ("Inactivos".equals(filtroEstado)) {
            sql.append(" AND estado = 0");
        }

        sql.append(" ORDER BY id_fidelizacion DESC");

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {
            while (rs.next()) {
                listaProgramas.add(mapearPrograma(rs));
            }
            tablaFidelizacion.setItems(listaProgramas);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBusqueda.clear();
        cmbFiltroEstado.setValue("Todos");
        cargarProgramas();
    }

    @FXML
    private void nuevoPrograma() {
        limpiarFormularioPrograma();
        txtNombre.requestFocus();
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    @FXML
    private void guardarPrograma() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar programas", Alert.AlertType.ERROR);
            return;
        }

        if (!validarFormularioPrograma()) return;

        if (modoEdicion && programaSeleccionado != null) {
            // Editar programa existente
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Confirmar edición");
            conf.setHeaderText(null);
            conf.setContentText("¿Guardar cambios en este programa?");
            if (conf.showAndWait().get() != ButtonType.OK) return;

            String sql = "UPDATE tbl_SISTEMA_FIDELIZACION SET nombre=?, puntos_por_peso=?, " +
                    "valor_punto=?, minimo_canje=?, descripcion=?, estado=? WHERE id_fidelizacion=?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                setearParametrosPrograma(ps);
                ps.setInt(7, programaSeleccionado.getIdFidelizacion());
                ps.executeUpdate();
                mostrarAlerta("Éxito", "Programa actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormularioPrograma();
                if (consultaPanel.isVisible()) {
                    cargarProgramas();
                }
                actualizarContadores();
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Crear nuevo programa
            String sql = "INSERT INTO tbl_SISTEMA_FIDELIZACION " +
                    "(nombre, puntos_por_peso, valor_punto, minimo_canje, descripcion, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setearParametrosPrograma(ps);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    mostrarAlerta("Éxito", "Programa guardado correctamente (ID: " + rs.getInt(1) + ")",
                            Alert.AlertType.INFORMATION);
                }
                limpiarFormularioPrograma();
                if (consultaPanel.isVisible()) {
                    cargarProgramas();
                }
                actualizarContadores();
            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        modoEdicion = false;
        btnGuardar.setDisable(false);
    }

    @FXML
    private void editarPrograma() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar programas", Alert.AlertType.ERROR);
            return;
        }

        if (programaSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un programa para editar", Alert.AlertType.WARNING);
            return;
        }
        guardarPrograma();
    }

    @FXML
    private void eliminarPrograma() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar programas", Alert.AlertType.ERROR);
            return;
        }

        if (programaSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un programa para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Eliminar este programa? Esta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_SISTEMA_FIDELIZACION WHERE id_fidelizacion=?")) {
            ps.setInt(1, programaSeleccionado.getIdFidelizacion());
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Programa eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormularioPrograma();
            if (consultaPanel.isVisible()) {
                cargarProgramas();
            }
            actualizarContadores();
            habilitarBotonesEdicion(false);
            btnGuardar.setDisable(false);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: el programa tiene registros asociados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarCampos() {
        limpiarFormularioPrograma();
        limpiarFormularioCliente();
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
    }

    private void limpiarFormularioPrograma() {
        txtIdFidelizacion.clear();
        txtNombre.clear();
        txtPuntosPorPeso.clear();
        txtValorPunto.clear();
        txtMinimoCanje.clear();
        txtDescripcion.clear();
        chkEstado.setSelected(true);
        programaSeleccionado = null;
        tablaFidelizacion.getSelectionModel().clearSelection();
    }

    private void limpiarFormularioCliente() {
        txtIdCliente.clear();
        txtNombreCliente.clear();
        txtPuntosAcumulados.clear();
        lblPuntosDisponibles.setText("— pts");
        txtPuntosACanjear.clear();
        lblPuntosRestantes.setText("Restantes: —");
        listHistorial.getItems().clear();
        idClienteActual = 0;
        puntosDisponiblesCliente = 0;
    }

    @FXML
    private void onBuscarCliente() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para buscar clientes", Alert.AlertType.ERROR);
            return;
        }

        String idClienteStr = txtIdCliente.getText().trim();
        if (idClienteStr.isEmpty()) {
            mostrarAlerta("Validación", "Ingrese un ID de cliente", Alert.AlertType.WARNING);
            return;
        }

        try {
            idClienteActual = Integer.parseInt(idClienteStr);

            String sql = "SELECT nombres, apellidos, puntos_fidelidad FROM tbl_CLIENTE WHERE id_cliente = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idClienteActual);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String nombre = rs.getString("nombres") + " " + rs.getString("apellidos");
                    puntosDisponiblesCliente = rs.getInt("puntos_fidelidad");

                    txtNombreCliente.setText(nombre);
                    lblPuntosDisponibles.setText(puntosDisponiblesCliente + " pts");
                    lblPuntosRestantes.setText("Restantes: " + puntosDisponiblesCliente + " pts");

                    cargarHistorialCliente();
                } else {
                    mostrarAlerta("Error", "Cliente no encontrado", Alert.AlertType.ERROR);
                    limpiarFormularioCliente();
                }
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de cliente inválido", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar cliente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarHistorialCliente() {
        listHistorial.getItems().clear();
        listHistorial.getItems().add("Cliente: " + txtNombreCliente.getText());
        listHistorial.getItems().add("Puntos disponibles: " + puntosDisponiblesCliente);
    }

    @FXML
    private void onRegistrarPuntos() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para registrar puntos", Alert.AlertType.ERROR);
            return;
        }

        if (idClienteActual == 0) {
            mostrarAlerta("Validación", "Primero busque un cliente", Alert.AlertType.WARNING);
            return;
        }

        String puntosStr = txtPuntosAcumulados.getText().trim();
        if (puntosStr.isEmpty()) {
            mostrarAlerta("Validación", "Ingrese la cantidad de puntos a agregar", Alert.AlertType.WARNING);
            return;
        }

        try {
            int puntos = Integer.parseInt(puntosStr);
            if (puntos <= 0) {
                mostrarAlerta("Validación", "Los puntos deben ser mayores a 0", Alert.AlertType.WARNING);
                return;
            }

            String sql = "UPDATE tbl_CLIENTE SET puntos_fidelidad = puntos_fidelidad + ? WHERE id_cliente = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, puntos);
                ps.setInt(2, idClienteActual);
                ps.executeUpdate();

                mostrarAlerta("Éxito", puntos + " puntos registrados correctamente", Alert.AlertType.INFORMATION);
                onBuscarCliente();
                txtPuntosAcumulados.clear();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad de puntos inválida", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al registrar puntos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onRenovarCaducidad() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para renovar caducidad", Alert.AlertType.ERROR);
            return;
        }

        if (idClienteActual == 0) {
            mostrarAlerta("Validación", "Primero busque un cliente", Alert.AlertType.WARNING);
            return;
        }

        LocalDate nuevaFecha = dpFechaCaducidad.getValue();
        if (nuevaFecha == null) {
            mostrarAlerta("Validación", "Seleccione una fecha de caducidad", Alert.AlertType.WARNING);
            return;
        }

        mostrarAlerta("Éxito", "Fecha de caducidad renovada hasta: " + nuevaFecha, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onCanjearPuntos() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador") && !rol.equals("Cajero")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para canjear puntos", Alert.AlertType.ERROR);
            return;
        }

        if (idClienteActual == 0) {
            mostrarAlerta("Validación", "Primero busque un cliente", Alert.AlertType.WARNING);
            return;
        }

        String puntosStr = txtPuntosACanjear.getText().trim();
        if (puntosStr.isEmpty()) {
            mostrarAlerta("Validación", "Ingrese los puntos a canjear", Alert.AlertType.WARNING);
            return;
        }

        try {
            int puntos = Integer.parseInt(puntosStr);
            if (puntos <= 0) {
                mostrarAlerta("Validación", "Los puntos deben ser mayores a 0", Alert.AlertType.WARNING);
                return;
            }

            if (puntos > puntosDisponiblesCliente) {
                mostrarAlerta("Validación", "Puntos insuficientes. Disponibles: " + puntosDisponiblesCliente, Alert.AlertType.WARNING);
                return;
            }

            String sql = "UPDATE tbl_CLIENTE SET puntos_fidelidad = puntos_fidelidad - ? WHERE id_cliente = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, puntos);
                ps.setInt(2, idClienteActual);
                ps.executeUpdate();

                mostrarAlerta("Éxito", puntos + " puntos canjeados correctamente", Alert.AlertType.INFORMATION);
                onBuscarCliente();
                txtPuntosACanjear.clear();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad de puntos inválida", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al canjear puntos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setearParametrosPrograma(PreparedStatement ps) throws SQLException {
        ps.setString(1, txtNombre.getText().trim());

        String puntosPorPeso = txtPuntosPorPeso.getText().trim();
        ps.setBigDecimal(2, puntosPorPeso.isEmpty() ? null : new BigDecimal(puntosPorPeso));

        String valorPunto = txtValorPunto.getText().trim();
        ps.setBigDecimal(3, valorPunto.isEmpty() ? null : new BigDecimal(valorPunto));

        String minimoCanje = txtMinimoCanje.getText().trim();
        ps.setInt(4, minimoCanje.isEmpty() ? 0 : Integer.parseInt(minimoCanje));

        ps.setString(5, txtDescripcion.getText().trim());
        ps.setBoolean(6, chkEstado.isSelected());
    }

    private boolean validarFormularioPrograma() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre del programa es obligatorio", Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }

    private void actualizarContadores() {
        int activos = 0;
        int inactivos = 0;
        int totalPuntos = 0;

        try {
            String sql = "SELECT estado, COUNT(*) as total FROM tbl_SISTEMA_FIDELIZACION GROUP BY estado";
            try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    boolean estado = rs.getBoolean("estado");
                    int total = rs.getInt("total");
                    if (estado) {
                        activos = total;
                    } else {
                        inactivos = total;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar programas: " + e.getMessage());
        }

        lblContActivo.setText("✔ " + activos + " Activos");
        lblContVencido.setText("✖ " + inactivos + " Inactivos");
        lblTotalPuntos.setText("⭐ " + totalPuntos + " Puntos");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}