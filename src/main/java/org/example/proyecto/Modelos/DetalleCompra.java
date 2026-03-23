package org.example.proyecto.Modelos;

public class DetalleCompra {
    private int idDetalle;
    private int idCompra;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private String lote;
    private double subtotal;

    public DetalleCompra() {}

    public DetalleCompra(int idProducto, String nombreProducto, int cantidad,
                         double precioUnitario, String lote) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.lote = lote;
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = cantidad * precioUnitario;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}