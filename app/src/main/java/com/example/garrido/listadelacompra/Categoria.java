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
        nombre = "";
        subcategorias = new ArrayList<>();
        productos = new ArrayList<>();
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

    public ArrayList<String> getNombresSubcategorias(){
        ArrayList<String> nombres = new ArrayList<>();

        for(Subcategoria subcategoria:subcategorias){
            nombres.add(subcategoria.toString());
        }

        return nombres;
    }

    public ArrayList<String> toStringSubcategorias(){
        ArrayList<String> nombres = new ArrayList<>();

        for(Subcategoria subcategoria:subcategorias){
            nombres.add(subcategoria.toString());
        }

        return nombres;
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

    public String toJSON() {
        return  "'" + nombre + "':" +
                toStringSubcategorias();
    }

    @Override
    public String toString() {
        return  getNombre();
    }
}
