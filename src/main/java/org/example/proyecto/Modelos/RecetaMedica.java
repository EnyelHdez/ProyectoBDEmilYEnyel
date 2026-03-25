package org.example.proyecto.Modelos;

import java.time.LocalDate;

public class RecetaMedica {
    private int idReceta;
    private int idCliente;
    private Integer idMedico;
    private Integer idVenta;
    private String nroReceta;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String nombreMedicoExt;
    private String especialidadExt;
    private String observacion;
    private String imagenReceta;
    private boolean estado;

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public String getNroReceta() {
        return nroReceta;
    }

    public void setNroReceta(String nroReceta) {
        this.nroReceta = nroReceta;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getNombreMedicoExt() {
        return nombreMedicoExt;
    }

    public void setNombreMedicoExt(String nombreMedicoExt) {
        this.nombreMedicoExt = nombreMedicoExt;
    }

    public String getEspecialidadExt() {
        return especialidadExt;
    }

    public void setEspecialidadExt(String especialidadExt) {
        this.especialidadExt = especialidadExt;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getImagenReceta() {
        return imagenReceta;
    }

    public void setImagenReceta(String imagenReceta) {
        this.imagenReceta = imagenReceta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public RecetaMedica(int idReceta, int idCliente, Integer idMedico, Integer idVenta, String nroReceta, LocalDate fechaEmision, LocalDate fechaVencimiento, String nombreMedicoExt, String especialidadExt, String observacion, String imagenReceta, boolean estado) {
        this.idReceta = idReceta;
        this.idCliente = idCliente;
        this.idMedico = idMedico;
        this.idVenta = idVenta;
        this.nroReceta = nroReceta;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.nombreMedicoExt = nombreMedicoExt;
        this.especialidadExt = especialidadExt;
        this.observacion = observacion;
        this.imagenReceta = imagenReceta;
        this.estado = estado;
    }
}
