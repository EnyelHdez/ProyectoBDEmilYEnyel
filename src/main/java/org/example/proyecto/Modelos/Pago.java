package org.example.proyecto.Modelos;

import java.time.LocalDate;

public class Pago {
    private int idPago;
    private int idProveedor;
    private String nombreProveedor;
    private String numeroFactura;
    private LocalDate fechaFactura;
    private LocalDate fechaPago;
    private String metodoPago;
    private double monto;
    private String estado;

    public Pago() {}

    public Pago(int idPago, int idProveedor, String numeroFactura, LocalDate fechaFactura,
                LocalDate fechaPago, String metodoPago, double monto, String estado) {
        this.idPago = idPago;
        this.idProveedor = idProveedor;
        this.numeroFactura = numeroFactura;
        this.fechaFactura = fechaFactura;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}