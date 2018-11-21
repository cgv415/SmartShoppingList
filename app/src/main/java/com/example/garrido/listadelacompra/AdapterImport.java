package com.example.garrido.listadelacompra;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class AdapterImport extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> datos;
    private boolean todos;
    private ArrayList<String> checked;


    public AdapterImport(Activity activity, ArrayList<String> datos,boolean todos) {
        this.activity = activity;
        this.datos = datos;
        this.todos = todos;

        checked = new ArrayList<>();

    }

    public ArrayList<String> getChecked(){
        return checked;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_import,null);
        }
        CheckBox dato = v.findViewById(R.id.checkBox);
        dato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checked.add(datos.get(position));
                }else{
                    checked.remove(datos.get(position));
                }
            }
        });
        dato.setText(datos.get(position));

        if(todos){
            dato.setChecked(true);
        }

        return v;
    }
}