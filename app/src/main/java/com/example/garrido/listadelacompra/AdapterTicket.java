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

public class AdapterTicket extends BaseAdapter {
    private Activity activity;
    private ArrayList<Producto> productos;

    public AdapterTicket(Activity activity, ArrayList<Producto> productos) {
        this.activity = activity;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int i) {
        return productos.get(i);
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

        producto.setText(productos.get(i).getNombre());
        precio.setText(productos.get(i).getPrecio().toString());

        return v;
    }
}
