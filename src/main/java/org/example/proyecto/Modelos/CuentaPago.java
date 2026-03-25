package org.example.proyecto.Modelos;

public class CuentaPago {
    private int idCuentaPago;
    private String nombre;
    private String descripcion;
    private boolean estado;

    public CuentaPago(int idCuentaPago, String nombre, String descripcion, boolean estado) {
        this.idCuentaPago = idCuentaPago;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdCuentaPago() {
        return idCuentaPago;
    }

    public void setIdCuentaPago(int idCuentaPago) {
        this.idCuentaPago = idCuentaPago;
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
}
