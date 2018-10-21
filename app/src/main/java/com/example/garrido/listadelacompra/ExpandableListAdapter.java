package com.example.garrido.listadelacompra;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Clase generada por Carlos Garrido para la aplicación ComandUal en 12/04/2017
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> categorias;
    Map<String,ArrayList<String>> topics;
    Map<String,ArrayList<String>> tachados;

    public ExpandableListAdapter(Context context, ArrayList<String> categorias, Map<String, ArrayList<String>> topics, Map<String,ArrayList<String>> tachados) {
        this.context = context;
        this.categorias = categorias;
        this.topics = topics;
        this.tachados = tachados;
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
        return topics.get(categorias.get(groupPosition)).get(childPosition);
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
            view = inflater.inflate(R.layout.dropdown_producto,null);
        }

        TextView txtChild = view.findViewById(R.id.tvChild);
        txtChild.setText(topic);

        String categoria = categorias.get(groupPosition);
        if(tachados.size() != 0){
            ArrayList<String> values = tachados.get(categoria);
            if(values != null){
                for(String val:values){
                    if(val.equals(topic)){
                        txtChild.setPaintFlags(txtChild.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else{
                        txtChild.setPaintFlags(txtChild.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }


            }
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {

        return true;
    }
}
