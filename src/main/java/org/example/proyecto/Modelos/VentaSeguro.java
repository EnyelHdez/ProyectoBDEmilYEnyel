package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class VentaSeguro {
    private int idVentaSeguro;
    private int idVenta;
    private int idSeguro;
    private BigDecimal montoSeguro;
    private BigDecimal montoPaciente;
    private boolean estado;

    public VentaSeguro(int idVentaSeguro, int idVenta, int idSeguro, BigDecimal montoSeguro, BigDecimal montoPaciente, boolean estado) {
        this.idVentaSeguro = idVentaSeguro;
        this.idVenta = idVenta;
        this.idSeguro = idSeguro;
        this.montoSeguro = montoSeguro;
        this.montoPaciente = montoPaciente;
        this.estado = estado;
    }

    public int getIdVentaSeguro() {
        return idVentaSeguro;
    }

    public void setIdVentaSeguro(int idVentaSeguro) {
        this.idVentaSeguro = idVentaSeguro;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdSeguro() {
        return idSeguro;
    }

    public void setIdSeguro(int idSeguro) {
        this.idSeguro = idSeguro;
    }

    public BigDecimal getMontoSeguro() {
        return montoSeguro;
    }

    public void setMontoSeguro(BigDecimal montoSeguro) {
        this.montoSeguro = montoSeguro;
    }

    public BigDecimal getMontoPaciente() {
        return montoPaciente;
    }

    public void setMontoPaciente(BigDecimal montoPaciente) {
        this.montoPaciente = montoPaciente;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
