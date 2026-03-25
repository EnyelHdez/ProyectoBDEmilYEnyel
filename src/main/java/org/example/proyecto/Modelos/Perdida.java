package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Perdida {
    private int idPerdida;
    private int idEmpleado;
    private LocalDateTime fecha;
    private String motivo;
    private BigDecimal totalPerdida;
    private boolean estado;

    public Perdida(int idPerdida, int idEmpleado, LocalDateTime fecha, String motivo, BigDecimal totalPerdida, boolean estado) {
        this.idPerdida = idPerdida;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.motivo = motivo;
        this.totalPerdida = totalPerdida;
        this.estado = estado;
    }

    public int getIdPerdida() {
        return idPerdida;
    }

    public void setIdPerdida(int idPerdida) {
        this.idPerdida = idPerdida;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
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

    public BigDecimal getTotalPerdida() {
        return totalPerdida;
    }

    public void setTotalPerdida(BigDecimal totalPerdida) {
        this.totalPerdida = totalPerdida;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
