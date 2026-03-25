package org.example.proyecto.Modelos;

public class MedicamentoEnfApl {
    private int idMedEnf;
    private int idMedicamento;
    private int idEnfermedad;
    private String observacion;

    public int getIdMedEnf() {
        return idMedEnf;
    }

    public void setIdMedEnf(int idMedEnf) {
        this.idMedEnf = idMedEnf;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public int getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(int idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public MedicamentoEnfApl(int idMedEnf, int idMedicamento, int idEnfermedad, String observacion) {
        this.idMedEnf = idMedEnf;
        this.idMedicamento = idMedicamento;
        this.idEnfermedad = idEnfermedad;
        this.observacion = observacion;
    }
}
