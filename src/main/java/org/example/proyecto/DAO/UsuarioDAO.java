package org.example.proyecto.DAO;


import org.example.proyecto.Modelos.Usuario;
import org.example.proyecto.Conexion.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;

public class UsuarioDAO {

    // Método para autenticar usuario
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM tbl_USUARIO WHERE nombre_usuario = ? AND estado = 1";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Verificar contraseña (en producción, usar hash)
                String contrasenaBD = rs.getString("contrasena");
                if (contrasenaBD.equals(contrasena)) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setCargo(rs.getString("cargo"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setEstado(rs.getBoolean("estado"));

                    // Actualizar último acceso
                    actualizarUltimoAcceso(usuario.getIdUsuario());

                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para registrar nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO tbl_USUARIO (nombre_usuario, contrasena, nombre_completo, email, cargo, telefono, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena()); // En producción, aplicar hash
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getCargo());
            pstmt.setString(6, usuario.getTelefono());
            pstmt.setBoolean(7, usuario.isEstado());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar si el nombre de usuario ya existe
    public boolean existeUsuario(String nombreUsuario) {
        String sql = "SELECT COUNT(*) FROM tbl_USUARIO WHERE nombre_usuario = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para actualizar último acceso
    private void actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE tbl_USUARIO SET ultimo_acceso = ? WHERE id_usuario = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, idUsuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
