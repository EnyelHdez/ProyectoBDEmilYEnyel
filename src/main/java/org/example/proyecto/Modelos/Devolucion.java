package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Devolucion {
    private int           idDevolucion;
    private int           idVenta;
    private int           idEmpleado;
    private int           idMotivo;
    private Integer       idComprobante;
    private LocalDateTime fecha;
    private BigDecimal montoDevuelto;

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public Integer getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Integer idComprobante) {
        this.idComprobante = idComprobante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMontoDevuelto() {
        return montoDevuelto;
    }

    public void setMontoDevuelto(BigDecimal montoDevuelto) {
        this.montoDevuelto = montoDevuelto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    private String        observacion;

    public Devolucion(int idDevolucion, int idVenta, int idEmpleado, int idMotivo, Integer idComprobante, LocalDateTime fecha, BigDecimal montoDevuelto, String observacion) {
        this.idDevolucion = idDevolucion;
        this.idVenta = idVenta;
        this.idEmpleado = idEmpleado;
        this.idMotivo = idMotivo;
        this.idComprobante = idComprobante;
        this.fecha = fecha;
        this.montoDevuelto = montoDevuelto;
        this.observacion = observacion;
    }
}
