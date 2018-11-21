package com.example.garrido.listadelacompra;

import java.util.ArrayList;
import java.util.TreeMap;

public class Lista {
    private String id;
    private String nombre;
    private String descripcion;
    private ArrayList<Producto> productos;
    private boolean principal;

    public Lista(String nombre, String descripcion, boolean principal) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.principal = principal;
    }

    public Lista(String id, String nombre, String descripcion, ArrayList<Producto> productos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.productos = productos;
    }

    public Lista() {
        this.id = "0";
        this.nombre = "";
        this.productos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public ArrayList<String> getNombreProductos(){
        ArrayList<String> nombres = new ArrayList<>();

        for(Producto p
                : productos){
            nombres.add(p.getNombre());
        }

        return nombres;
    }

    public String toStringProductos(){
        String nombres = "";
        Producto finProductos = productos.get(productos.size()-1);
        for(Producto p
                : productos){
            nombres += p.toString();
            if(!finProductos.equals(p)){
                nombres += ",";
            }
        }

        return nombres;
    }

    @Override
    public String toString() {
        return  "'" + nombre + "': {descripcion:'" +
                descripcion + "',producto:{" +
                toStringProductos() + "},principal:" +
                principal +
                "}";
    }
}
