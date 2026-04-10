package org.example.proyecto.Modelos;

import java.time.LocalDateTime;

public class Reclamacion {
    private int           idReclamacion;
    private int           idCliente;
    private int           idEmpleado;
    private int           idMotivo;
    private LocalDateTime fecha;
    private String        descripcion;
    private String        estado;

    public Reclamacion() {

    }

    public int getIdReclamacion() {
        return idReclamacion;
    }

    public void setIdReclamacion(int idReclamacion) {
        this.idReclamacion = idReclamacion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Reclamacion(int idReclamacion, int idCliente, int idEmpleado, int idMotivo, LocalDateTime fecha, String descripcion, String estado) {
        this.idReclamacion = idReclamacion;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.idMotivo = idMotivo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
