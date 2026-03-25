package org.example.proyecto.Modelos;

public class Medicamento {
    private int idMedicamento;
    private int idProducto;
    private String principioActivo;
    private String concentracion;
    private String viaAdministracion;
    private String registroSanitario;
    private String laboratorio;
    private boolean estado;

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getPrincipioActivo() {
        return principioActivo;
    }

    public void setPrincipioActivo(String principioActivo) {
        this.principioActivo = principioActivo;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public String getViaAdministracion() {
        return viaAdministracion;
    }

    public void setViaAdministracion(String viaAdministracion) {
        this.viaAdministracion = viaAdministracion;
    }

    public String getRegistroSanitario() {
        return registroSanitario;
    }

    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Medicamento(int idMedicamento, int idProducto, String principioActivo, String concentracion, String viaAdministracion, String registroSanitario, String laboratorio, boolean estado) {
        this.idMedicamento = idMedicamento;
        this.idProducto = idProducto;
        this.principioActivo = principioActivo;
        this.concentracion = concentracion;
        this.viaAdministracion = viaAdministracion;
        this.registroSanitario = registroSanitario;
        this.laboratorio = laboratorio;
        this.estado = estado;
    }
}
