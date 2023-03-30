package com.example.pojos;

import java.io.Serializable;

public class Alergeno implements Serializable {
    String nombre;

    public Alergeno() {
    }

    public Alergeno(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
