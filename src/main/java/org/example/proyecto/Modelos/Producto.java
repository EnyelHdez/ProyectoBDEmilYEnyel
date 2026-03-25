package org.example.proyecto.Modelos;

public class Producto {

    private int     idProducto;
    private int     idCategoria;
    private Integer idProveedor;        // null en BD
    private String  codigoBarra;        // codigo_barra (sin 's')
    private String  nombre;
    private String  descripcion;        // null en BD
    private double  precioCosto;
    private double  precioVenta;
    private int     stockActual;        // stock_actual
    private int     stockMinimo;        // stock_minimo
    private boolean requiereReceta;     // bit
    private boolean aplicaItbis;        // aplica_itbis (bit)
    private double  porcentajeItbis;    // decimal(5,2)
    private boolean estado;             // bit

    // Campo auxiliar (JOIN con proveedor, no viene directo de PRODUCTO)
    private String nombreProveedor;
    // Campo auxiliar (JOIN con categoria)
    private String nombreCategoria;


    public Producto() {}


    public Producto(int idProducto, int idCategoria, Integer idProveedor,
                    String codigoBarra, String nombre, String descripcion,
                    double precioCosto, double precioVenta,
                    int stockActual, int stockMinimo,
                    boolean requiereReceta, boolean aplicaItbis,
                    double porcentajeItbis, boolean estado) {
        this.idProducto      = idProducto;
        this.idCategoria     = idCategoria;
        this.idProveedor     = idProveedor;
        this.codigoBarra     = codigoBarra;
        this.nombre          = nombre;
        this.descripcion     = descripcion;
        this.precioCosto     = precioCosto;
        this.precioVenta     = precioVenta;
        this.stockActual     = stockActual;
        this.stockMinimo     = stockMinimo;
        this.requiereReceta  = requiereReceta;
        this.aplicaItbis     = aplicaItbis;
        this.porcentajeItbis = porcentajeItbis;
        this.estado          = estado;
    }

    // ── Getters & Setters ────────────────────────────────────

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    // Integer (nullable) para id_proveedor que admite NULL en BD
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }

    // PropertyValueFactory("codigoBarra") → getCodigoBarra()
    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // PropertyValueFactory("precioCosto") → getPrecioCosto()
    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    // PropertyValueFactory("stockActual") → getStockActual()
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public boolean isRequiereReceta() { return requiereReceta; }
    public void setRequiereReceta(boolean requiereReceta) { this.requiereReceta = requiereReceta; }

    public boolean isAplicaItbis() { return aplicaItbis; }
    public void setAplicaItbis(boolean aplicaItbis) { this.aplicaItbis = aplicaItbis; }

    public double getPorcentajeItbis() { return porcentajeItbis; }
    public void setPorcentajeItbis(double porcentajeItbis) { this.porcentajeItbis = porcentajeItbis; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    // ── Auxiliares (JOIN) ────────────────────────────────────

    // PropertyValueFactory("nombreProveedor") → getNombreProveedor()
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    // PropertyValueFactory("nombreCategoria") → getNombreCategoria()
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    @Override
    public String toString() { return nombre; }
}