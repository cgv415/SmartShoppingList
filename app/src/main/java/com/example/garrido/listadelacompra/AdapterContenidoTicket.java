package com.example.garrido.listadelacompra;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AdapterContenidoTicket extends BaseAdapter {
    private Activity activity;
    private ArrayList<Producto> productos;

    public AdapterContenidoTicket(Activity activity, ArrayList<Producto> productos) {
        this.activity = activity;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int i) {
        return productos.get(i).getNombre();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ocr_listview,null);
        }
        TextView producto = v.findViewById(R.id.et_producto);
        TextView precio = v.findViewById(R.id.et_precio);

        String stNombre = productos.get(i).getNombre();
        String stPrecio = "";
        try{
             stPrecio = String.format("%.2f",productos.get(i).getPrecio());
        }catch (Exception e){
            stPrecio = "?";
        }


        if(!stNombre.equals("")){
            producto.setText(stNombre);
        }else{
            producto.setText(" ");
        }

        if(!stNombre.equals("") && !stNombre.equals("Insertar nuevo producto")){
            precio.setText(stPrecio);
        }else{
            precio.setText(" ");
        }


        return v;
    }
}
