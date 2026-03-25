package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DevolucionCompra {
    private int           idDevolucionCompra;
    private int           idCompra;
    private int           idEmpleado;
    private int           idMotivo;
    private LocalDateTime fecha;
    private BigDecimal montoDevuelto;
    private String        observacion;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getMontoDevuelto() {
        return montoDevuelto;
    }

    public void setMontoDevuelto(BigDecimal montoDevuelto) {
        this.montoDevuelto = montoDevuelto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdDevolucionCompra() {
        return idDevolucionCompra;
    }

    public void setIdDevolucionCompra(int idDevolucionCompra) {
        this.idDevolucionCompra = idDevolucionCompra;
    }

    public DevolucionCompra(int idDevolucionCompra, int idCompra, int idEmpleado, int idMotivo, LocalDateTime fecha, BigDecimal montoDevuelto, String observacion, String estado) {
        this.idDevolucionCompra = idDevolucionCompra;
        this.idCompra = idCompra;
        this.idEmpleado = idEmpleado;
        this.idMotivo = idMotivo;
        this.fecha = fecha;
        this.montoDevuelto = montoDevuelto;
        this.observacion = observacion;
        this.estado = estado;
    }

    private String        estado;   // PROCESADA | ANULADA

}
