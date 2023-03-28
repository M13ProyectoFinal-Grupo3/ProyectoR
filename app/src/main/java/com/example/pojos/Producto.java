package com.example.pojos;

import java.io.Serializable;
import java.util.Arrays;

public class Producto implements Serializable {
    String id;
    String nombre;
    String descripcion;
    Boolean activo;
    Float precio;
    Alergeno[] alergenos;
    String prepara_idperfil;
    String id_departamento;
    byte[] foto_producto;

    public Producto() {
    }

    public Producto(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }


    public Producto(String id, String nombre, String descripcion, Boolean activo, Float precio, Alergeno[] alergenos, String prepara_idperfil, String id_departamento, byte[] foto_producto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.precio = precio;
        this.alergenos = alergenos;
        this.prepara_idperfil = prepara_idperfil;
        this.id_departamento = id_departamento;
        this.foto_producto = foto_producto;
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

    public byte[] getFoto_producto() {
        return foto_producto;
    }

    public void setFoto_producto(byte[] foto_producto) {
        this.foto_producto = foto_producto;
    }
}
