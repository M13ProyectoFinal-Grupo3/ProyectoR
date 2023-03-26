package com.example.pojos;

import java.util.Arrays;

public class Producto {
    String id;
    String nombre;
    String descripcion;
    Boolean activo;
    Float precio;
    Alergeno[] alergenos;
    String prepara_idperfil;
    byte[] imagen_carta;
    byte[] imagen_ampliada;

    public Producto() {
    }

    public Producto(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Producto(String id, String nombre, String descripcion, Boolean activo, Float precio, Alergeno[] alergenos, String prepara_idperfil, byte[] imagen_carta, byte[] imagen_ampliada) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.precio = precio;
        this.alergenos = alergenos;
        this.prepara_idperfil = prepara_idperfil;
        this.imagen_carta = imagen_carta;
        this.imagen_ampliada = imagen_ampliada;
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

    public byte[] getImagen_carta() {
        return imagen_carta;
    }

    public void setImagen_carta(byte[] imagen_carta) {
        this.imagen_carta = imagen_carta;
    }

    public byte[] getImagen_ampliada() {
        return imagen_ampliada;
    }

    public void setImagen_ampliada(byte[] imagen_ampliada) {
        this.imagen_ampliada = imagen_ampliada;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", precio=" + precio +
                ", alergenos=" + Arrays.toString(alergenos) +
                ", prepara_idperfil='" + prepara_idperfil + '\'' +
                '}';
    }
}
