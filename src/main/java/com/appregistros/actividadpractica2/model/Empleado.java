package com.appregistros.actividadpractica2.model;

import java.time.LocalDate;

public class Empleado {
    private int id_empleado;
    private String codigo;
    private String nombre;
    private String correo;
    private String cargo;
    private double salario;
    private LocalDate fecha_registro;
    private boolean activo;

    public Empleado() {
    }

    public Empleado(int id_empleado, String codigo, String nombre, String correo, String cargo, double salario, LocalDate fecha_registro, boolean activo) {
        this.id_empleado = id_empleado;
        this.codigo = codigo;
        this.nombre = nombre;
        this.correo = correo;
        this.cargo = cargo;
        this.salario = salario;
        this.fecha_registro = fecha_registro;
        this.activo = activo;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public LocalDate getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDate fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
