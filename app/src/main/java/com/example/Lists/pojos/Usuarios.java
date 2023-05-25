package com.example.Lists.pojos;

import java.io.Serializable;

public class Usuarios implements Serializable {
    String Perfil;
    String Restaurante;
    String UID;
    String id;

    public Usuarios() {
    }

    public Usuarios(String perfil, String restaurante, String UID, String id) {
        this.Perfil = perfil;
        this.Restaurante = restaurante;
        this.UID = UID;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", Perfil='" + Perfil + '\'' +
                ", Restaurante='" + Restaurante + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
