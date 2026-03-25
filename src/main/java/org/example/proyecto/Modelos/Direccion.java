package org.example.proyecto.Modelos;

public class Direccion {
    private int    idDireccion;
    private int    idCalle;
    private String numero;
    private String referencia;
    private boolean estado;

    public Direccion(int idDireccion, int idCalle, String numero, String referencia, boolean estado) {
        this.idDireccion = idDireccion;
        this.idCalle = idCalle;
        this.numero = numero;
        this.referencia = referencia;
        this.estado = estado;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public int getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(int idCalle) {
        this.idCalle = idCalle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
