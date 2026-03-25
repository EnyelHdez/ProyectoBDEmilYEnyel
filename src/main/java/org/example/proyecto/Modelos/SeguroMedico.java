package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeguroMedico {
    private int idSeguro;
    private int idArs;
    private int idCliente;
    private String nroAfiliado;
    private String plan;
    private BigDecimal porcentajeCob;
    private BigDecimal limiteAnual;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFin;
    private boolean estado;

    public int getIdSeguro() {
        return idSeguro;
    }

    public void setIdSeguro(int idSeguro) {
        this.idSeguro = idSeguro;
    }

    public int getIdArs() {
        return idArs;
    }

    public void setIdArs(int idArs) {
        this.idArs = idArs;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNroAfiliado() {
        return nroAfiliado;
    }

    public void setNroAfiliado(String nroAfiliado) {
        this.nroAfiliado = nroAfiliado;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public BigDecimal getPorcentajeCob() {
        return porcentajeCob;
    }

    public void setPorcentajeCob(BigDecimal porcentajeCob) {
        this.porcentajeCob = porcentajeCob;
    }

    public BigDecimal getLimiteAnual() {
        return limiteAnual;
    }

    public void setLimiteAnual(BigDecimal limiteAnual) {
        this.limiteAnual = limiteAnual;
    }

    public LocalDate getVigenciaInicio() {
        return vigenciaInicio;
    }

    public void setVigenciaInicio(LocalDate vigenciaInicio) {
        this.vigenciaInicio = vigenciaInicio;
    }

    public LocalDate getVigenciaFin() {
        return vigenciaFin;
    }

    public void setVigenciaFin(LocalDate vigenciaFin) {
        this.vigenciaFin = vigenciaFin;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public SeguroMedico(int idSeguro, int idArs, int idCliente, String nroAfiliado, String plan, BigDecimal porcentajeCob, BigDecimal limiteAnual, LocalDate vigenciaInicio, LocalDate vigenciaFin, boolean estado) {
        this.idSeguro = idSeguro;
        this.idArs = idArs;
        this.idCliente = idCliente;
        this.nroAfiliado = nroAfiliado;
        this.plan = plan;
        this.porcentajeCob = porcentajeCob;
        this.limiteAnual = limiteAnual;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFin = vigenciaFin;
        this.estado = estado;
    }
}
