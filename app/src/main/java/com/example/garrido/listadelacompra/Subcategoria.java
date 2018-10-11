package com.example.garrido.listadelacompra;

import java.util.ArrayList;

public class Subcategoria {

    private String id;
    private String nombre;
    private Categoria categoria;
    private ArrayList<Producto> productos;

    public Subcategoria(String nombre) {
        this.nombre = nombre;
    }

    public Subcategoria() {
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
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
}
