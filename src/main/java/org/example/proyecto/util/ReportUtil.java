package org.example.proyecto.util;

import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.*;
import java.sql.Connection;
import java.util.Map;

public class ReportUtil {

    public static void generarReporte(String titulo, String jasperResourcePath, Map<String, Object> parametros, Connection conexion) {
        try {
            InputStream jasperStream = ReportUtil.class.getResourceAsStream(jasperResourcePath);
            if (jasperStream == null) {
                mostrarError("No se encontró el archivo compilado: " + jasperResourcePath);
                return;
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            parametros.put("REPORT_LOCALE", new java.util.Locale("es", "DO"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conexion);

            if (jasperPrint.getPages().isEmpty()) {
                mostrarError("El reporte no contiene datos para los criterios actuales.");
                return;
            }

            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            mostrarError("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarError(String mensaje) {
        javafx.application.Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error - Reporte");
            a.setHeaderText(null);
            a.setContentText(mensaje);
            a.showAndWait();
        });
    }
}
