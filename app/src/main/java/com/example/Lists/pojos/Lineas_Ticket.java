package com.example.Lists.pojos;

import java.io.Serializable;

public class Lineas_Ticket implements Serializable {
    String idLineaTicket;
    Producto producto;
    int cantidad;
    String observaciones;
    int prepara_idperfil;
    int sirve_idperfil;

    public Lineas_Ticket() {
    }

    public Lineas_Ticket(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Lineas_Ticket(Producto producto, int cantidad, int prepara_idperfil) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.prepara_idperfil = prepara_idperfil;
    }

    public Lineas_Ticket(String idLineaTicket, Producto producto, int cantidad, String observaciones, int prepara_idperfil, int sirve_idperfil) {
        this.idLineaTicket = idLineaTicket;
        this.producto = producto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.prepara_idperfil = prepara_idperfil;
        this.sirve_idperfil = sirve_idperfil;
    }

    public void setIdLineaTicket(String idLineaTicket) {
        this.idLineaTicket = idLineaTicket;
    }

    public String getIdLineaTicket() {
        return idLineaTicket;
    }
    ;
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getPrepara_idperfil() {
        return prepara_idperfil;
    }

    public void setPrepara_idperfil(int prepara_idperfil) {
        this.prepara_idperfil = prepara_idperfil;
    }

    public int getSirve_idperfil() {
        return sirve_idperfil;
    }

    public void setSirve_idperfil(int sirve_idperfil) {
        this.sirve_idperfil = sirve_idperfil;
    }

    @Override
    public String toString() {
        return "Lineas_Ticket{" +
                "idLineaTicket='" + idLineaTicket + '\'' +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", observaciones='" + observaciones + '\'' +
                ", prepara_idperfil=" + prepara_idperfil +
                ", sirve_idperfil=" + sirve_idperfil +
                '}';
    }
}
