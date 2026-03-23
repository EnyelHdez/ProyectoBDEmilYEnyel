package org.example.proyecto.Conexion;

import javax.swing.*;
import java.sql.*;

public class ConexionBD {
    Connection connection = null;
    String usuario = "conexion";
    String contrasena = "root1";
    String db = "FarmaciaKenia";
    String server = "localhost";
    String puerto = "1433";

    String cadena = "jdbc:sqlserver://" + server + "." + puerto + "/" + db;

    public Connection EstablecerConexion() {
        try {

            String cadena = "jdbc:sqlserver://" + server + ":" + puerto + ";" + "databaseName=" + db + ";" + "encrypt=true" + ";" + "trustServerCertificate=true";
            connection = DriverManager.getConnection(cadena, usuario, contrasena);
            JOptionPane.showMessageDialog
                    (null, "Se realizo exitosamente la conexion bro");
        } catch (Exception e) {
            JOptionPane.showMessageDialog
                    (null, "te dio error menol" + e.toString());
            //e.toString es para imprimir el error.

        }
        return connection;
    }

    public void InsertarDatos() {

        String sql = "INSERT INTO Persona(Nombre, Apellido, Direccion, Telefono, Correo, Estado) VALUES(?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "Lolita");
            pstmt.setString(2, "Herrera");
            pstmt.setString(3, "Santiago");
            pstmt.setString(4, "8092345700");
            pstmt.setString(5, "klkmanito@gmail.com");
            pstmt.setString(6, "A");

            int filasInsertadas = pstmt.executeUpdate();
            System.out.println("dilas insertadas: " + filasInsertadas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog
                    (null, "Error al insertar" + e.toString());
        }
    }

    public void Borrar(int idpersona) {

    }

    public void actualizarDatos(int id, String nombre) {

        String query = "Update Persona set direccion = ? where idPersona = 3";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, "NuevaCasa");

            int filaActualizada = pstmt.executeUpdate();
            System.out.println("Fila actualizada: " + filaActualizada);
        } catch (SQLException a) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el dato");

        }
    }

    public void LeerDatos() {
        String sql = "Select * from persona";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                System.out.println("codigo" + rs.getInt("idpersona"));
                System.out.println("codigo" + rs.getString("Nombre") + "\n");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERORRRRR BRO" + e.toString());
        }

    }

    public void InsertarDatos(String nombre, String apellido, String direccion, String telefono, String correo, String a) {
    }


}

