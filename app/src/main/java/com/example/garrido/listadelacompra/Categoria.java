package com.example.garrido.listadelacompra;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Categoria implements Comparable<Categoria>{
    private String id;
    private String nombre;
    private ArrayList<Subcategoria> subcategorias;
    private ArrayList<Producto> productos;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public Categoria() {
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

    public ArrayList<Subcategoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(ArrayList<Subcategoria> subcategorias) {
        this.subcategorias = subcategorias;
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

    @Override
    public int compareTo(@NonNull Categoria categoria) {
        if(this.getId()!=null && categoria.getId()!=null){
            return this.getId().compareTo(categoria.getId());
        }else if (this.getId()!=null && categoria.getId()==null){
            return 1;
        }else if(this.getId()==null && categoria.getId()!=null){
            return -1;
        }else{
            return this.getNombre().compareTo(categoria.getNombre());
        }
    }
}
