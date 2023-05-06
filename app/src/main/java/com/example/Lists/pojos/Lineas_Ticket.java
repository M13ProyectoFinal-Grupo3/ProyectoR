package com.example.Lists.pojos;

import java.io.Serializable;

public class Lineas_Ticket implements Serializable {
    String idLineaTicket;
    Producto producto;
    int cantidad;
    String observaciones;
    Float precioUnitario;
    Float precioTotal;
    int prepara_idperfil;
    int sirve_idperfil;

    public Lineas_Ticket() {
    }

    public Lineas_Ticket(Producto producto, int cantidad, String observaciones, Float precioUnitario, int prepara_idperfil, int sirve_idperfil) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioUnitario * cantidad;
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

    public Float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Float getPrecioTotal() {
        return precioTotal;
    }

    //ESTO SE PUEDE ELIMINAR NO? ESTÁ PUESTO EN EL CONSTRUCTOR QUE SEA AUTOMATICO PRECIOUNITARIO * CANTIDAD
    //public void setPrecioTotal(Float precioTotal) {
    //    this.precioTotal = precioTotal;
    //}

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
