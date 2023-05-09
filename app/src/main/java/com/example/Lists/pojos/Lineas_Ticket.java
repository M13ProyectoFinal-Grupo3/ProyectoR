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
}
