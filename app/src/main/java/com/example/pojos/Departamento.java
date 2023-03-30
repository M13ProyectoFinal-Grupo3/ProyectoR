package com.example.pojos;

import java.io.Serializable;

public class Departamento implements Serializable {
    String nombre;
    Producto[] productos;

    public Departamento() {
    }

    public Departamento(String nombre, Producto[] productos) {
        this.nombre = nombre;
        this.productos = productos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Producto[] getProductos() {
        return productos;
    }

    public void setProductos(Producto[] productos) {
        this.productos = productos;
    }
}