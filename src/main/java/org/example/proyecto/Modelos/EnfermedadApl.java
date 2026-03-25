package org.example.proyecto.Modelos;

public class EnfermedadApl {
    private int idEnfermedad;
    private String nombre;
    private String codigoCie10;
    private String descripcion;
    private boolean estado;

    public int getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(int idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoCie10() {
        return codigoCie10;
    }

    public void setCodigoCie10(String codigoCie10) {
        this.codigoCie10 = codigoCie10;
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

    public EnfermedadApl(int idEnfermedad, String nombre, String codigoCie10, String descripcion, boolean estado) {
        this.idEnfermedad = idEnfermedad;
        this.nombre = nombre;
        this.codigoCie10 = codigoCie10;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
