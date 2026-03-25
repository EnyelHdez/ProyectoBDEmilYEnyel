package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class MetodoEnvio {
    private int idMetodoEnvio;
    private String nombre;
    private String descripcion;
    private BigDecimal costoBase;
    private boolean estado;

    public int getIdMetodoEnvio() {
        return idMetodoEnvio;
    }

    public void setIdMetodoEnvio(int idMetodoEnvio) {
        this.idMetodoEnvio = idMetodoEnvio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCostoBase() {
        return costoBase;
    }

    public void setCostoBase(BigDecimal costoBase) {
        this.costoBase = costoBase;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public MetodoEnvio(int idMetodoEnvio, String nombre, String descripcion, BigDecimal costoBase, boolean estado) {
        this.idMetodoEnvio = idMetodoEnvio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoBase = costoBase;
        this.estado = estado;
    }
}
