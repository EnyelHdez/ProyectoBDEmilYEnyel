package org.example.proyecto.Modelos;

public class ReclamacionCompra {
    private int idReclamacionCompra;
    private int idReclamacion;

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdReclamacion() {
        return idReclamacion;
    }

    public void setIdReclamacion(int idReclamacion) {
        this.idReclamacion = idReclamacion;
    }

    public int getIdReclamacionCompra() {
        return idReclamacionCompra;
    }

    public void setIdReclamacionCompra(int idReclamacionCompra) {
        this.idReclamacionCompra = idReclamacionCompra;
    }

    private int idCompra;

    public ReclamacionCompra(int idReclamacionCompra, int idReclamacion, int idCompra) {
        this.idReclamacionCompra = idReclamacionCompra;
        this.idReclamacion = idReclamacion;
        this.idCompra = idCompra;
    }
}
