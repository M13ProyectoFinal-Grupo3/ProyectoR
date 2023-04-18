package com.example.Lists.pojos;

import java.io.Serializable;
import java.util.Date;

public class Ticket implements Serializable {
    int id_restaurante;
    Date fecha;
    int num_mesa;
    int id_camarero;
    int comensales; // o Comensal comensales? (segun el diagrama de clases)
    Lineas_Ticket linea_ticket;

    public Ticket() {
    }

    public Ticket(int id_restaurante, Date fecha, int num_mesa, int id_camarero, int comensales, Lineas_Ticket linea_ticket) {
        this.id_restaurante = id_restaurante;
        this.fecha = fecha;
        this.num_mesa = num_mesa;
        this.id_camarero = id_camarero;
        this.comensales = comensales;
        this.linea_ticket = linea_ticket;
    }

    public int getId_restaurante() {
        return id_restaurante;
    }

    public void setId_restaurante(int id_restaurante) {
        this.id_restaurante = id_restaurante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getNum_mesa() {
        return num_mesa;
    }

    public void setNum_mesa(int num_mesa) {
        this.num_mesa = num_mesa;
    }

    public int getId_camarero() {
        return id_camarero;
    }

    public void setId_camarero(int id_camarero) {
        this.id_camarero = id_camarero;
    }

    public int getComensales() {
        return comensales;
    }

    public void setComensales(int comensales) {
        this.comensales = comensales;
    }

    public Lineas_Ticket getLinea_ticket() {
        return linea_ticket;
    }

    public void setLinea_ticket(Lineas_Ticket linea_ticket) {
        this.linea_ticket = linea_ticket;
    }
}
