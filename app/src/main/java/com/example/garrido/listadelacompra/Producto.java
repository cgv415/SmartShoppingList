package com.example.garrido.listadelacompra;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Producto implements Comparable<Producto>,Parcelable{
    private String id;
    private String nombre;
    private String descripcion;
    private String etiqueta;
    private Categoria categoria;
    private Subcategoria subcategoria;
    private Local local;
    private String marca;
    private Double precio;
    //¿?
    private Double precioLocal;


    public Producto() {
    }

    public Producto(String nombre) {
        this.nombre = nombre;
    }

    protected Producto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        descripcion = in.readString();
        etiqueta = in.readString();
        marca = in.readString();
        if (in.readByte() == 0) {
            precio = null;
        } else {
            precio = in.readDouble();
        }
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    public Double getPrecioLocal() {
        return precioLocal;
    }

    public void setPrecioLocal(Double precioLocal) {
        this.precioLocal = precioLocal;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Producto(String id, String nombre, String descripcion, String etiqueta, Categoria categoria, Subcategoria subcategoria, Local local, String marca, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiqueta = etiqueta;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.local = local;
        this.marca = marca;
        this.precio = precio;
    }

    public Producto(String nombre, String descripcion, String etiqueta, Categoria categoria, Subcategoria subcategoria, Local local, String marca, Double precio) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiqueta = etiqueta;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.local = local;
        this.marca = marca;
        this.precio = precio;
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

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public int compareTo(@NonNull Producto producto) {

        if(this.getId()!=null && producto.getId()!=null){
            return this.getId().compareTo(producto.getId());
        }else if (this.getId()!=null && producto.getId()==null){
            return 1;
        }else if(this.getId()==null && producto.getId()!=null){
            return -1;
        }else{
            return this.getNombre().compareTo(producto.getNombre());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(descripcion);
        parcel.writeString(local.getNombre());
        parcel.writeString(marca);
        parcel.writeDouble(precio);
    }
}