package org.example.proyecto.Modelos;

public class DetalleCategoria {
    private int idDetalleCategoria;
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private boolean estado;

    public int getIdDetalleCategoria() {
        return idDetalleCategoria;
    }

    public void setIdDetalleCategoria(int idDetalleCategoria) {
        this.idDetalleCategoria = idDetalleCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
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

    public DetalleCategoria(int idDetalleCategoria, int idCategoria, String nombre, String descripcion, boolean estado) {
        this.idDetalleCategoria = idDetalleCategoria;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
