package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class VentaDevolucion {
    private int        idVentaDevolucion;
    private int        idDevolucion;
    private int        idProducto;
    private int        cantidad;
    private BigDecimal precioUnitario;

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdVentaDevolucion() {
        return idVentaDevolucion;
    }

    public void setIdVentaDevolucion(int idVentaDevolucion) {
        this.idVentaDevolucion = idVentaDevolucion;
    }

    private BigDecimal subtotal;

    public VentaDevolucion(int idVentaDevolucion, int idDevolucion, int idProducto, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.idVentaDevolucion = idVentaDevolucion;
        this.idDevolucion = idDevolucion;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
}
