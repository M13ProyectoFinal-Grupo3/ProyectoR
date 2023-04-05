package com.example.pojos;

public class Permisos {
    String nombre;
    Boolean activo;

    public Permisos() {
    }

    public Permisos(String nombre) {
        this.nombre = nombre;
        this.activo = true; //por defecto será true; se cambiará al asignarlo a alguien no?
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
