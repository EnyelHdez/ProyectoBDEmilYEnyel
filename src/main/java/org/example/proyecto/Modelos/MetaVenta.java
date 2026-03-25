package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MetaVenta {
    private int idMetaVenta;
    private String nombre;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private BigDecimal montoMeta;
    private boolean estado;

    public MetaVenta(int idMetaVenta, String nombre, LocalDate periodoInicio, LocalDate periodoFin, BigDecimal montoMeta, boolean estado) {
        this.idMetaVenta = idMetaVenta;
        this.nombre = nombre;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.montoMeta = montoMeta;
        this.estado = estado;
    }

    public int getIdMetaVenta() {
        return idMetaVenta;
    }

    public void setIdMetaVenta(int idMetaVenta) {
        this.idMetaVenta = idMetaVenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public BigDecimal getMontoMeta() {
        return montoMeta;
    }

    public void setMontoMeta(BigDecimal montoMeta) {
        this.montoMeta = montoMeta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
