package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdenCompra {
    private int idOrden;
    private int idProveedor;
    private String nombreProveedor;
    private int idEmpleado;
    private String nombreEmpleado;
    private LocalDateTime fechaOrden;
    private LocalDateTime fechaEntrega;
    private String condicionPago;
    private String observaciones;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String estado;
    private int cantidad_productos;
    private String productos_orden;

    public OrdenCompra(int idOrden, int idProveedor, String nombreProveedor, int idEmpleado, String nombreEmpleado, LocalDateTime fechaOrden, LocalDateTime fechaEntrega, String condicionPago, String observaciones, BigDecimal subtotal, BigDecimal total, String estado, int cantidad_productos, String productos_orden) {
        this.idOrden = idOrden;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.fechaOrden = fechaOrden;
        this.fechaEntrega = fechaEntrega;
        this.condicionPago = condicionPago;
        this.observaciones = observaciones;
        this.subtotal = subtotal;
        this.total = total;
        this.estado = estado;
        this.cantidad_productos = cantidad_productos;
        this.productos_orden = productos_orden;
    }

    public int getCantidad_productos() {
        return cantidad_productos;
    }

    public void setCantidad_productos(int cantidad_productos) {
        this.cantidad_productos = cantidad_productos;
    }

    public String getProductos_orden() {
        return productos_orden;
    }

    public void setProductos_orden(String productos_orden) {
        this.productos_orden = productos_orden;
    }

    public OrdenCompra() {}

    // Getters y Setters
    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public LocalDateTime getFechaOrden() { return fechaOrden; }
    public void setFechaOrden(LocalDateTime fechaOrden) { this.fechaOrden = fechaOrden; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}