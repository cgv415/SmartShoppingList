package com.example.garrido.listadelacompra;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class AdapterLocal extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> categorias;
    Map<String,ArrayList<Producto>> topics;

    public AdapterLocal(Context context, ArrayList<String> categorias, Map<String, ArrayList<Producto>> topics) {
        this.context = context;
        this.categorias = categorias;
        this.topics = topics;
    }

    @Override
    public int getGroupCount() {
        return categorias.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return topics.get(categorias.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categorias.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return topics.get(categorias.get(groupPosition)).get(childPosition).getNombre();
    }

    public String getPrecio(int groupPosition, int childPosition) {
        return String.valueOf(topics.get(categorias.get(groupPosition)).get(childPosition).getPrecioLocal());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        String lang = (String) getGroup(groupPosition);
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dropdown_categoria,null);
        }

        TextView txtParent = view.findViewById(R.id.tvParent);
        txtParent.setText(lang);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        String topic = (String) getChild(groupPosition,childPosition);
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dropdown_producto_local,null);
        }

        TextView txtChild = view.findViewById(R.id.tvChild);
        TextView txtPrecio = view.findViewById(R.id.tv_precio);
        txtChild.setText(topic);
        txtPrecio.setText(getPrecio(groupPosition,childPosition));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {

        return true;
    }
}
