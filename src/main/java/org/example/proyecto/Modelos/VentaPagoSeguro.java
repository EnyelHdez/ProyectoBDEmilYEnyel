package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class VentaPagoSeguro {
    private int idPagoVseg;
    private int idVentaSeguro;
    private int idPago;
    private BigDecimal monto;

    public int getIdPagoVseg() {
        return idPagoVseg;
    }

    public void setIdPagoVseg(int idPagoVseg) {
        this.idPagoVseg = idPagoVseg;
    }

    public int getIdVentaSeguro() {
        return idVentaSeguro;
    }

    public void setIdVentaSeguro(int idVentaSeguro) {
        this.idVentaSeguro = idVentaSeguro;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public VentaPagoSeguro(int idPagoVseg, int idVentaSeguro, int idPago, BigDecimal monto) {
        this.idPagoVseg = idPagoVseg;
        this.idVentaSeguro = idVentaSeguro;
        this.idPago = idPago;
        this.monto = monto;
    }
}
