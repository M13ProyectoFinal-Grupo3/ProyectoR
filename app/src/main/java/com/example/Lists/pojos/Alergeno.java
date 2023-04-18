package com.example.Lists.pojos;

import java.io.Serializable;

public class Alergeno implements Serializable {
    String id;
    String nombre;

    public Alergeno() {
    }

    public Alergeno(String nombre) {
        this.nombre = nombre;
    }

    public Alergeno(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
