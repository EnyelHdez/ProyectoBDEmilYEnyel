package org.example.proyecto.Modelos;

public class Producto {
    private int idProducto;
    private String nombre;
    private String codigoBarras;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private int idProveedor;
    private String nombreProveedor;

    public Producto() {}

    public Producto(int idProducto, String nombre, String codigoBarras, double precioCompra,
                    double precioVenta, int stock, int idProveedor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.codigoBarras = codigoBarras;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    @Override
    public String toString() {
        return nombre;
    }
}