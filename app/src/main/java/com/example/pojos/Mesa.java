package com.example.pojos;

import java.io.Serializable;

public class Mesa implements Serializable {
    String num_mesa;
    String ubicacion;

    public Mesa() {
    }

    public Mesa(String num_mesa, String ubicacion) {
        this.num_mesa = num_mesa;
        this.ubicacion = ubicacion;
    }

    public String getNum_mesa() {
        return num_mesa;
    }

    public void setNum_mesa(String num_mesa) {
        this.num_mesa = num_mesa;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
