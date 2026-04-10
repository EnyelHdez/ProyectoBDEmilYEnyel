package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private int idEmpleado;
    private int idMetodoEnvio;
    private Integer idDirEnvio;
    private LocalDateTime fechaPedido;
    private LocalDate fechaEstimada;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    public Pedido(int idPedido, int idCliente, int idEmpleado, int idMetodoEnvio, Integer idDirEnvio, LocalDateTime fechaPedido, LocalDate fechaEstimada, BigDecimal subtotal, BigDecimal costoEnvio, BigDecimal total, String estado) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.idMetodoEnvio = idMetodoEnvio;
        this.idDirEnvio = idDirEnvio;
        this.fechaPedido = fechaPedido;
        this.fechaEstimada = fechaEstimada;
        this.subtotal = subtotal;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.estado = estado;
    }

    public Pedido() {

    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public LocalDate getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDate fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Integer getIdDirEnvio() {
        return idDirEnvio;
    }

    public void setIdDirEnvio(Integer idDirEnvio) {
        this.idDirEnvio = idDirEnvio;
    }

    public int getIdMetodoEnvio() {
        return idMetodoEnvio;
    }

    public void setIdMetodoEnvio(int idMetodoEnvio) {
        this.idMetodoEnvio = idMetodoEnvio;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    private String estado; // PENDIENTE, CONFIRMADO, EN_CAMINO, ENTREGADO, CANCELADO

}
