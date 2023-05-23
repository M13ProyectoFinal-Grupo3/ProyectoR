package com.example.Lists.pojos;

import java.io.Serializable;

public class Usuarios implements Serializable {
    String UID;
    String Perfil;
    String Restaurante;

    public Usuarios() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
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

    @Override
    public String toString() {
        return "Usuarios{" +
                "UID='" + UID + '\'' +
                ", Perfil='" + Perfil + '\'' +
                ", Restaurante='" + Restaurante + '\'' +
                '}';
    }
}
