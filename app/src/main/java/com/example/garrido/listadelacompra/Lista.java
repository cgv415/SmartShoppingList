package com.example.garrido.listadelacompra;

import java.util.ArrayList;
import java.util.TreeMap;

public class Lista {
    private String id;
    private String nombre;
    private String descripcion;
    private TreeMap<Producto,Integer> productos;

    public Lista(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Lista(String id, String nombre, String descripcion, TreeMap<Producto,Integer> productos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.productos = productos;
    }

    public Lista() {
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

    public TreeMap<Producto,Integer> getProductos() {
        return productos;
    }

    public void setProductos(TreeMap<Producto,Integer> productos) {
        this.productos = productos;
    }
}
