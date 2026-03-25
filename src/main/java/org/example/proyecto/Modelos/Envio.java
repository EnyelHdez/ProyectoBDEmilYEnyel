package org.example.proyecto.Modelos;

import java.time.LocalDateTime;

public class Envio {
    private int idEnvio;
    private int idPedido;
    private String nroGuia;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
    private String transportista;
    private String observacion;
    private String estado;

    public Envio(int idEnvio, int idPedido, String nroGuia, LocalDateTime fechaDespacho, LocalDateTime fechaEntrega, String transportista, String observacion, String estado) {
        this.idEnvio = idEnvio;
        this.idPedido = idPedido;
        this.nroGuia = nroGuia;
        this.fechaDespacho = fechaDespacho;
        this.fechaEntrega = fechaEntrega;
        this.transportista = transportista;
        this.observacion = observacion;
        this.estado = estado;
    }

    public int getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
        this.idEnvio = idEnvio;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getNroGuia() {
        return nroGuia;
    }

    public void setNroGuia(String nroGuia) {
        this.nroGuia = nroGuia;
    }

    public LocalDateTime getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDateTime fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
