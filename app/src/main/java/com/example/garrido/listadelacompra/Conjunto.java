package com.example.garrido.listadelacompra;

import java.util.ArrayList;

public class Conjunto {
    private String id;
    private String nombre;
    private String descripcion;
    private ArrayList<Producto> productos;

    public Conjunto() {
        productos = new ArrayList<>();
        nombre = "";
        descripcion = "";
    }

    public Conjunto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public void insertarProducto(Producto producto){
        productos.add(producto);
    }

    public void eliminarProducto(Producto producto){
        int pos = -1;
        for(int i = 0 ; i < productos.size() ; i++){
            Producto p = productos.get(i);
            if(producto.getNombre().equals(p.getNombre())){
                pos = i;
            }
        }
        if(pos != -1){
            productos.remove(pos);
        }
    }

    public String toJSON() {
        return  "'" + nombre + "': {descripcion:'" +
                descripcion + "',producto:{" +
                toStringProductos() + "}" +
                "}";
    }

    @Override
    public String toString() {
        return  getNombre();
    }
}
