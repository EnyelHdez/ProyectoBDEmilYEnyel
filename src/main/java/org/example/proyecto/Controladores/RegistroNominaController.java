package org.example.proyecto.Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.proyecto.Conexion.ConexionBD;
import org.example.proyecto.Modelos.Nomina;
import org.example.proyecto.Modelos.Usuarios.SesionUsuario;
import org.example.proyecto.util.EmailUtil;
import org.example.proyecto.util.ReportUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroNominaController implements Initializable {

    // Panel de consulta
    @FXML private VBox consultaPanel;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerTodos;
    @FXML private Button btnCerrarConsulta;

    // Botones de acción
    @FXML private Button btnConsultar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnEnviarCorreo;

    // Tabla
    @FXML private TableView<Nomina> tblNominas;
    @FXML private TableColumn<Nomina, Integer> colId;
    @FXML private TableColumn<Nomina, String> colEmpleado;
    @FXML private TableColumn<Nomina, LocalDate> colPeriodoInicio;
    @FXML private TableColumn<Nomina, LocalDate> colPeriodoFin;
    @FXML private TableColumn<Nomina, BigDecimal> colSalarioBruto;
    @FXML private TableColumn<Nomina, BigDecimal> colBonificaciones;
    @FXML private TableColumn<Nomina, BigDecimal> colComisiones;
    @FXML private TableColumn<Nomina, BigDecimal> colDeduccionesSfs;
    @FXML private TableColumn<Nomina, BigDecimal> colDeduccionesAfp;
    @FXML private TableColumn<Nomina, BigDecimal> colOtrasDeducciones;
    @FXML private TableColumn<Nomina, BigDecimal> colSalarioNeto;
    @FXML private TableColumn<Nomina, LocalDate> colFechaPago;
    @FXML private TableColumn<Nomina, String> colEstado;

    // Formulario
    @FXML private ComboBox<String> cmbEmpleado;
    @FXML private DatePicker datePeriodoInicio;
    @FXML private DatePicker datePeriodoFin;
    @FXML private TextField txtSalarioBruto;
    @FXML private TextField txtBonificaciones;
    @FXML private TextField txtComisiones;
    @FXML private TextField txtDeduccionesSfs;
    @FXML private TextField txtDeduccionesAfp;
    @FXML private TextField txtOtrasDeducciones;
    @FXML private TextField txtSalarioNeto;
    @FXML private DatePicker dateFechaPago;
    @FXML private ComboBox<String> cmbEstado;

    // Estado interno
    private Connection conexion;
    private int idNominaSeleccionada = 0;
    private final ObservableList<Nomina> listaNominas = FXCollections.observableArrayList();
    private boolean modoEdicion = false;

    // Almacenar info completa del empleado para la tabla
    private final ObservableList<String> listaEmpleados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = new ConexionBD().EstablecerConexion();

            cargarEmpleados();

            cmbEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));
            cmbEstado.setValue("PENDIENTE");

            datePeriodoInicio.setValue(LocalDate.now());
            datePeriodoFin.setValue(LocalDate.now());
            dateFechaPago.setValue(LocalDate.now());

            configurarTabla();
            configurarSeleccionTabla();
            configurarBotonesPorRol();

            consultaPanel.setVisible(false);
            consultaPanel.setManaged(false);

            habilitarBotonesEdicion(false);
            btnGuardar.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarBotonesPorRol() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        boolean puedeEditar = "Administrador".equals(rol);
        boolean puedeEliminar = "Administrador".equals(rol);

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
        cargarNominas();
    }

    @FXML
    private void cerrarConsulta() {
        consultaPanel.setVisible(false);
        consultaPanel.setManaged(false);
    }

    private void cargarEmpleados() {
        listaEmpleados.clear();
        String sql = "SELECT id_empleado, nombres FROM tbl_EMPLEADO ORDER BY nombres";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                listaEmpleados.add(rs.getInt("id_empleado") + " - " + rs.getString("nombres"));
            cmbEmpleado.setItems(listaEmpleados);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar empleados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idNomina"));
        colEmpleado.setCellValueFactory(cellData -> {
            int idEmp = cellData.getValue().getIdEmpleado();
            String nombre = listaEmpleados.stream()
                    .filter(s -> s.startsWith(idEmp + " - "))
                    .findFirst()
                    .map(s -> s.split(" - ", 2)[1])
                    .orElse("Empleado " + idEmp);
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colPeriodoInicio.setCellValueFactory(new PropertyValueFactory<>("periodoInicio"));
        colPeriodoFin.setCellValueFactory(new PropertyValueFactory<>("periodoFin"));
        colSalarioBruto.setCellValueFactory(new PropertyValueFactory<>("salarioBruto"));
        colBonificaciones.setCellValueFactory(new PropertyValueFactory<>("bonificaciones"));
        colComisiones.setCellValueFactory(new PropertyValueFactory<>("comisiones"));
        colDeduccionesSfs.setCellValueFactory(new PropertyValueFactory<>("deduccionesSfs"));
        colDeduccionesAfp.setCellValueFactory(new PropertyValueFactory<>("deduccionesAfp"));
        colOtrasDeducciones.setCellValueFactory(new PropertyValueFactory<>("otrasDeducciones"));
        colSalarioNeto.setCellValueFactory(new PropertyValueFactory<>("salarioNeto"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblNominas.setItems(listaNominas);
    }

    private void configurarSeleccionTabla() {
        tblNominas.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        cargarDatosEnFormulario(newVal);
                        habilitarBotonesEdicion(true);
                        modoEdicion = true;
                        btnGuardar.setDisable(true);
                    }
                });
    }

    private void cargarDatosEnFormulario(Nomina n) {
        idNominaSeleccionada = n.getIdNomina();

        String empBuscado = listaEmpleados.stream()
                .filter(s -> s.startsWith(n.getIdEmpleado() + " - "))
                .findFirst()
                .orElse(null);
        cmbEmpleado.setValue(empBuscado);

        datePeriodoInicio.setValue(n.getPeriodoInicio());
        datePeriodoFin.setValue(n.getPeriodoFin());
        txtSalarioBruto.setText(n.getSalarioBruto() != null ? n.getSalarioBruto().toPlainString() : "");
        txtBonificaciones.setText(n.getBonificaciones() != null ? n.getBonificaciones().toPlainString() : "");
        txtComisiones.setText(n.getComisiones() != null ? n.getComisiones().toPlainString() : "");
        txtDeduccionesSfs.setText(n.getDeduccionesSfs() != null ? n.getDeduccionesSfs().toPlainString() : "");
        txtDeduccionesAfp.setText(n.getDeduccionesAfp() != null ? n.getDeduccionesAfp().toPlainString() : "");
        txtOtrasDeducciones.setText(n.getOtrasDeducciones() != null ? n.getOtrasDeducciones().toPlainString() : "");
        txtSalarioNeto.setText(n.getSalarioNeto() != null ? n.getSalarioNeto().toPlainString() : "");
        dateFechaPago.setValue(n.getFechaPago());
        cmbEstado.setValue(n.getEstado() != null ? n.getEstado() : "PENDIENTE");
    }

    @FXML
    private void cargarNominas() {
        listaNominas.clear();
        String sql = "SELECT * FROM tbl_NOMINA ORDER BY id_nomina DESC";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Nomina n = new Nomina(
                        rs.getInt("id_nomina"),
                        rs.getInt("id_empleado"),
                        rs.getDate("periodo_inicio") != null ? rs.getDate("periodo_inicio").toLocalDate() : null,
                        rs.getDate("periodo_fin") != null ? rs.getDate("periodo_fin").toLocalDate() : null,
                        rs.getBigDecimal("salario_bruto"),
                        rs.getBigDecimal("bonificaciones"),
                        rs.getBigDecimal("comisiones"),
                        rs.getBigDecimal("deducciones_sfs"),
                        rs.getBigDecimal("deducciones_afp"),
                        rs.getBigDecimal("otras_deducciones"),
                        rs.getBigDecimal("salario_neto"),
                        rs.getDate("fecha_pago") != null ? rs.getDate("fecha_pago").toLocalDate() : null,
                        rs.getString("estado")
                );
                listaNominas.add(n);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar nóminas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscarNomina() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) { cargarNominas(); return; }

        listaNominas.clear();
        String sql = "SELECT n.*, e.nombres FROM tbl_NOMINA n " +
                "LEFT JOIN tbl_EMPLEADO e ON n.id_empleado = e.id_empleado " +
                "WHERE e.nombres LIKE ? OR n.estado LIKE ? " +
                "ORDER BY n.id_nomina DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            String like = "%" + filtro + "%";
            ps.setString(1, like); ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Nomina nom = new Nomina(
                        rs.getInt("id_nomina"),
                        rs.getInt("id_empleado"),
                        rs.getDate("periodo_inicio") != null ? rs.getDate("periodo_inicio").toLocalDate() : null,
                        rs.getDate("periodo_fin") != null ? rs.getDate("periodo_fin").toLocalDate() : null,
                        rs.getBigDecimal("salario_bruto"),
                        rs.getBigDecimal("bonificaciones"),
                        rs.getBigDecimal("comisiones"),
                        rs.getBigDecimal("deducciones_sfs"),
                        rs.getBigDecimal("deducciones_afp"),
                        rs.getBigDecimal("otras_deducciones"),
                        rs.getBigDecimal("salario_neto"),
                        rs.getDate("fecha_pago") != null ? rs.getDate("fecha_pago").toLocalDate() : null,
                        rs.getString("estado")
                );
                listaNominas.add(nom);
            }
            if (listaNominas.isEmpty())
                mostrarAlerta("Información", "No se encontraron resultados para: " + filtro, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cargarNominas();
    }

    @FXML
    private void guardarNomina() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "No tiene permisos para guardar nóminas", Alert.AlertType.ERROR);
            return;
        }

        if (!validarCampos()) return;

        String sql = "INSERT INTO tbl_NOMINA " +
                "(id_empleado, periodo_inicio, periodo_fin, salario_bruto, bonificaciones, comisiones, " +
                " deducciones_sfs, deducciones_afp, otras_deducciones, salario_neto, fecha_pago, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Nómina registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarNominas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarNomina() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden editar nóminas", Alert.AlertType.ERROR);
            return;
        }

        if (idNominaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una nómina de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }
        if (!validarCampos()) return;

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar edición");
        conf.setHeaderText(null);
        conf.setContentText("¿Desea guardar los cambios en esta nómina?");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        String sql = "UPDATE tbl_NOMINA " +
                "SET id_empleado=?, periodo_inicio=?, periodo_fin=?, salario_bruto=?, bonificaciones=?, comisiones=?, " +
                "    deducciones_sfs=?, deducciones_afp=?, otras_deducciones=?, salario_neto=?, fecha_pago=?, estado=? " +
                "WHERE id_nomina=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            setearParametros(ps);
            ps.setInt(13, idNominaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Nómina actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarNominas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarNomina() {
        String rol = SesionUsuario.getInstancia().getCargoUsuario();
        if (!rol.equals("Administrador")) {
            mostrarAlerta("Permiso denegado", "Solo Administradores pueden eliminar nóminas", Alert.AlertType.ERROR);
            return;
        }

        if (idNominaSeleccionada == 0) {
            mostrarAlerta("Advertencia", "Seleccione una nómina de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Está seguro que desea eliminar esta nómina?\nEsta acción no se puede deshacer.");
        if (conf.showAndWait().get() != ButtonType.OK) return;

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM tbl_NOMINA WHERE id_nomina=?")) {
            ps.setInt(1, idNominaSeleccionada);
            ps.executeUpdate();
            mostrarAlerta("Éxito", "Nómina eliminada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposInterno();
            if (consultaPanel.isVisible()) {
                cargarNominas();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se puede eliminar: la nómina tiene registros asociados.", Alert.AlertType.ERROR);
        }
    }

    private void setearParametros(PreparedStatement ps) throws SQLException {
        int idx = 1;

        String empVal = cmbEmpleado.getValue();
        if (empVal != null && !empVal.isEmpty())
            ps.setInt(idx++, Integer.parseInt(empVal.split(" - ")[0]));
        else
            ps.setNull(idx++, Types.INTEGER);

        ps.setDate(idx++, datePeriodoInicio.getValue() != null ? Date.valueOf(datePeriodoInicio.getValue()) : null);
        ps.setDate(idx++, datePeriodoFin.getValue() != null ? Date.valueOf(datePeriodoFin.getValue()) : null);
        ps.setBigDecimal(idx++, getBigDecimal(txtSalarioBruto));
        ps.setBigDecimal(idx++, getBigDecimal(txtBonificaciones));
        ps.setBigDecimal(idx++, getBigDecimal(txtComisiones));
        ps.setBigDecimal(idx++, getBigDecimal(txtDeduccionesSfs));
        ps.setBigDecimal(idx++, getBigDecimal(txtDeduccionesAfp));
        ps.setBigDecimal(idx++, getBigDecimal(txtOtrasDeducciones));
        ps.setBigDecimal(idx++, getBigDecimal(txtSalarioNeto));
        ps.setDate(idx++, dateFechaPago.getValue() != null ? Date.valueOf(dateFechaPago.getValue()) : null);
        ps.setString(idx++, cmbEstado.getValue() != null ? cmbEstado.getValue() : "PENDIENTE");
    }

    private BigDecimal getBigDecimal(TextField tf) {
        String txt = tf.getText().trim();
        if (txt.isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(txt);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @FXML
    private void limpiarCampos() {
        limpiarCamposInterno();
    }

    private void limpiarCamposInterno() {
        idNominaSeleccionada = 0;
        cmbEmpleado.setValue(null);
        datePeriodoInicio.setValue(LocalDate.now());
        datePeriodoFin.setValue(LocalDate.now());
        txtSalarioBruto.clear();
        txtBonificaciones.clear();
        txtComisiones.clear();
        txtDeduccionesSfs.clear();
        txtDeduccionesAfp.clear();
        txtOtrasDeducciones.clear();
        txtSalarioNeto.clear();
        dateFechaPago.setValue(LocalDate.now());
        cmbEstado.setValue("PENDIENTE");
        modoEdicion = false;
        habilitarBotonesEdicion(false);
        btnGuardar.setDisable(false);
        tblNominas.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (cmbEmpleado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el empleado.", Alert.AlertType.WARNING);
            cmbEmpleado.requestFocus();
            return false;
        }
        if (datePeriodoInicio.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el período inicio.", Alert.AlertType.WARNING);
            datePeriodoInicio.requestFocus();
            return false;
        }
        if (datePeriodoFin.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el período fin.", Alert.AlertType.WARNING);
            datePeriodoFin.requestFocus();
            return false;
        }
        if (txtSalarioBruto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "Ingrese el salario bruto.", Alert.AlertType.WARNING);
            txtSalarioBruto.requestFocus();
            return false;
        }
        if (txtSalarioNeto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "Ingrese el salario neto.", Alert.AlertType.WARNING);
            txtSalarioNeto.requestFocus();
            return false;
        }
        if (dateFechaPago.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la fecha de pago.", Alert.AlertType.WARNING);
            dateFechaPago.requestFocus();
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el estado.", Alert.AlertType.WARNING);
            cmbEstado.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    private void generarReporte() {
        Nomina seleccion = tblNominas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Seleccionar Nómina", "Debe seleccionar una nómina para generar el reporte.", Alert.AlertType.WARNING);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id_nomina", seleccion.getIdNomina());
        ReportUtil.generarReporte("Nominas", "/reportes/ReporteNominas.jasper", params, conexion);
    }

    @FXML
    private void enviarReporte() {
        Nomina seleccion = tblNominas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Seleccionar Nómina", "Debe seleccionar una nómina para enviar el reporte.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String correo = "";
            String sql = "SELECT e.email FROM tbl_EMPLEADO e JOIN tbl_NOMINA n ON e.id_empleado = n.id_empleado WHERE n.id_nomina = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, seleccion.getIdNomina());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    correo = rs.getString("email");
                }
            }
            if (correo == null || correo.trim().isEmpty()) {
                mostrarAlerta("Sin correo", "El empleado no tiene un correo electrónico registrado.", Alert.AlertType.WARNING);
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id_nomina", seleccion.getIdNomina());
            byte[] pdf = ReportUtil.generarReportePDF("/reportes/ReporteNominas.jasper", params, conexion);
            if (pdf == null) return;

            EmailUtil.enviarFacturaPDF(correo,
                    "Nómina #" + seleccion.getIdNomina(),
                    "Estimado empleado,\n\nAdjunto encontrará su comprobante de nómina.\n\nSaludos cordiales.\nFarmacia Kenia Carmen",
                    pdf, "Nomina_" + seleccion.getIdNomina() + ".pdf");

            mostrarAlerta("Correo enviado", "La nómina ha sido enviada exitosamente a: " + correo, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al enviar el correo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
