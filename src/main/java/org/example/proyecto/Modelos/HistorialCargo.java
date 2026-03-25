package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistorialCargo {
    private int        idHistorialCargo;
    private int        idEmpleado;
    private int        idCargo;
    private LocalDate  fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal salario;
    private String     observacion;

    public HistorialCargo(int idHistorialCargo, int idEmpleado, int idCargo, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal salario, String observacion) {
        this.idHistorialCargo = idHistorialCargo;
        this.idEmpleado = idEmpleado;
        this.idCargo = idCargo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.salario = salario;
        this.observacion = observacion;
    }

    public int getIdHistorialCargo() {
        return idHistorialCargo;
    }

    public void setIdHistorialCargo(int idHistorialCargo) {
        this.idHistorialCargo = idHistorialCargo;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
