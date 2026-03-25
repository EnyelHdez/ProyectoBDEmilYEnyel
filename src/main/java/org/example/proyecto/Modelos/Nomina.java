package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Nomina {
    private int idNomina;
    private int idEmpleado;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private BigDecimal salarioBruto;
    private BigDecimal bonificaciones;
    private BigDecimal comisiones;
    private BigDecimal deduccionesSfs;
    private BigDecimal deduccionesAfp;
    private BigDecimal otrasDeducciones;
    private BigDecimal salarioNeto;
    private LocalDate fechaPago;

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(BigDecimal salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public BigDecimal getBonificaciones() {
        return bonificaciones;
    }

    public void setBonificaciones(BigDecimal bonificaciones) {
        this.bonificaciones = bonificaciones;
    }

    public BigDecimal getComisiones() {
        return comisiones;
    }

    public void setComisiones(BigDecimal comisiones) {
        this.comisiones = comisiones;
    }

    public BigDecimal getDeduccionesSfs() {
        return deduccionesSfs;
    }

    public void setDeduccionesSfs(BigDecimal deduccionesSfs) {
        this.deduccionesSfs = deduccionesSfs;
    }

    public BigDecimal getDeduccionesAfp() {
        return deduccionesAfp;
    }

    public void setDeduccionesAfp(BigDecimal deduccionesAfp) {
        this.deduccionesAfp = deduccionesAfp;
    }

    public BigDecimal getOtrasDeducciones() {
        return otrasDeducciones;
    }

    public void setOtrasDeducciones(BigDecimal otrasDeducciones) {
        this.otrasDeducciones = otrasDeducciones;
    }

    public BigDecimal getSalarioNeto() {
        return salarioNeto;
    }

    public void setSalarioNeto(BigDecimal salarioNeto) {
        this.salarioNeto = salarioNeto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Nomina(int idNomina, int idEmpleado, LocalDate periodoInicio, LocalDate periodoFin, BigDecimal salarioBruto, BigDecimal bonificaciones, BigDecimal comisiones, BigDecimal deduccionesSfs, BigDecimal deduccionesAfp, BigDecimal otrasDeducciones, BigDecimal salarioNeto, LocalDate fechaPago, String estado) {
        this.idNomina = idNomina;
        this.idEmpleado = idEmpleado;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.salarioBruto = salarioBruto;
        this.bonificaciones = bonificaciones;
        this.comisiones = comisiones;
        this.deduccionesSfs = deduccionesSfs;
        this.deduccionesAfp = deduccionesAfp;
        this.otrasDeducciones = otrasDeducciones;
        this.salarioNeto = salarioNeto;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    private String estado; // PENDIENTE, PAGADA, ANULADA

}
