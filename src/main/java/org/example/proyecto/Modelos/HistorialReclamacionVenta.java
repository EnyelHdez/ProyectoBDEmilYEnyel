package org.example.proyecto.Modelos;

import java.time.LocalDateTime;

public class HistorialReclamacionVenta {
    private int idHistReclV;
    private int idReclVenta;
    private int idEmpleado;
    private LocalDateTime fecha;
    private String observacion;

    public int getIdHistReclV() {
        return idHistReclV;
    }

    public void setIdHistReclV(int idHistReclV) {
        this.idHistReclV = idHistReclV;
    }

    public int getIdReclVenta() {
        return idReclVenta;
    }

    public void setIdReclVenta(int idReclVenta) {
        this.idReclVenta = idReclVenta;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public HistorialReclamacionVenta(int idHistReclV, int idReclVenta, int idEmpleado, LocalDateTime fecha, String observacion) {
        this.idHistReclV = idHistReclV;
        this.idReclVenta = idReclVenta;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.observacion = observacion;
    }
}
