package org.example.proyecto.Modelos;

public class ARS {
    private int idArs;
    private int idAseguradora;
    private String codigoArs;
    private String nombre;
    private boolean estado;

    public int getIdArs() {
        return idArs;
    }

    public void setIdArs(int idArs) {
        this.idArs = idArs;
    }

    public int getIdAseguradora() {
        return idAseguradora;
    }

    public void setIdAseguradora(int idAseguradora) {
        this.idAseguradora = idAseguradora;
    }

    public String getCodigoArs() {
        return codigoArs;
    }

    public void setCodigoArs(String codigoArs) {
        this.codigoArs = codigoArs;
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

    public ARS(int idArs, int idAseguradora, String codigoArs, String nombre, boolean estado) {
        this.idArs = idArs;
        this.idAseguradora = idAseguradora;
        this.codigoArs = codigoArs;
        this.nombre = nombre;
        this.estado = estado;
    }
}
