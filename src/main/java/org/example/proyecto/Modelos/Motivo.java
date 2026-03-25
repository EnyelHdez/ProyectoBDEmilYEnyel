package org.example.proyecto.Modelos;

public class Motivo {
    private int idMotivo;
    private String nombre;
    private String tipo; // VENTA, COMPRA, RECLAMACION
    private boolean estado;

    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Motivo(int idMotivo, String nombre, String tipo, boolean estado) {
        this.idMotivo = idMotivo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
    }
}
