package com.example.Lists.pojos;

import java.io.Serializable;

public class Departamento implements Serializable {
    String id;
    String nombre;

    public Departamento() {
    }

    public Departamento(String nombre) {
        this.nombre = nombre;
    }

    public Departamento(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getnombre() {
        return nombre;
    }

    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Departamento{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
