package org.example.proyecto.DAO;

import org.example.proyecto.Modelos.SeguroMedico;
import org.example.proyecto.Conexion.ConexionBD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeguroMedicoDAO {

    public SeguroMedico obtenerPorIdCliente(int idCliente) throws SQLException {
        String sql = "SELECT * FROM seguro_medico WHERE id_cliente = ? AND estado = 1 " +
                "AND GETDATE() BETWEEN vigencia_inicio AND vigencia_fin";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearSeguroMedico(rs);
                }
            }
        }
        return null;
    }

    // Obtener seguro médico por ID
    public SeguroMedico obtenerPorId(int idSeguro) throws SQLException {
        String sql = "SELECT * FROM seguro_medico WHERE id_seguro = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSeguro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearSeguroMedico(rs);
                }
            }
        }
        return null;
    }

    // Obtener todos los seguros médicos activos
    public List<SeguroMedico> obtenerTodos() throws SQLException {
        List<SeguroMedico> seguros = new ArrayList<>();
        String sql = "SELECT * FROM seguro_medico WHERE estado = 1";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                seguros.add(mapearSeguroMedico(rs));
            }
        }
        return seguros;
    }

    // Obtener seguros médicos por ARS
    public List<SeguroMedico> obtenerPorIdArs(int idArs) throws SQLException {
        List<SeguroMedico> seguros = new ArrayList<>();
        String sql = "SELECT * FROM seguro_medico WHERE id_ars = ? AND estado = 1";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idArs);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seguros.add(mapearSeguroMedico(rs));
                }
            }
        }
        return seguros;
    }

    // Verificar si un cliente tiene seguro válido
    public boolean tieneSeguroValido(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM seguro_medico WHERE id_cliente = ? AND estado = 1 " +
                "AND GETDATE() BETWEEN vigencia_inicio AND vigencia_fin";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Calcular monto que cubre el seguro para una venta
    public BigDecimal calcularMontoSeguro(BigDecimal totalVenta, BigDecimal porcentajeCobertura, BigDecimal limiteAnual, BigDecimal acumuladoAnual) {
        BigDecimal montoSeguro = totalVenta.multiply(porcentajeCobertura.divide(new BigDecimal("100")));

        // Verificar límite anual
        if (limiteAnual != null && acumuladoAnual != null) {
            BigDecimal disponible = limiteAnual.subtract(acumuladoAnual);
            if (montoSeguro.compareTo(disponible) > 0) {
                montoSeguro = disponible;
            }
        }

        return montoSeguro.setScale(2, RoundingMode.HALF_UP);
    }

    // Obtener acumulado anual del cliente
    public BigDecimal obtenerAcumuladoAnual(int idCliente, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto_seguro), 0) as acumulado " +
                "FROM ventas_seguro vs " +
                "INNER JOIN ventas v ON vs.id_venta = v.id_venta " +
                "WHERE v.id_cliente = ? AND YEAR(v.fecha) = ? AND vs.estado = 1";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("acumulado");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private SeguroMedico mapearSeguroMedico(ResultSet rs) throws SQLException {
        return new SeguroMedico(
                rs.getInt("id_seguro"),
                rs.getInt("id_ars"),
                rs.getInt("id_cliente"),
                rs.getString("nro_afiliado"),
                rs.getString("plan"),
                rs.getBigDecimal("porcentaje_cob"),
                rs.getBigDecimal("limite_anual"),
                rs.getDate("vigencia_inicio").toLocalDate(),
                rs.getDate("vigencia_fin").toLocalDate(),
                rs.getBoolean("estado")
        );
    }
}