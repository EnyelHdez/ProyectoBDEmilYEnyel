package org.example.proyecto.Modelos;

public class TipoComprobanteFiscal {
    private int idTipoNcf;
    private String codigo;
    private String descripcion;
    private boolean aplicaCredito;
    private boolean aplicaConsumidor;
    private boolean estado;

    public TipoComprobanteFiscal(int idTipoNcf, String codigo, String descripcion, boolean aplicaCredito, boolean aplicaConsumidor, boolean estado) {
        this.idTipoNcf = idTipoNcf;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.aplicaCredito = aplicaCredito;
        this.aplicaConsumidor = aplicaConsumidor;
        this.estado = estado;
    }

    public int getIdTipoNcf() {
        return idTipoNcf;
    }

    public void setIdTipoNcf(int idTipoNcf) {
        this.idTipoNcf = idTipoNcf;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isAplicaCredito() {
        return aplicaCredito;
    }

    public void setAplicaCredito(boolean aplicaCredito) {
        this.aplicaCredito = aplicaCredito;
    }

    public boolean isAplicaConsumidor() {
        return aplicaConsumidor;
    }

    public void setAplicaConsumidor(boolean aplicaConsumidor) {
        this.aplicaConsumidor = aplicaConsumidor;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
