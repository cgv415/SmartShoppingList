package com.example.garrido.listadelacompra;

import java.util.ArrayList;
import java.util.TreeMap;

public class Local {
    private String id;
    private String nombre;
    private String direccion;
    private String horario;
    private String nif;
    private String tlfn;
    private String web;
    private ArrayList<Producto> productos;

    public Local(String nombre, String direccion, String horario, String nif, String tlfn, String web) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.horario = horario;
        this.nif = nif;
        this.tlfn = tlfn;
        this.web = web;
    }

    public Local() {
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getTlfn() {
        return tlfn;
    }

    public void setTlfn(String tlfn) {
        this.tlfn = tlfn;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
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
