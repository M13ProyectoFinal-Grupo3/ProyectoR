package com.example.Lists.pojos;

import android.util.Log;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Log.d("error 0", "this is == 0");
        if (o == null || getClass() != o.getClass()) return false;
        Log.d("error 1", "o == null");
        Mesa mesa = (Mesa) o;
        return Objects.equals(num_mesa, mesa.num_mesa) && Objects.equals(ubicacion, mesa.ubicacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num_mesa, ubicacion);
    }
}
