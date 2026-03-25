package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DetallePerdida {
    private int idDetPerdida;
    private int idPerdida;
    private int idProducto;
    private int cantidad;
    private BigDecimal costoUnitario;
    private LocalDate fechaVencimiento;
    private String lote;

    public DetallePerdida(int idDetPerdida, int idPerdida, int idProducto, int cantidad, BigDecimal costoUnitario, LocalDate fechaVencimiento, String lote) {
        this.idDetPerdida = idDetPerdida;
        this.idPerdida = idPerdida;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.fechaVencimiento = fechaVencimiento;
        this.lote = lote;
    }

    public int getIdDetPerdida() {
        return idDetPerdida;
    }

    public void setIdDetPerdida(int idDetPerdida) {
        this.idDetPerdida = idDetPerdida;
    }

    public int getIdPerdida() {
        return idPerdida;
    }

    public void setIdPerdida(int idPerdida) {
        this.idPerdida = idPerdida;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }
}
