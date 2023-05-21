package com.example.Lists.pojos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Ticket implements Serializable {
    Restaurante restaurante;
    String id;
    Date fecha;
    int num_mesa;
    int id_camarero;
    List<Lineas_Ticket> lineas_ticket;
    //Lineas_Ticket linea_ticket;

    public Ticket() {
    }

    public Ticket(String id) {
        this.id = id;
    }

    public Ticket(Restaurante restaurante, String id, Date fecha, int num_mesa, int id_camarero, List<Lineas_Ticket> lineas_ticket) {
        this.restaurante = restaurante;
        this.id = id;
        this.fecha = fecha;
        this.num_mesa = num_mesa;
        this.id_camarero = id_camarero;
        this.lineas_ticket = lineas_ticket;
    }

    public Ticket(Restaurante restaurante, String id, Date fecha, int num_mesa, int id_camarero) {
        this.restaurante = restaurante;
        this.id = id;
        this.fecha = fecha;
        this.num_mesa = num_mesa;
        this.id_camarero = id_camarero;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Lineas_Ticket> getLineas_ticket() {
        return lineas_ticket;
    }

    public void setLineas_ticket(List<Lineas_Ticket> lineas_ticket) {
        this.lineas_ticket = lineas_ticket;
    }
}
