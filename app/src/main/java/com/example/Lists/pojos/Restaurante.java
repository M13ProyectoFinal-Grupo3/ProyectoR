package com.example.Lists.pojos;

import java.io.Serializable;

public class Restaurante implements Serializable {
    String nombre;
    String razonSocial;
    String nif;
    String provincia;
    String ciudad;
    String codPostal;
    String telefono;

    public Restaurante() {
    }

    public Restaurante(String nombre, String razonSocial, String nif, String provincia, String ciudad, String codPostal, String telefono) {
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.nif = nif;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.codPostal = codPostal;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
