package com.example.pojos;

import java.io.Serializable;

public class Carta implements Serializable {
    String departamento;

    public Carta() {
    }

    public Carta(String departamento) {
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
