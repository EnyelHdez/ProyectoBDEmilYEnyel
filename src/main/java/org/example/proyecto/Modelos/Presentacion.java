package org.example.proyecto.Modelos;

public class Presentacion {
    private int idPresentacion;
    private String nombre;
    private String descripcion;
    private boolean estado;

    public int getIdPresentacion() {
        return idPresentacion;
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Presentacion(int idPresentacion, String nombre, String descripcion, boolean estado) {
        this.idPresentacion = idPresentacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
