package com.example.garrido.listadelacompra;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    AutoCompleteTextView buscador;

    ExpandableListView expandableListView;
    ArrayList<String> agrupacion;
    Map<String,ArrayList<String>> map;
    ArrayList<Producto> productos;
    ArrayList<String> arrayBuscador;
    ArrayList<String> nombreCategorias;
    ArrayList<String> nombresSubcategorias;
    android.widget.ExpandableListAdapter listAdapter;

    EditText etNombre;
    EditText etDescripcion;
    EditText etEtiqueta;
    EditText etPrecio;
    EditText etMarca;

    Spinner spTipo;
    Spinner spCategoria;
    Spinner spSubcategoria;

    String agruparPor = "categoria";
    TreeMap<String,ArrayList<String>> tachados = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__productos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!manager.existeTablaLista()){
                    Snackbar.make(view, "Crear Lista", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Incluir producto a lista", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.nav_principal);
        item.setChecked(true);
        spTipo = findViewById(R.id.sp_tipo);
        final ArrayList<String> tipos = new ArrayList<>();
        tipos.add("producto");
        tipos.add("categoria");
        tipos.add("etiqueta");

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        spTipo.setAdapter(adapterTipo);

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buscador.setHint(tipos.get(i));
                String tipo = spTipo.getSelectedItem().toString();
                switch (tipo) {
                    case "producto": {
                        arrayBuscador = manager.obtenerNombreProductos();
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line, arrayBuscador);
                        buscador.setAdapter(adapter);
                        break;
                    }
                    case "categoria": {
                        arrayBuscador = manager.obtenerNombreCategorias();
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line, arrayBuscador);
                        buscador.setAdapter(adapter);
                        break;
                    }
                    default: {
                        arrayBuscador = new ArrayList<>();
                        for (int j = 0; j < productos.size(); j++) {
                            arrayBuscador.add(productos.get(j).getEtiqueta());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line, arrayBuscador);
                        buscador.setAdapter(adapter);
                        break;
                    }
                }

                buscador.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        expandableListView = findViewById(R.id.lv_dropdow);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(getApplicationContext(), agrupacion.get(groupPosition)+ ":" + map.get(agrupacion.get(groupPosition)).get(childPosition),Toast.LENGTH_SHORT).show();

                TextView texto = view.findViewById(R.id.tvChild);

                String key = agrupacion.get(groupPosition);

                ArrayList<String> valueTachados;
                ArrayList<String> valueMap;

                /*ArrayList<String> value = map.get(agrupacion.get(groupPosition));

                value.remove(texto.getText().toString());
                value.add(texto.getText().toString());

                map.put(key,value);*/

                if(tachados.containsKey(key)){
                    valueTachados = tachados.get(key);
                    if(valueTachados.contains(texto.getText().toString())){
                        valueTachados.remove(texto.getText().toString());
                    }else{
                        valueTachados.add(texto.getText().toString());
                    }
                }else{
                    valueTachados = new ArrayList<>();
                    valueTachados.add(texto.getText().toString());
                }
                tachados.put(key,valueTachados);


                if(valueTachados.contains(texto.getText().toString())){
                    valueMap = map.get(key);
                    valueMap.remove(texto.getText().toString());
                    valueMap.add(texto.getText().toString());
                }else{
                    valueMap = map.get(key);
                    Collections.sort(valueMap);
                }
                map.put(key,valueMap);

                actualizarAdapter();


                /*if(!texto.getPaint().isStrikeThruText()){


                }else{
                    texto.setPaintFlags(texto.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                }*/

                return false;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    //String nombreproducto = expandableListView.getItemAtPosition(i).toString();
                    //Producto producto = manager.obtenerProductoByNombre(nombreproducto);


                }
                return true;
            }
        });
        buscador = findViewById(R.id.ac_productos);

        actualizarLista();

        arrayBuscador = manager.obtenerNombreProductos();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, arrayBuscador);
        buscador.setAdapter(adapter);
        buscador.setThreshold(100);


        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Map<String,ArrayList<String>> mapAux = new TreeMap<>();
                ArrayList<String> agrupacionAux = new ArrayList<>();
                String tipo = spTipo.getSelectedItem().toString();
                ArrayList<String> value;

                if(charSequence.equals("")){
                    actualizarLista();
                }else {
                    Set<Map.Entry<String, ArrayList<String>>> entrySet = map.entrySet();
                    for (Map.Entry<String, ArrayList<String>> entry : entrySet) {
                        ArrayList<String> nombresProductos = entry.getValue();
                        for (int j = 0; j < nombresProductos.size(); j++) {
                            Producto producto = manager.obtenerProductoByNombre(nombresProductos.get(j));
                            switch (tipo) {
                                case "categoria":
                                    if (producto.getCategoria().getNombre().contains(charSequence)) {
                                        if (!agrupacionAux.contains(entry.getKey())) {
                                            value = new ArrayList<>();
                                            agrupacionAux.add(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        } else {
                                            value = mapAux.get(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        }
                                    }
                                    break;
                                case "etiqueta":
                                    if (producto.getEtiqueta().contains(charSequence)) {
                                        if (!agrupacionAux.contains(entry.getKey())) {
                                            value = new ArrayList<>();
                                            agrupacionAux.add(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        } else {
                                            value = mapAux.get(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        }
                                    }

                                    break;
                                case "producto":
                                    if (producto.getNombre().contains(charSequence)) {
                                        if (!agrupacionAux.contains(entry.getKey())) {
                                            value = new ArrayList<>();
                                            agrupacionAux.add(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        } else {
                                            value = mapAux.get(entry.getKey());
                                            value.add(producto.getNombre());
                                            mapAux.put(entry.getKey(), value);
                                        }
                                    }
                                    break;
                            }
                        }
                    }

                    listAdapter = new ExpandableListAdapter(getApplicationContext(), agrupacionAux,mapAux,tachados);
                    expandableListView.setAdapter(listAdapter);

                    for(int j = 0; j < agrupacionAux.size() ; j ++){
                        expandableListView.expandGroup(j);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //manager.crear();

        //TODO En el buscador de productos, que se filtren los productos de la lista
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void actualizarLista(){
        switch (agruparPor){
            case "orden":
                actualizarListaPorOrden();
                break;
            case "categoria":
                actualizarListaPorCategoria();
                break;
            case "etiqueta":
                actualizarListaPorEtiqueta();
                break;
        }

    }

    public void actualizarListaPorOrden(){
        productos = manager.obtenerProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){
            String producto = productos.get(i).getNombre();
            String letter = String.valueOf(producto.charAt(0));
            if(!agrupacion.contains(letter)){
                agrupacion.add(letter);
            }
            ArrayList<String> p = new ArrayList<>();
            if(map.containsKey(letter)){
                p  = map.get(letter);
            }
            p.add(producto);
            map.put(letter,p);
        }

        actualizarAdapter();
    }

    public void actualizarListaPorEtiqueta(){
        productos = manager.obtenerProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){
            String etiqueta = productos.get(i).getEtiqueta();
            if(!agrupacion.contains(etiqueta)){
                agrupacion.add(etiqueta);
            }
            ArrayList<String> p = new ArrayList<>();
            if(map.containsKey(etiqueta)){
                p  = map.get(etiqueta);
            }
            p.add(productos.get(i).getNombre());
            map.put(etiqueta,p);
        }

        actualizarAdapter();
    }

    public void actualizarListaPorCategoria(){
        productos = manager.obtenerProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){


                Categoria categoria = productos.get(i).getCategoria();
                if(!agrupacion.contains(categoria.getNombre())){
                    agrupacion.add(categoria.getNombre());
                }
                Collections.sort(agrupacion);

                ArrayList<String> p = new ArrayList<>();

                if(map.containsKey(categoria.getNombre())){
                    p  = map.get(categoria.getNombre());
                }
                p.add(productos.get(i).getNombre());
                map.put(categoria.getNombre(),p);
        }

        actualizarAdapter();
    }

    public void actualizarAdapter(){
        listAdapter = new ExpandableListAdapter(this, agrupacion,map,tachados);
        expandableListView.setAdapter(listAdapter);

        for(int j = 0; j < agrupacion.size() ; j ++){
            expandableListView.expandGroup(j);
        }
    }

    private static final int MENU_ITEM_ITEM1 = 1;
    private static final int MENU_ITEM_ITEM2 = 2;
    private static final int MENU_ITEM_ITEM3 = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__productos, menu);
        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "A-Z");
        menu.add(Menu.NONE, MENU_ITEM_ITEM2, Menu.NONE, "Categoria");
        menu.add(Menu.NONE, MENU_ITEM_ITEM3, Menu.NONE, "Etiqueta");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case MENU_ITEM_ITEM1:
                agruparPor = "orden";
                break;
            case MENU_ITEM_ITEM2:
                agruparPor = "categoria";
                break;
            case MENU_ITEM_ITEM3:
                agruparPor = "etiqueta";
                break;
        }
        actualizarLista();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        finish();

        manager.gestionarMenu(item,this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
