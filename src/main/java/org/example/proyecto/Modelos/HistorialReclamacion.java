package org.example.proyecto.Modelos;

import java.time.LocalDateTime;

public class HistorialReclamacion {
    private int idHistRecl;
    private int idReclamacion;
    private int idEmpleado;
    private LocalDateTime fecha;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;

    public int getIdHistRecl() {
        return idHistRecl;
    }

    public void setIdHistRecl(int idHistRecl) {
        this.idHistRecl = idHistRecl;
    }

    public int getIdReclamacion() {
        return idReclamacion;
    }

    public void setIdReclamacion(int idReclamacion) {
        this.idReclamacion = idReclamacion;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public HistorialReclamacion(String estadoNuevo, String estadoAnterior, String accion, LocalDateTime fecha, int idEmpleado, int idReclamacion, int idHistRecl) {
        this.estadoNuevo = estadoNuevo;
        this.estadoAnterior = estadoAnterior;
        this.accion = accion;
        this.fecha = fecha;
        this.idEmpleado = idEmpleado;
        this.idReclamacion = idReclamacion;
        this.idHistRecl = idHistRecl;
    }
}
