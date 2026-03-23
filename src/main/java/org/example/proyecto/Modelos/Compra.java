package org.example.proyecto.Modelos;

import java.time.LocalDate;

public class Compra {
    private int idCompra;
    private String numeroOrden;
    private LocalDate fechaCompra;
    private int idProveedor;
    private String nombreProveedor;
    private String metodoPago;
    private String estado;
    private double total;

    public Compra() {}

    public Compra(int idCompra, String numeroOrden, LocalDate fechaCompra, int idProveedor,
                  String metodoPago, String estado, double total) {
        this.idCompra = idCompra;
        this.numeroOrden = numeroOrden;
        this.fechaCompra = fechaCompra;
        this.idProveedor = idProveedor;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.total = total;
    }

    // Getters y Setters
    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}