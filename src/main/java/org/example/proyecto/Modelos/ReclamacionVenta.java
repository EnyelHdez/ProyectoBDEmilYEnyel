package org.example.proyecto.Modelos;

public class ReclamacionVenta {
    private int idReclVenta;
    private int idReclamacion;
    private int idVenta;

    public int getIdReclVenta() {
        return idReclVenta;
    }

    public void setIdReclVenta(int idReclVenta) {
        this.idReclVenta = idReclVenta;
    }

    public int getIdReclamacion() {
        return idReclamacion;
    }

    public void setIdReclamacion(int idReclamacion) {
        this.idReclamacion = idReclamacion;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public ReclamacionVenta(int idVenta, int idReclamacion, int idReclVenta) {
        this.idVenta = idVenta;
        this.idReclamacion = idReclamacion;
        this.idReclVenta = idReclVenta;
    }
}
