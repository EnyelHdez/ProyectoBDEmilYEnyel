package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DetalleVenta {
    private int idDetVenta;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal itbis;
    private BigDecimal subtotal;
    private String lote;
    private LocalDate fechaVencimiento;

}
