package org.example.proyecto.Modelos;

public class DescuentoProducto {
    private int idDescuentoProducto;
    private int idDescuento;
    private int idProducto;

    public DescuentoProducto(int idDescuentoProducto, int idDescuento, int idProducto) {
        this.idDescuentoProducto = idDescuentoProducto;
        this.idDescuento = idDescuento;
        this.idProducto = idProducto;
    }

    public int getIdDescuentoProducto() {
        return idDescuentoProducto;
    }

    public void setIdDescuentoProducto(int idDescuentoProducto) {
        this.idDescuentoProducto = idDescuentoProducto;
    }

    public int getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(int idDescuento) {
        this.idDescuento = idDescuento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
