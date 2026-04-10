package org.example.proyecto.Modelos;

import java.time.LocalDate;
import java.util.Date;

public class Cliente {
   private int idCliente;
   private int idDireccion;
   private String cedula_rnc;
   private String nombres;
   private String apellidos;
   private Date fecha_nacimiento;
   private char sexo;
   private String telefono;
   private String email;
   private LocalDate fecha_registro;
   private int puntos_fidelidad;
   private boolean estado;

   public Cliente(){
   }

    public Cliente(int idCliente, int idDireccion, String cedula_rnc, String nombres, String apellidos, Date fecha_nacimiento, char sexo, String telefono, String email, LocalDate fecha_registro, int puntos_fidelidad, boolean estado) {
        this.idCliente = idCliente;
        this.idDireccion = idDireccion;
        this.cedula_rnc = cedula_rnc;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fecha_nacimiento = fecha_nacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
        this.fecha_registro = fecha_registro;
        this.puntos_fidelidad = puntos_fidelidad;
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCedula_rnc() {
        return cedula_rnc;
    }

    public void setCedula_rnc(String cedula_rnc) {
        this.cedula_rnc = cedula_rnc;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDate fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public int getPuntos_fidelidad() {
        return puntos_fidelidad;
    }

    public void setPuntos_fidelidad(int puntos_fidelidad) {
        this.puntos_fidelidad = puntos_fidelidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
