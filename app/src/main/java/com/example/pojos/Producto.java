package com.example.pojos;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Producto implements Serializable {
    String nombre;
    String descripcion;
    Boolean activo;
    Float precio;
    Alergeno[] alergenos;
    String prepara_idperfil;
    String id_departamento;

    public Producto() {}

    public Producto(Boolean activo, String nombre, String descripcion, Float precio) {
        this.activo = activo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }


    public Producto( String nombre, String descripcion, Boolean activo, Float precio, Alergeno[] alergenos, String prepara_idperfil, String id_departamento) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.precio = precio;
        this.alergenos = alergenos;
        this.prepara_idperfil = prepara_idperfil;
        this.id_departamento = id_departamento;
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

    public Alergeno[] getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(Alergeno[] alergenos) {
        this.alergenos = alergenos;
    }

    public String getPrepara_idperfil() {
        return prepara_idperfil;
    }

    public void setPrepara_idperfil(String prepara_idperfil) {
        this.prepara_idperfil = prepara_idperfil;
    }

    public String getId_departamento() {
        return id_departamento;
    }

    public void setId_departamento(String id_departamento) {
        this.id_departamento = id_departamento;
    }
}
