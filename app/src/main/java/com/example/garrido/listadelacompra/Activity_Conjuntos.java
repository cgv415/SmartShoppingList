package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.SubMenu;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Activity_Conjuntos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    ListView lista;
    ArrayList<String> nombresConjuntos;

    AutoCompleteTextView buscador;

    ExpandableListView expandableListView;
    ArrayList<String> agrupacion;
    Map<String,ArrayList<String>> map;
    ArrayList<Producto> productos;
    ArrayList<String> arrayBuscador;
    ArrayList<String> contraidos;
    android.widget.ExpandableListAdapter listAdapter;
    ArrayList<Conjunto> conjuntos;
    Conjunto conjunto;

    Spinner spTipo;


    String agruparPor = "categoria";
    TreeMap<String,ArrayList<String>> tachados = new TreeMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__conjuntos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);
        try{
            conjunto = manager.obtenerConjuntos().get(0);
            setTitle(conjunto.getNombre());
        }catch (Exception e){
            conjunto = new Conjunto();
            conjunto.setNombre("");
            setTitle("Conjuntos");
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conjunto.getNombre().equals("")){
                    popUpInsertar();
                }else{
                    popUpInsertarProductoEnConjunto();
                }
            }
        });

        FloatingActionButton copy = findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertarEnLista();
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
        MenuItem item = menu.findItem(R.id.nav_conjuntos);
        item.setChecked(true);

        nombresConjuntos = manager.obtenerNombreConjuntos();

        contraidos = new ArrayList<>();


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
                        arrayBuscador = conjunto.getNombreProductos();
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
                popUpEliminarDeConjunto(producto);

                return false;
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                contraidos.add(agrupacion.get(i));
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                contraidos.remove(agrupacion.get(i));
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //Quitar de la lista

                    String nombreproducto = expandableListView.getItemAtPosition(i).toString();
                    Producto producto = manager.obtenerProductoByNombre(nombreproducto);
                    popUpEliminarDeConjunto(producto);


                }
                return true;
            }
        });
        buscador = findViewById(R.id.ac_productos);

        actualizarLista();

        arrayBuscador = conjunto.getNombreProductos();
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
    }

    public void popUpInsertar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_conjunto, null);


        final EditText etNombre = v.findViewById(R.id.et_nombre_conjunto);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion_conjunto);

        builder.setTitle("Crear conjunto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Conjunto conjunto = new Conjunto();
                conjunto.setNombre(etNombre.getText().toString());
                conjunto.setDescripcion(etDescripcion.getText().toString());

                manager.insertarConjunto(conjunto);
                nombresConjuntos.add(conjunto.getNombre());

                finish();
                startActivity(getIntent());

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpInsertarProductoEnConjunto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_en_lista, null);

        final MultiAutoCompleteTextView buscadorInsertar = v.findViewById(R.id.atv_buscador);
        builder.setView(v);

        final ArrayList<String> nombresProductos = manager.obtenerNombreProductos();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, nombresProductos);
        buscadorInsertar.setAdapter(adapter);

        final Tokenizer t = new Tokenizer();
        buscadorInsertar.setThreshold(1);
        buscadorInsertar.setTokenizer(t);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),buscadorInsertar.getText().toString(),Toast.LENGTH_LONG).show();

                StringTokenizer tokenizer = new StringTokenizer(buscadorInsertar.getText().toString(),t.toString());
                while(tokenizer.hasMoreTokens()){
                    String token = tokenizer.nextToken();
                    if(!token.equals(" ")){
                        Producto producto = manager.obtenerProductoByNombre(token);
                        if(producto != null){
                            manager.insertarProducto_Conjunto(producto,conjunto);
                            conjunto.insertarProducto(producto);
                        }

                    }
                }
                actualizarLista();
                Toast.makeText(getApplicationContext(),"Productos insertados con exito!",Toast.LENGTH_LONG).show();

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminarDeConjunto(final Producto producto){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar producto");
        builder.setMessage("¿Desea eliminar el producto '" + producto.getNombre() + "' del conjunto?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarProducto_Conjunto(producto,conjunto);
                conjunto.eliminarProducto(producto);
                actualizarLista();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpModificarConjunto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_productos_conjuntos, null);

        final EditText etNombre = v.findViewById(R.id.et_nombre);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion);


        etNombre.setText(conjunto.getNombre());
        etDescripcion.setText(conjunto.getDescripcion());

        builder.setTitle("Modificar conjunto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Conjunto conjunto = new Conjunto();
                conjunto.setNombre(etNombre.getText().toString());
                conjunto.setDescripcion(etDescripcion.getText().toString());
                manager.modificarConjunto(conjunto);
                setTitle(conjunto.getNombre());
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminarConjunto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar Conjunto");
        builder.setMessage("¿Desea eliminar la conjunto '" + conjunto.getNombre() + "' y todos sus productos?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarConjunto(conjunto);

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpInsertarEnLista(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar en lista");
        //builder.setMessage("Selecciona las listas donde quieres insertar el producto");
        final ArrayList<String> listas = manager.obtenerNombreListas();

        String[] mStringArray = new String[listas.size()];
        mStringArray = listas.toArray(mStringArray);

        final ArrayList<Integer> mSelectedItems = new ArrayList<>();

        builder.setMultiChoiceItems(mStringArray, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for(int i = 0 ; i < mSelectedItems.size() ; i ++){
                            String nombre = listas.get(mSelectedItems.get(i));
                            Lista lista = manager.obtenerListaByNombre(nombre);
                            manager.insertarProductos_Lista(conjunto,lista);
                        }
                        Toast.makeText(getApplicationContext(),"Productos insertados con exito!",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
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
        productos = conjunto.getProductos();
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
        productos = conjunto.getProductos();
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

        productos = conjunto.getProductos();
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
            if(!contraidos.contains(agrupacion.get(j))){
                expandableListView.expandGroup(j);
            }
        }
    }

    public void limpiarConjunto(){
        manager.eliminarProductos_Conjunto(conjunto);
        conjunto.setProductos(new ArrayList<Producto>());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //action_search

        conjuntos = manager.obtenerConjuntos();

        MenuItem item = menu.findItem(R.id.action_listas);

        SubMenu submenu = item.getSubMenu();

        for(int i = 0 ; i < conjuntos.size(); i++){
            String nombre = conjuntos.get(i).getNombre();
            int id = Integer.parseInt(conjuntos.get(i).getId());
            submenu.add(1,id,id,nombre);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int group = item.getGroupId();
        contraidos.clear();

        if(group == 1){
            int id = item.getItemId();
            conjunto = manager.obtenerConjuntoById(String.valueOf(id));
            setTitle(conjunto.getNombre());
        }else {
            switch (item.getItemId()) {
                case R.id.a_z:
                    agruparPor = "orden";
                    break;
                case R.id.categoria:
                    agruparPor = "categoria";
                    break;
                case R.id.etiqueta:
                    agruparPor = "etiqueta";
                    break;
                case R.id.action_listas:
                    break;
                case R.id.action_add:
                    popUpInsertar();
                    break;
                case R.id.action_edit:
                    popUpModificarConjunto();
                    break;
                case R.id.action_eliminar:
                    popUpEliminarConjunto();
                    break;
                case R.id.action_limpiar:
                    limpiarConjunto();
                    break;
                default:

                    break;
            }
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
