package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venta {
    private int           idVenta;
    private Integer       idCliente;
    private int           idEmpleado;
    private Integer       idComprobante;
    private Integer       idProducto;      // ← NUEVO
    private LocalDateTime fecha;
    private BigDecimal    subtotal;
    private BigDecimal    descuento;
    private BigDecimal    itbis;
    private BigDecimal    total;
    private String        estado;          // COMPLETADA | ANULADA | PENDIENTE

    // Campo auxiliar para mostrar en tabla (no está en la BD)
    private String nombreProducto;         // ← NUEVO

    public Venta() {}

    public Venta(int idVenta, Integer idCliente, int idEmpleado, Integer idComprobante,
                 LocalDateTime fecha, BigDecimal subtotal, BigDecimal descuento,
                 BigDecimal itbis, BigDecimal total, String estado) {
        this.idVenta       = idVenta;
        this.idCliente     = idCliente;
        this.idEmpleado    = idEmpleado;
        this.idComprobante = idComprobante;
        this.fecha         = fecha;
        this.subtotal      = subtotal;
        this.descuento     = descuento;
        this.itbis         = itbis;
        this.total         = total;
        this.estado        = estado;
    }

    // ── Getters y Setters ────────────────────────────────────────────────

    public int getIdVenta()                             { return idVenta; }
    public void setIdVenta(int v)                       { this.idVenta = v; }

    public Integer getIdCliente()                       { return idCliente; }
    public void setIdCliente(Integer v)                 { this.idCliente = v; }

    public Integer getIdEmpleado()                      { return idEmpleado; }
    public void setIdEmpleado(int v)                    { this.idEmpleado = v; }

    public Integer getIdComprobante()                   { return idComprobante; }
    public void setIdComprobante(Integer v)             { this.idComprobante = v; }

    /** ID del producto involucrado en esta venta (puede ser null) */
    public Integer getIdProducto()                      { return idProducto; }
    public void setIdProducto(Integer v)                { this.idProducto = v; }

    public LocalDateTime getFecha()                     { return fecha; }
    public void setFecha(LocalDateTime v)               { this.fecha = v; }

    public BigDecimal getSubtotal()                     { return subtotal; }
    public void setSubtotal(BigDecimal v)               { this.subtotal = v; }

    public BigDecimal getDescuento()                    { return descuento; }
    public void setDescuento(BigDecimal v)              { this.descuento = v; }

    public BigDecimal getItbis()                        { return itbis; }
    public void setItbis(BigDecimal v)                  { this.itbis = v; }

    public BigDecimal getTotal()                        { return total; }
    public void setTotal(BigDecimal v)                  { this.total = v; }

    public String getEstado()                           { return estado; }
    public void setEstado(String v)                     { this.estado = v; }

    /** Nombre del producto (campo auxiliar, viene del JOIN) */
    public String getNombreProducto()                   { return nombreProducto; }
    public void setNombreProducto(String v)             { this.nombreProducto = v; }
}