package com.example.garrido.listadelacompra;

public class Fact {
    private String id;
    private String idTicket;
    private String idProducto;
    private double precio;

    public Fact(String idTicket, String idProducto,double precio) {
        this.idTicket = idTicket;
        this.idProducto = idProducto;
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
}
