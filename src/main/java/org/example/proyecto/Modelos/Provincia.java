package org.example.proyecto.Modelos;

public class Provincia {
    private int    idProvincia;
    private int    idRegion;
    private String nombre;
    private boolean estado;

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    public int getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
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

    public Provincia(int idProvincia, int idRegion, String nombre, boolean estado) {
        this.idProvincia = idProvincia;
        this.idRegion = idRegion;
        this.nombre = nombre;
        this.estado = estado;
    }
}
