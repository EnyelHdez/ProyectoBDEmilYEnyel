package org.example.proyecto.Modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Empleado {
    private int        idEmpleado;
    private int        idCargo;
    private Integer    idDireccion;
    private String     cedula;
    private String     nombres;
    private String     apellidos;
    private LocalDate  fechaNacimiento;
    private char       sexo;
    private String     telefono;
    private String     email;
    private LocalDate fechaIngreso;
    private BigDecimal salarioBase;
    private char    estado_temp;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public Integer getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public char isEstado() {
        return estado_temp;
    }

    public void setEstado(char estado) {
        this.estado_temp = estado;
    }

    public Empleado(int idEmpleado, int idCargo, Integer idDireccion, String cedula, String nombres, String apellidos, LocalDate fechaNacimiento, char sexo, String telefono, String email, LocalDate fechaIngreso, BigDecimal salarioBase, char estado) {
        this.idEmpleado = idEmpleado;
        this.idCargo = idCargo;
        this.idDireccion = idDireccion;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
        this.fechaIngreso = fechaIngreso;
        this.salarioBase = salarioBase;
        this.estado_temp = estado;
    }
}
