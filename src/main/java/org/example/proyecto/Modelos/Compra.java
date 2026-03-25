package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public class Compra {
    private int           idCompra;
    private int           idProveedor;
    private int           idEmpleado;
    private Integer       idComprobante;
    private LocalDateTime fecha;
    private String        nroFacturaProv;
    private BigDecimal    subtotal;
    private BigDecimal    descuento;
    private BigDecimal    itbis;
    private BigDecimal    total;
    private String        estado;   // PENDIENTE | RECIBIDA | ANULADA
    public Compra() {}

    public Compra(int idCompra, int idProveedor, int idEmpleado,
                  int idComprobante, LocalDateTime fecha,
                  String nroFacturaProv, BigDecimal subtotal,
                  BigDecimal descuento, BigDecimal itbis,
                  BigDecimal total, String estado) {
        this.idCompra       = idCompra;
        this.idProveedor    = idProveedor;
        this.idEmpleado     = idEmpleado;
        this.idComprobante  = idComprobante;
        this.fecha          = fecha;
        this.nroFacturaProv = nroFacturaProv;
        this.subtotal       = subtotal;
        this.descuento      = descuento;
        this.itbis          = itbis;
        this.total          = total;
        this.estado         = this.estado;
    }



    public int getIdCompra()                        { return idCompra; }
    public void setIdCompra(int v)                  { this.idCompra = v; }

    public int getIdProveedor()                     { return idProveedor; }
    public void setIdProveedor(int v)               { this.idProveedor = v; }

    public int getIdEmpleado()                      { return idEmpleado; }
    public void setIdEmpleado(int v)                { this.idEmpleado = v; }

    public Integer getIdComprobante()               { return idComprobante; }
    public void setIdComprobante(Integer v)         { this.idComprobante = v; }

    public LocalDateTime getFecha()                 { return fecha; }
    public void setFecha(LocalDateTime v)           { this.fecha = v; }

    public String getNroFacturaProv()               { return nroFacturaProv; }
    public void setNroFacturaProv(String v)         { this.nroFacturaProv = v; }

    public BigDecimal getSubtotal()                 { return subtotal; }
    public void setSubtotal(BigDecimal v)           { this.subtotal = v; }

    public BigDecimal getDescuento()                { return descuento; }
    public void setDescuento(BigDecimal v)          { this.descuento = v; }

    public BigDecimal getItbis()                    { return itbis; }
    public void setItbis(BigDecimal v)              { this.itbis = v; }

    public BigDecimal getTotal()                    { return total; }
    public void setTotal(BigDecimal v)              { this.total = v; }

    public String getEstado()                       { return estado; }
    public void setEstado(String v)                 { this.estado = v; }
}

