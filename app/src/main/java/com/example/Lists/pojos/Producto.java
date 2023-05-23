package com.example.Lists.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Producto implements Serializable {
    String id;
    String nombre;
    String descripcion;
    Boolean activo;
    Float precio;
    String prepara_idperfil;
    ArrayList<Alergeno> alergenos;

    public Producto() {
        setAlergenos(new ArrayList<>());
    }

    public Producto(String nombre, Float precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(Boolean activo, String nombre, String descripcion, Float precio, ArrayList<Alergeno> alergenos) {
        this.activo = activo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.alergenos = alergenos;
    }

    public Producto(String id, String nombre, String descripcion, Boolean activo, Float precio, ArrayList<Alergeno> alergenos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.precio = precio;
        this.alergenos = alergenos;
    }

    public ArrayList<Alergeno> getAlergenos() {
        if(alergenos==null) alergenos = new ArrayList<>();
        return alergenos;
    }

    public void setAlergenos(ArrayList<Alergeno> alergenos) {
        this.alergenos = alergenos;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }


    public String getPrepara_idperfil() {
        return prepara_idperfil;
    }

    public void setPrepara_idperfil(String prepara_idperfil) {
        this.prepara_idperfil = prepara_idperfil;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", precio=" + precio +
                ", prepara_idperfil='" + prepara_idperfil + '\'' +
                '}';
    }
}
