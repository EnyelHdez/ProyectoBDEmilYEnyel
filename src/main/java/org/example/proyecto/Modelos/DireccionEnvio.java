package org.example.proyecto.Modelos;

public class DireccionEnvio {
    private int    idDireccionEnvio;
    private int    idCalle;
    private String numero;
    private String referencia;
    private String destinatario;
    private String telefonoContacto;
    private boolean estado;

    public int getIdDireccionEnvio() {
        return idDireccionEnvio;
    }

    public void setIdDireccionEnvio(int idDireccionEnvio) {
        this.idDireccionEnvio = idDireccionEnvio;
    }

    public int getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(int idCalle) {
        this.idCalle = idCalle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public DireccionEnvio(int idDireccionEnvio, int idCalle, String numero, String referencia, String destinatario, String telefonoContacto, boolean estado) {
        this.idDireccionEnvio = idDireccionEnvio;
        this.idCalle = idCalle;
        this.numero = numero;
        this.referencia = referencia;
        this.destinatario = destinatario;
        this.telefonoContacto = telefonoContacto;
        this.estado = estado;
    }
}
