package com.example.garrido.listadelacompra;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.garrido.listadelacompra.Producto;
import com.example.garrido.listadelacompra.R;
import com.example.garrido.listadelacompra.Ticket;

import java.util.ArrayList;

public class AdapterTicket extends BaseAdapter {
    private Activity activity;
    private ArrayList<Ticket> tickets;

    public AdapterTicket(Activity activity, ArrayList<Ticket> tickets) {
        this.activity = activity;
        this.tickets = tickets;
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Object getItem(int i) {
        return tickets.get(i);
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
            v = inflater.inflate(R.layout.adapter_ticket,null);
        }
        TextView nombre = v.findViewById(R.id.tv_nombre);
        TextView precio = v.findViewById(R.id.tv_precio);
        TextView fecha = v.findViewById(R.id.tv_fecha);
        TextView hora = v.findViewById(R.id.tv_hora);

        Ticket t = tickets.get(i);
        nombre.setText(t.getLocal().getNombre());
        String p = String.format("%.2f",t.getTotal());
        p = p.replace(",",".");
        precio.setText(p);
        fecha.setText(t.getFecha());
        hora.setText(t.getHora());

        return v;
    }
}