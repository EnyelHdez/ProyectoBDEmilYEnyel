package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Devolucion {
    private int idDevolucion;
    private int idProducto;
    private int idVenta;
    private int idEmpleado;
    private int idMotivo;
    private Integer idNotaCredito;          // renombrado de idComprobante
    private LocalDateTime fecha;
    private BigDecimal montoDevuelto;
    private String observacion;
    private String estado;

    // Campos auxiliares para mostrar en tablas (no están en la BD)
    private String motivoNombre;
    private String fechaTexto;
    private String nombreEmpleado;
    private String nombreProducto;
    private BigDecimal montoNotaCredito;    // ← NUEVO: monto de la nota de crédito

    // Constructor vacío
    public Devolucion() {}

    // Constructor completo
    public Devolucion(int idDevolucion, int idProducto, int idVenta, int idEmpleado,
                      int idMotivo, Integer idNotaCredito, LocalDateTime fecha,
                      BigDecimal montoDevuelto, String observacion, String estado,
                      String motivoNombre, String fechaTexto, String nombreEmpleado) {
        this.idDevolucion   = idDevolucion;
        this.idProducto     = idProducto;
        this.idVenta        = idVenta;
        this.idEmpleado     = idEmpleado;
        this.idMotivo       = idMotivo;
        this.idNotaCredito  = idNotaCredito;
        this.fecha          = fecha;
        this.montoDevuelto  = montoDevuelto;
        this.observacion    = observacion;
        this.estado         = estado;
        this.motivoNombre   = motivoNombre;
        this.fechaTexto     = fechaTexto;
        this.nombreEmpleado = nombreEmpleado;
    }

    // ── Getters y Setters ────────────────────────────────────────────────

    public int getIdDevolucion()                        { return idDevolucion; }
    public void setIdDevolucion(int v)                  { this.idDevolucion = v; }

    public int getIdProducto()                          { return idProducto; }
    public void setIdProducto(int v)                    { this.idProducto = v; }

    public int getIdVenta()                             { return idVenta; }
    public void setIdVenta(int v)                       { this.idVenta = v; }

    public int getIdEmpleado()                          { return idEmpleado; }
    public void setIdEmpleado(int v)                    { this.idEmpleado = v; }

    public int getIdMotivo()                            { return idMotivo; }
    public void setIdMotivo(int v)                      { this.idMotivo = v; }

    /** ID de la nota de crédito asociada (puede ser null) */
    public Integer getIdNotaCredito()                   { return idNotaCredito; }
    public void setIdNotaCredito(Integer v)             { this.idNotaCredito = v; }

    /** @deprecated Alias mantenido por compatibilidad con código existente */
    @Deprecated
    public Integer getIdComprobante()                   { return idNotaCredito; }
    @Deprecated
    public void setIdComprobante(Integer v)             { this.idNotaCredito = v; }

    public LocalDateTime getFecha()                     { return fecha; }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        if (fecha != null)
            this.fechaTexto = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public BigDecimal getMontoDevuelto()                { return montoDevuelto; }
    public void setMontoDevuelto(BigDecimal v)          { this.montoDevuelto = v; }

    public String getObservacion()                      { return observacion; }
    public void setObservacion(String v)                { this.observacion = v; }

    public String getEstado()                           { return estado; }
    public void setEstado(String v)                     { this.estado = v; }

    public String getMotivoNombre()                     { return motivoNombre; }
    public void setMotivoNombre(String v)               { this.motivoNombre = v; }

    public String getFechaTexto()                       { return fechaTexto; }
    public void setFechaTexto(String v)                 { this.fechaTexto = v; }

    public String getNombreEmpleado()                   { return nombreEmpleado; }
    public void setNombreEmpleado(String v)             { this.nombreEmpleado = v; }

    public String getNombreProducto()                   { return nombreProducto; }
    public void setNombreProducto(String v)             { this.nombreProducto = v; }

    /** Monto de la nota de crédito (campo auxiliar, viene del JOIN) */
    public BigDecimal getMontoNotaCredito()             { return montoNotaCredito; }
    public void setMontoNotaCredito(BigDecimal v)       { this.montoNotaCredito = v; }
}