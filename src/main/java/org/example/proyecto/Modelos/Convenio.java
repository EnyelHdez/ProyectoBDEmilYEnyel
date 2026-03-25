package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Convenio {
    private int idConvenio;
    private int idArs;
    private String nombre;
    private String descripcion;
    private BigDecimal porcentajeCob;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean estado;

    public int getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(int idConvenio) {
        this.idConvenio = idConvenio;
    }

    public int getIdArs() {
        return idArs;
    }

    public void setIdArs(int idArs) {
        this.idArs = idArs;
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

    public BigDecimal getPorcentajeCob() {
        return porcentajeCob;
    }

    public void setPorcentajeCob(BigDecimal porcentajeCob) {
        this.porcentajeCob = porcentajeCob;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Convenio(int idConvenio, int idArs, String nombre, String descripcion, BigDecimal porcentajeCob, LocalDate fechaInicio, LocalDate fechaFin, boolean estado) {
        this.idConvenio = idConvenio;
        this.idArs = idArs;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porcentajeCob = porcentajeCob;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }
}
