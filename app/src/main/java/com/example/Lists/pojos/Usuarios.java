package com.example.Lists.pojos;

import java.io.Serializable;

public class Usuarios implements Serializable {
    String Perfil;
    String Restaurante;
    String UID;

    public Usuarios() {
    }

    public Usuarios(String perfil, String restaurante, String UID) {
        Perfil = perfil;
        Restaurante = restaurante;
        this.UID = UID;
    }

    public String getPerfil() {
        return Perfil;
    }

    public void setPerfil(String perfil) {
        Perfil = perfil;
    }

    public String getRestaurante() {
        return Restaurante;
    }

    public void setRestaurante(String restaurante) {
        Restaurante = restaurante;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "Perfil='" + Perfil + '\'' +
                ", Restaurante='" + Restaurante + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
