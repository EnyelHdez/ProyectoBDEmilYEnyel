package org.example.proyecto.Modelos;

public class Pais {

        private int  idPais;
        private String nombre;
        private String codigoIso;
        private boolean estado;

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoIso() {
        return codigoIso;
    }

    public void setCodigoIso(String codigoIso) {
        this.codigoIso = codigoIso;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Pais(int idPais, String nombre, String codigoIso, boolean estado) {
        this.idPais = idPais;
        this.nombre = nombre;
        this.codigoIso = codigoIso;
        this.estado = estado;
    }
}
