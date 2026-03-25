package org.example.proyecto.Modelos;

public class Proveedor {
    private int idProveedor;
    private String razonSocial;
    private String nombreComercial;
    private String rnc;
    private String telefono;
    private String email;
    private String contacto;
    private String direccion;
    private String estado_temp; // Cambiado de boolean a String

    // Constructores
    public Proveedor() {}

    public Proveedor(int idProveedor, String razonSocial, String nombreComercial,
                     String rnc, String telefono, String email, String contacto,
                     String direccion, String estado_temp) {
        this.idProveedor = idProveedor;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.rnc = rnc;
        this.telefono = telefono;
        this.email = email;
        this.contacto = contacto;
        this.direccion = direccion;
        this.estado_temp = estado_temp;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRnc() { return rnc; }
    public void setRnc(String rnc) { this.rnc = rnc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstadoTexto() { return estado_temp; }
    public void setEstadoTexto(String estadoTexto) { this.estado_temp = estadoTexto; }

}