package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private int           idPago;
    private int           idCuentaPago;
    private BigDecimal    monto;
    private LocalDateTime fecha;
    private String        referencia;
    private boolean       estado;

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdCuentaPago() {
        return idCuentaPago;
    }

    public void setIdCuentaPago(int idCuentaPago) {
        this.idCuentaPago = idCuentaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Pago(int idPago, int idCuentaPago, BigDecimal monto, LocalDateTime fecha, String referencia, boolean estado) {
        this.idPago = idPago;
        this.idCuentaPago = idCuentaPago;
        this.monto = monto;
        this.fecha = fecha;
        this.referencia = referencia;
        this.estado = estado;
    }
}

