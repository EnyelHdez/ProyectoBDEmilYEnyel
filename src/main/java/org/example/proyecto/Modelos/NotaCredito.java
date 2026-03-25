package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NotaCredito {
    private int idNotaCredito;
    private Integer idComprobante;
    private String numero;
    private LocalDateTime fecha;
    private String motivo;
    private BigDecimal monto;

    public int getIdNotaCredito() {
        return idNotaCredito;
    }

    public void setIdNotaCredito(int idNotaCredito) {
        this.idNotaCredito = idNotaCredito;
    }

    public Integer getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Integer idComprobante) {
        this.idComprobante = idComprobante;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public NotaCredito(int idNotaCredito, Integer idComprobante, String numero, LocalDateTime fecha, String motivo, BigDecimal monto, String estado) {
        this.idNotaCredito = idNotaCredito;
        this.idComprobante = idComprobante;
        this.numero = numero;
        this.fecha = fecha;
        this.motivo = motivo;
        this.monto = monto;
        this.estado = estado;
    }

    private String estado; // ACTIVA, APLICADA, ANULADA

}
