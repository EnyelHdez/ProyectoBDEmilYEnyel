package org.example.proyecto.Modelos;

public class Sector {
    private int idSector;
    private int idDistrito;
    private String nombre;

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
    }

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
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

    public Sector(int idSector, int idDistrito, String nombre, boolean estado) {
        this.idSector = idSector;
        this.idDistrito = idDistrito;
        this.nombre = nombre;
        this.estado = estado;
    }

    private boolean estado;

}
