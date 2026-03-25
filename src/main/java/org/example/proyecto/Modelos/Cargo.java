package org.example.proyecto.Modelos;

public class Cargo {
    private int    idCargo;
    private String nombre;
    private String descripcion;
    private boolean estado;

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
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

    public Cargo(boolean estado, String descripcion, String nombre, int idCargo) {
        this.estado = estado;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.idCargo = idCargo;
    }
}
