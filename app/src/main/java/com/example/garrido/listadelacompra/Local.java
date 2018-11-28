package com.example.garrido.listadelacompra;

import java.util.ArrayList;
import java.util.TreeMap;

public class Local {
    private String id;
    private String nombre;
    private String descripcion;

    private ArrayList<Producto> productos;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Local() {
        nombre = "";
    }

    public Local(String nombre) {
        this.nombre = nombre;
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

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public ArrayList<String> getNombreProductos(){
        ArrayList<String> nombres = new ArrayList<>();
        for( Producto p
                : productos){
            nombres.add(p.getNombre());
        }

        return nombres;
    }

    public String toStringProductos(){
        String nombres = "";
        Producto finProductos = productos.get(productos.size()-1);
        for( Producto p
                : productos){
            nombres += "'" + p.getNombre() + "':" + p.getPrecio();
            if(!finProductos.equals(p)){
                nombres += ",";
            }
        }

        return nombres;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public boolean isNull(){
        if(nombre==null){
            return true;
        }else{
            return false;
        }
    }

    public String toJSON() {
        return "'" + getNombre() + "':{" +
                toStringProductos() + "}";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
