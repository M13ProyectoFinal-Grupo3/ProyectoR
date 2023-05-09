package com.example.Lists.pojos;

import java.io.Serializable;

public class Usuarios implements Serializable {
    String UID;
    String perfil;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Usuarios(String UID, String perfil) {
        this.UID = UID;
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "UID='" + UID + '\'' +
                ", perfil='" + perfil + '\'' +
                '}';
    }
}
