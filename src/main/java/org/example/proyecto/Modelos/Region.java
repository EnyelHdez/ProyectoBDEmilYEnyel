package org.example.proyecto.Modelos;

public class Region {
        private int    idRegion;
        private int    idPais;
        private String nombre;
        private boolean estado;

    public int getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
    }

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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Region(int idRegion, int idPais, String nombre, boolean estado) {
        this.idRegion = idRegion;
        this.idPais = idPais;
        this.nombre = nombre;
        this.estado = estado;
    }
}
