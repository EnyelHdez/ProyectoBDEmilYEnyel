package org.example.proyecto.Conexion;

import javax.swing.*;
import java.sql.*;

public class ConexionBD {
    Connection connection = null;
    String usuario = "EmilPrueba";
    String contrasena = "123456";
    String db = "FarmaciaCarmen2";
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

        }
        return connection;
    }
}

