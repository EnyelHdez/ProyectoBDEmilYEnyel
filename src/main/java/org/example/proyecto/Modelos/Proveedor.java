package org.example.proyecto.Modelos;

public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String rnc;
    private String telefono;
    private String email;
    private String direccion;

    public Proveedor() {}

    public Proveedor(int idProveedor, String nombre, String rnc, String telefono, String email, String direccion) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.rnc = rnc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRnc() { return rnc; }
    public void setRnc(String rnc) { this.rnc = rnc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return nombre;
    }
}