package com.example.Lists.pojos;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Lineas_Ticket implements Serializable {
    String idLineaTicket;
    Producto producto;
    int cantidad;
    String observaciones;
    String prepara_idperfil;
    String sirve_idperfil;

    public Lineas_Ticket() {
    }

    public Lineas_Ticket(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.prepara_idperfil = producto.getPrepara_idperfil();
        this.sirve_idperfil = producto.getSirve_idperfil();
    }

    public Lineas_Ticket(int cantidad,String observaciones,Producto producto) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.prepara_idperfil = producto.getPrepara_idperfil();
        this.sirve_idperfil = producto.getSirve_idperfil();
    }

    public String getIdLineaTicket() {
        return idLineaTicket;
    }

    public void setIdLineaTicket(String idLineaTicket) {
        this.idLineaTicket = idLineaTicket;
    }

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

    public String getPrepara_idperfil() {
        return prepara_idperfil;
    }

    public void setPrepara_idperfil(String prepara_idperfil) {
        this.prepara_idperfil = prepara_idperfil;
    }

    public String getSirve_idperfil() {
        return sirve_idperfil;
    }

    public void setSirve_idperfil(String sirve_idperfil) {
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
