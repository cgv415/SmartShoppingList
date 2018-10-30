package com.example.garrido.listadelacompra;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AdapterComparar extends BaseAdapter {

    private Activity activity;
    private TreeMap<String,String> mapa;

    public AdapterComparar(Activity activity, TreeMap<String,String> mapa) {
        this.activity = activity;
        this.mapa = mapa;
    }


    @Override
    public int getCount() {
        return mapa.size();
    }

    @Override
    public Object getItem(int i) {
        String key = (String) mapa.keySet().toArray()[i];

        return key;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_comparar, null);
        }
        String nombreLocal = (String) getItem(i);
        //String precioString = String.valueOf(mapa.get(i));
        String precioEnLocal = (String) mapa.get(nombreLocal);

        TextView local = v.findViewById(R.id.tv_local);
        TextView precio = v.findViewById(R.id.tv_precio);


        local.setText(nombreLocal);
        precio.setText(precioEnLocal);

        return v;
    }
}
