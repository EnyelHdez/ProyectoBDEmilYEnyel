package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class SistemaFidelizacion {
    private int idFidelizacion;
    private String nombre;
    private BigDecimal puntosPorPeso;
    private BigDecimal valorPunto;
    private int minimoCanje;
    private String descripcion;
    private boolean estado;

    public SistemaFidelizacion(int idFidelizacion, String nombre, BigDecimal puntosPorPeso, BigDecimal valorPunto, int minimoCanje, String descripcion, boolean estado) {
        this.idFidelizacion = idFidelizacion;
        this.nombre = nombre;
        this.puntosPorPeso = puntosPorPeso;
        this.valorPunto = valorPunto;
        this.minimoCanje = minimoCanje;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdFidelizacion() {
        return idFidelizacion;
    }

    public void setIdFidelizacion(int idFidelizacion) {
        this.idFidelizacion = idFidelizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPuntosPorPeso() {
        return puntosPorPeso;
    }

    public void setPuntosPorPeso(BigDecimal puntosPorPeso) {
        this.puntosPorPeso = puntosPorPeso;
    }

    public BigDecimal getValorPunto() {
        return valorPunto;
    }

    public void setValorPunto(BigDecimal valorPunto) {
        this.valorPunto = valorPunto;
    }

    public int getMinimoCanje() {
        return minimoCanje;
    }

    public void setMinimoCanje(int minimoCanje) {
        this.minimoCanje = minimoCanje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
