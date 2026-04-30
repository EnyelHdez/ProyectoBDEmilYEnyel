package org.example.proyecto.DAO;

import org.example.proyecto.Modelos.Usuario;
import org.example.proyecto.Conexion.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Registrar nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO tbl_USUARIO (nombre_usuario, contrasena, nombre_completo, email, cargo, telefono, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getCargo());
            pstmt.setString(6, usuario.getTelefono());
            pstmt.setBoolean(7, usuario.isEstado());
            pstmt.setTimestamp(8, Timestamp.valueOf(usuario.getFechaRegistro()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM tbl_USUARIO ORDER BY id_usuario DESC";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCargo(rs.getString("cargo"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setEstado(rs.getBoolean("estado"));

                Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
                if (fechaRegistro != null) {
                    usuario.setFechaRegistro(fechaRegistro.toLocalDateTime());
                }

                Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
                if (ultimoAcceso != null) {
                    usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
                }

                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Verificar si existe usuario
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

    // Actualizar usuario
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE tbl_USUARIO SET nombre_usuario = ?, nombre_completo = ?, email = ?, cargo = ?, telefono = ?, estado = ?";

        // Si se proporciona una nueva contraseña, incluirla en la actualización
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            sql = "UPDATE tbl_USUARIO SET nombre_usuario = ?, contrasena = ?, nombre_completo = ?, email = ?, cargo = ?, telefono = ?, estado = ? WHERE id_usuario = ?";
        } else {
            sql += " WHERE id_usuario = ?";
        }

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int index = 1;
            pstmt.setString(index++, usuario.getNombreUsuario());

            if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
                pstmt.setString(index++, usuario.getContrasena());
            }

            pstmt.setString(index++, usuario.getNombreCompleto());
            pstmt.setString(index++, usuario.getEmail());
            pstmt.setString(index++, usuario.getCargo());
            pstmt.setString(index++, usuario.getTelefono());
            pstmt.setBoolean(index++, usuario.isEstado());
            pstmt.setInt(index++, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar usuario
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM tbl_USUARIO WHERE id_usuario = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar último acceso
    public boolean actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE tbl_USUARIO SET ultimo_acceso = ? WHERE id_usuario = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, idUsuario);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Buscar usuario por nombre de usuario (para login)
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM tbl_USUARIO WHERE nombre_usuario = ?";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCargo(rs.getString("cargo"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setEstado(rs.getBoolean("estado"));

                Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
                if (fechaRegistro != null) {
                    usuario.setFechaRegistro(fechaRegistro.toLocalDateTime());
                }

                Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
                if (ultimoAcceso != null) {
                    usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
                }

                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Agrega este método a tu UsuarioDAO.java
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM tbl_USUARIO WHERE nombre_usuario = ? AND contrasena = ? AND estado = 1";

        try (Connection conn = new ConexionBD().EstablecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCargo(rs.getString("cargo"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setEstado(rs.getBoolean("estado"));

                Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
                if (fechaRegistro != null) {
                    usuario.setFechaRegistro(fechaRegistro.toLocalDateTime());
                }

                Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
                if (ultimoAcceso != null) {
                    usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
                }

                // Actualizar último acceso
                actualizarUltimoAcceso(usuario.getIdUsuario());

                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}