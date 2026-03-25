package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class PresentacionProducto {
    private int idPresentacionProducto;
    private int idProducto;
    private int idPresentacion;
    private int unidadesContenido;
    private BigDecimal precioVenta;
    private boolean estado;

    public int getIdPresentacionProducto() {
        return idPresentacionProducto;
    }

    public void setIdPresentacionProducto(int idPresentacionProducto) {
        this.idPresentacionProducto = idPresentacionProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public int getUnidadesContenido() {
        return unidadesContenido;
    }

    public void setUnidadesContenido(int unidadesContenido) {
        this.unidadesContenido = unidadesContenido;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public PresentacionProducto(int idPresentacionProducto, int idProducto, int idPresentacion, int unidadesContenido, BigDecimal precioVenta, boolean estado) {
        this.idPresentacionProducto = idPresentacionProducto;
        this.idProducto = idProducto;
        this.idPresentacion = idPresentacion;
        this.unidadesContenido = unidadesContenido;
        this.precioVenta = precioVenta;
        this.estado = estado;
    }
}
