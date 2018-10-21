package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Activity_Productos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    AutoCompleteTextView buscador;

    android.widget.ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;

    ArrayList<String> agrupacion;
    Map<String,ArrayList<String>> map;
    ArrayList<Producto> productos;
    ArrayList<String> arrayBuscador;
    ArrayList<String> nombreCategorias;
    ArrayList<String> nombresSubcategorias;

    EditText etNombre;
    EditText etDescripcion;
    EditText etEtiqueta;
    EditText etPrecio;
    EditText etMarca;

    Spinner spTipo;
    Spinner spCategoria;
    Spinner spSubcategoria;

    String agruparPor = "categoria";

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
                popUpInsertarProducto();
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
        final MenuItem item = menu.findItem(R.id.nav_productos);
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
                String nombreproducto = map.get(agrupacion.get(groupPosition)).get(childPosition);
                Producto producto = manager.obtenerProductoByNombre(nombreproducto);
                popUpModificarProducto(producto);
                return false;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    String nombreproducto = expandableListView.getItemAtPosition(i).toString();
                    Producto producto = manager.obtenerProductoByNombre(nombreproducto);
                    popUpEliminar(producto);

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

                    listAdapter = new ExpandableListAdapter(getApplicationContext(), agrupacionAux,mapAux,new TreeMap<String, ArrayList<String>>());
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
        listAdapter = new ExpandableListAdapter(this, agrupacion,map,new TreeMap<String, ArrayList<String>>());
        expandableListView.setAdapter(listAdapter);

        for(int j = 0; j < agrupacion.size() ; j ++){
            expandableListView.expandGroup(j);
        }
    }

    public void popUpInsertarProducto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_producto, null);

        ImageButton btn_categoria = v.findViewById(R.id.bt_categoria);
        btn_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertar("categoria");
            }
        });
        ImageButton btn_subcategoria = v.findViewById(R.id.bt_subcategoria);
        btn_subcategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertar("subcategoria");
            }
        });

        etNombre = v.findViewById(R.id.et_nombre_producto);
        etDescripcion = v.findViewById(R.id.et_descripcion);
        etMarca = v.findViewById(R.id.et_marca);
        etPrecio = v.findViewById(R.id.et_precio_producto);
        etEtiqueta = v.findViewById(R.id.et_alias);

        spCategoria = v.findViewById(R.id.sp_categoria);
        spSubcategoria = v.findViewById(R.id.sp_subcategoria);

        nombreCategorias = manager.obtenerNombreCategorias();
        Collections.sort(nombreCategorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombreCategorias);
        spCategoria.setAdapter(adapterCategoria);

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreCategoria = nombreCategorias.get(i);
                Categoria categoria = manager.obtenerCategoria(nombreCategoria);
                ArrayList<Subcategoria> subcategorias = manager.obtenerSubcategorias_Categoria(categoria);
                nombresSubcategorias = new ArrayList<>();
                for(Subcategoria sucba: subcategorias){
                    nombresSubcategorias.add(sucba.getNombre());
                }
                Collections.sort(nombresSubcategorias);
                ArrayAdapter<String> adapterSubcategoria = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresSubcategorias);
                spSubcategoria.setAdapter(adapterSubcategoria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setTitle("Insertar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    Producto producto = new Producto();
                    try{
                        if(!etPrecio.getText().toString().equals("")){
                            Double.parseDouble(etPrecio.getText().toString());
                        }
                        if(!etNombre.getText().toString().equals("")){
                            producto.setNombre(etNombre.getText().toString().toLowerCase());
                            producto.setDescripcion(etDescripcion.getText().toString());
                            producto.setMarca(etMarca.getText().toString().toLowerCase());
                            producto.setEtiqueta(etEtiqueta.getText().toString().toLowerCase());
                            Categoria categoria = manager.obtenerCategoria(spCategoria.getSelectedItem().toString());
                            producto.setCategoria(categoria);
                            Subcategoria subcategoria = manager.obtenerSubcategoria(spSubcategoria.getSelectedItem().toString());
                            producto.setSubcategoria(subcategoria);
                            producto.setPrecio(Double.parseDouble(etPrecio.getText().toString()));

                            manager.insertarProducto(producto);

                            actualizarLista();
                        }else{
                            Toast.makeText(v.getContext(),R.string.error_nombre,Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(v.getContext(),R.string.error_precio,Toast.LENGTH_LONG).show();
                    }

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpModificarProducto(final Producto producto){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_producto, null);

        ImageButton btn_categoria = v.findViewById(R.id.bt_categoria);
        btn_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertar("categoria");
            }
        });
        ImageButton btn_subcategoria = v.findViewById(R.id.bt_subcategoria);
        btn_subcategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertar("subcategoria");
            }
        });

        etNombre = v.findViewById(R.id.et_nombre_producto);
        etNombre.setText(producto.getNombre());
        etDescripcion = v.findViewById(R.id.et_descripcion);
        etDescripcion.setText(producto.getDescripcion());
        etMarca = v.findViewById(R.id.et_marca);
        etMarca.setText(producto.getMarca());
        etPrecio = v.findViewById(R.id.et_precio_producto);
        etPrecio.setText(String.valueOf(producto.getPrecio()));
        etEtiqueta = v.findViewById(R.id.et_alias);
        etEtiqueta.setText(producto.getEtiqueta());


        spCategoria = v.findViewById(R.id.sp_categoria);
        spSubcategoria = v.findViewById(R.id.sp_subcategoria);

        nombreCategorias = manager.obtenerNombreCategorias();
        Collections.sort(nombreCategorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombreCategorias);
        spCategoria.setAdapter(adapterCategoria);

        int posCat = nombreCategorias.indexOf(producto.getCategoria().getNombre());
        spCategoria.setSelection(posCat);

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreCategoria = nombreCategorias.get(i);
                Categoria categoria = manager.obtenerCategoria(nombreCategoria);
                ArrayList<Subcategoria> subcategorias = manager.obtenerSubcategorias_Categoria(categoria);
                nombresSubcategorias = new ArrayList<>();
                for(Subcategoria sucba: subcategorias){
                    nombresSubcategorias.add(sucba.getNombre());
                }
                Collections.sort(nombresSubcategorias);
                ArrayAdapter<String> adapterSubcategoria = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresSubcategorias);
                spSubcategoria.setAdapter(adapterSubcategoria);

                int posSubcat = nombresSubcategorias.indexOf(producto.getSubcategoria().getNombre());
                spSubcategoria.setSelection(posSubcat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setTitle("Modificar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    if(!etPrecio.getText().toString().equals("")){
                        Double.parseDouble(etPrecio.getText().toString());
                    }
                    if(!etNombre.getText().toString().equals("")){
                        producto.setNombre(etNombre.getText().toString().toLowerCase());
                        producto.setDescripcion(etDescripcion.getText().toString());
                        producto.setMarca(etMarca.getText().toString().toLowerCase());
                        producto.setEtiqueta(etEtiqueta.getText().toString().toLowerCase());
                        Categoria categoria = manager.obtenerCategoria(spCategoria.getSelectedItem().toString());
                        producto.setCategoria(categoria);
                        Subcategoria subcategoria = manager.obtenerSubcategoria(spSubcategoria.getSelectedItem().toString());
                        producto.setSubcategoria(subcategoria);
                        producto.setPrecio(Double.parseDouble(etPrecio.getText().toString()));

                        manager.modificarProducto(producto);

                        actualizarLista();
                    }
                }catch (Exception e){
                    Toast.makeText(v.getContext(),R.string.error_precio,Toast.LENGTH_LONG).show();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                popUpEliminar(producto);
            }
        });
        builder.show();
    }

    public void popUpInsertar(final String tipo){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_categoria, null);

        final EditText et_categoria = v.findViewById(R.id.et_nombre_producto);

        if(tipo=="categoria"){
            builder.setTitle("Insertar Nueva Categoria");
        }else{
            builder.setTitle("Insertar Nueva Subcategoria");
            et_categoria.setHint("subcategoria");
        }
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(tipo=="categoria"){
                    Categoria categoria = new Categoria(et_categoria.getText().toString().toLowerCase());
                    long idCategoria = manager.insertarCategoria(categoria);
                    categoria.setId(String.valueOf(idCategoria));
                    nombreCategorias.add(et_categoria.getText().toString());
                    Collections.sort(nombreCategorias);
                }else{
                    String cat = spCategoria.getSelectedItem().toString();
                    spCategoria.getSelectedItemPosition();
                    Subcategoria subcategoria = new Subcategoria(et_categoria.getText().toString());
                    long idSubcategoria = manager.insertarSubcategoria(subcategoria);
                    subcategoria.setId(String.valueOf(idSubcategoria));
                    Categoria categoria = manager.obtenerCategoria(cat);
                    manager.insertarCategoria_Subcategoria(categoria,subcategoria);
                    nombresSubcategorias.add(subcategoria.getNombre());
                    Collections.sort(nombresSubcategorias);

                }

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminar(final Producto producto){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar producto");
        builder.setMessage("¿Desea eliminar el producto '" + producto.getNombre() + "'?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarProducto(producto);

                actualizarLista();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
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
