package org.example.proyecto.Modelos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Usuario {
    private int idUsuario;  // Cambiado de id a idUsuario
    private String nombreUsuario;
    private String contrasena;
    private String nombreCompleto;
    private String email;
    private String cargo;
    private String telefono;
    private boolean estado;
    private LocalDateTime fechaRegistro;  // Cambiado a LocalDateTime
    private LocalDateTime ultimoAcceso;   // Nuevo campo

    // Constructor vacío
    public Usuario() {}

    // Constructor con parámetros
    public Usuario(int idUsuario, String nombreUsuario, String contrasena, String nombreCompleto,
                   String email, String cargo, String telefono, boolean estado,
                   LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.cargo = cargo;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    // Método para mostrar fecha registro como String
    public String getFechaRegistroStr() {
        if (fechaRegistro != null) {
            return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    // Método para mostrar estado como String
    public String getEstadoStr() {
        return estado ? "Activo" : "Inactivo";
    }

    @Override
    public String toString() {
        return nombreUsuario + " - " + nombreCompleto;
    }
}