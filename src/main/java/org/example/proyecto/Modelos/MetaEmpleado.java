package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class MetaEmpleado {
    private int idMetaEmpleado;
    private int idEmpleado;
    private int idMetaVenta;
    private BigDecimal montoAsignado;
    private BigDecimal montoAlcanzado;
    private boolean estado;

    public int getIdMetaEmpleado() {
        return idMetaEmpleado;
    }

    public void setIdMetaEmpleado(int idMetaEmpleado) {
        this.idMetaEmpleado = idMetaEmpleado;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdMetaVenta() {
        return idMetaVenta;
    }

    public void setIdMetaVenta(int idMetaVenta) {
        this.idMetaVenta = idMetaVenta;
    }

    public BigDecimal getMontoAsignado() {
        return montoAsignado;
    }

    public void setMontoAsignado(BigDecimal montoAsignado) {
        this.montoAsignado = montoAsignado;
    }

    public BigDecimal getMontoAlcanzado() {
        return montoAlcanzado;
    }

    public void setMontoAlcanzado(BigDecimal montoAlcanzado) {
        this.montoAlcanzado = montoAlcanzado;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public MetaEmpleado(int idMetaEmpleado, int idEmpleado, int idMetaVenta, BigDecimal montoAsignado, BigDecimal montoAlcanzado, boolean estado) {
        this.idMetaEmpleado = idMetaEmpleado;
        this.idEmpleado = idEmpleado;
        this.idMetaVenta = idMetaVenta;
        this.montoAsignado = montoAsignado;
        this.montoAlcanzado = montoAlcanzado;
        this.estado = estado;
    }
}
