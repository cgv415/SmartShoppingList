package com.example.garrido.listadelacompra;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ticket {
    private String idTicket;
    private String fecha;
    private String hora;
    private Local local;
    private double total;
    private ArrayList<Producto> productos;
    private int ano,mes,dia;

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public double getTotal() {
        return total;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public String toStringProductos(){
        String nombres = "{";
        Producto finProductos = productos.get(productos.size()-1);
        for( Producto p
                : productos){
            nombres += "'" + p.getNombre() + "':" + p.getPrecio();
            if(!finProductos.equals(p)){
                nombres += ",";
            }
        }
        nombres += "}";
        return nombres;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }



    public String toJSON() {
        return  "'" + fecha + " " + hora + "':" +
                "{'fecha':'" + fecha + "'," +
                "'hora':'" + hora + "'," +
                "'local':'" + local.getNombre() + "'," +
                "'total':" + total + "," +
                "'producto':" + toStringProductos() +
                '}';
    }

    @Override
    public String toString() {
        return  fecha + ";" + hora;
    }
}
