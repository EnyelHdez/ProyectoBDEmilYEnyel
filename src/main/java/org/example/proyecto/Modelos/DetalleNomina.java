package org.example.proyecto.Modelos;

import java.math.BigDecimal;

public class DetalleNomina {
    private int idDetNomina;
    private int idNomina;
    private String concepto;
    private char tipo; // I=Ingreso, D=Deduccion
    private BigDecimal monto;

    public int getIdDetNomina() {
        return idDetNomina;
    }

    public void setIdDetNomina(int idDetNomina) {
        this.idDetNomina = idDetNomina;
    }

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public DetalleNomina(int idDetNomina, int idNomina, String concepto, char tipo, BigDecimal monto) {
        this.idDetNomina = idDetNomina;
        this.idNomina = idNomina;
        this.concepto = concepto;
        this.tipo = tipo;
        this.monto = monto;
    }
}
