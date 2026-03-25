package org.example.proyecto.Modelos;

public class Distrito {
    private int    idDistrito;
    private int    idProvincia;
    private String nombre;
    private boolean estado;

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
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

    public Distrito(int idDistrito, int idProvincia, String nombre, boolean estado) {
        this.idDistrito = idDistrito;
        this.idProvincia = idProvincia;
        this.nombre = nombre;
        this.estado = estado;
    }
}
