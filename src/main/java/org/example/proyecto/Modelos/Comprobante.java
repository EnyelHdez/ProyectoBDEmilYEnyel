package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Comprobante {
    private int idComprobante;
    private int idTipoNcf;
    private String ncf;
    private LocalDateTime fechaEmision;
    private BigDecimal montoGravado;
    private BigDecimal itbis;
    private BigDecimal montoTotal;

    public Comprobante(int idComprobante, int idTipoNcf, String ncf, LocalDateTime fechaEmision, BigDecimal montoGravado, BigDecimal itbis, BigDecimal montoTotal, String estado) {
        this.idComprobante = idComprobante;
        this.idTipoNcf = idTipoNcf;
        this.ncf = ncf;
        this.fechaEmision = fechaEmision;
        this.montoGravado = montoGravado;
        this.itbis = itbis;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getItbis() {
        return itbis;
    }

    public void setItbis(BigDecimal itbis) {
        this.itbis = itbis;
    }

    public BigDecimal getMontoGravado() {
        return montoGravado;
    }

    public void setMontoGravado(BigDecimal montoGravado) {
        this.montoGravado = montoGravado;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
    }

    public int getIdTipoNcf() {
        return idTipoNcf;
    }

    public void setIdTipoNcf(int idTipoNcf) {
        this.idTipoNcf = idTipoNcf;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    private String estado; // EMITIDO, ANULADO

}
