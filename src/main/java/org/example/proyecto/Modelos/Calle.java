package org.example.proyecto.Modelos;

public class Calle {
    private int    idCalle;
    private int    idSector;
    private String nombre;
    private boolean estado;

    public int getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(int idCalle) {
        this.idCalle = idCalle;
    }

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
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

    public Calle(int idCalle, int idSector, String nombre, boolean estado) {
        this.idCalle = idCalle;
        this.idSector = idSector;
        this.nombre = nombre;
        this.estado = estado;
    }
}
