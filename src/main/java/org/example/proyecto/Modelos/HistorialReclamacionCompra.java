package org.example.proyecto.Modelos;

import java.time.LocalDateTime;

public class HistorialReclamacionCompra {
    private int idHistReclC;
    private int idReclCompra;
    private int idEmpleado;
    private LocalDateTime fecha;
    private String observacion;

    public HistorialReclamacionCompra(int idHistReclC, int idReclCompra, int idEmpleado, LocalDateTime fecha, String observacion) {
        this.idHistReclC = idHistReclC;
        this.idReclCompra = idReclCompra;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public int getIdHistReclC() {
        return idHistReclC;
    }

    public void setIdHistReclC(int idHistReclC) {
        this.idHistReclC = idHistReclC;
    }

    public int getIdReclCompra() {
        return idReclCompra;
    }

    public void setIdReclCompra(int idReclCompra) {
        this.idReclCompra = idReclCompra;
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
}
