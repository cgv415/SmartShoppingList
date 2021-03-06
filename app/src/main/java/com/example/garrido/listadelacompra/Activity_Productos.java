package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
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

    Spinner spCategoria;

    String agruparPor = "categoria";

    Button comparar;

    static int posCat = -1;
    static int posSubcat = -1;

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

        FloatingActionButton calcule = findViewById(R.id.fab_calcular);
        calcule.setVisibility(View.GONE);

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

                    //Mandar a listas
                    String nombreproducto = expandableListView.getItemAtPosition(i).toString();
                    Producto producto = manager.obtenerProductoByNombre(nombreproducto);
                    popUpInsertarEnLista(producto);

                }
                return true;
            }
        });
        buscador = findViewById(R.id.ac_productos);

        actualizarLista();

        arrayBuscador = manager.obtenerNombreProductos();
        int size = arrayBuscador.size();
        for(int i = 0; i < size; i++){
            Producto producto = manager.obtenerProductoByNombre(arrayBuscador.get(i));
            if(!arrayBuscador.contains(producto.getCategoria().getNombre())){
                arrayBuscador.add(producto.getCategoria().getNombre());
            }

            if(!arrayBuscador.contains(producto.getEtiqueta())){
                arrayBuscador.add(producto.getEtiqueta());
            }
        }

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
                String secuencia = charSequence.toString().toLowerCase();
                Map<String,ArrayList<String>> mapAux = new TreeMap<>();
                ArrayList<String> agrupacionAux = new ArrayList<>();
                ArrayList<String> value;

                if(charSequence.toString().equals("")){
                    actualizarLista();
                }else {
                    Set<Map.Entry<String, ArrayList<String>>> entrySet = map.entrySet();
                    for (Map.Entry<String, ArrayList<String>> entry : entrySet) {
                        ArrayList<String> nombresProductos = entry.getValue();
                        for (int j = 0; j < nombresProductos.size(); j++) {
                            Producto producto = manager.obtenerProductoByNombre(nombresProductos.get(j));
                            if (producto.getNombre().toLowerCase().contains(secuencia)
                                            || producto.getCategoria().getNombre().toLowerCase().contains(secuencia)
                                            || producto.getEtiqueta().toLowerCase().contains(secuencia)) {
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

    public void actualizarCategorias(){
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombreCategorias);
        spCategoria.setAdapter(adapterCategoria);

        if(posCat>-1){
            spCategoria.setSelection(posCat);
        }

    }

    public void popUpInsertarProducto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_producto, null);

        etNombre = v.findViewById(R.id.et_nombre_producto);
        etDescripcion = v.findViewById(R.id.et_descripcion);
        etEtiqueta = v.findViewById(R.id.et_alias);

        spCategoria = v.findViewById(R.id.sp_categoria);

        nombreCategorias = manager.obtenerNombreCategorias();
        Collections.sort(nombreCategorias);
        nombreCategorias.add("nueva categoria");

        actualizarCategorias();

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreCategoria = nombreCategorias.get(i);
                if(nombreCategoria.equals("nueva categoria")){
                    popUpInsertar("categoria");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //builder.setTitle("Insertar Producto");

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    Producto producto = new Producto();

                    if(!etNombre.getText().toString().equals("")){
                        producto.setNombre(etNombre.getText().toString().toLowerCase());
                        producto.setDescripcion(etDescripcion.getText().toString());
                        producto.setEtiqueta(etEtiqueta.getText().toString().toLowerCase());
                        Categoria categoria = manager.obtenerCategoria(spCategoria.getSelectedItem().toString());
                        producto.setCategoria(categoria);

                        manager.insertarProducto(producto);

                        actualizarLista();
                    }else{
                        Toast.makeText(v.getContext(),R.string.error_nombre,Toast.LENGTH_LONG).show();
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

        Button header = v.findViewById(R.id.btn_header);
        header.setText("Modificar producto");

        comparar = v.findViewById(R.id.btn_gestionar);

        comparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpCompararProducto(producto);
            }
        });

        etNombre = v.findViewById(R.id.et_nombre_producto);
        etNombre.setText(producto.getNombre());

        etDescripcion = v.findViewById(R.id.et_descripcion);
        etDescripcion.setText(producto.getDescripcion());

        etEtiqueta = v.findViewById(R.id.et_alias);
        etEtiqueta.setText(producto.getEtiqueta());


        spCategoria = v.findViewById(R.id.sp_categoria);

        nombreCategorias = manager.obtenerNombreCategorias();
        Collections.sort(nombreCategorias);
        nombreCategorias.add("nueva categoria");
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombreCategorias);
        spCategoria.setAdapter(adapterCategoria);

        int pos = nombreCategorias.indexOf(producto.getCategoria().getNombre());
        spCategoria.setSelection(pos);

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreCategoria = nombreCategorias.get(i);
                if(nombreCategoria.equals("nueva categoria")){
                    popUpInsertar("categoria");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(!etNombre.getText().toString().equals("")){
                    producto.setNombre(etNombre.getText().toString().toLowerCase());
                    producto.setDescripcion(etDescripcion.getText().toString());
                    producto.setEtiqueta(etEtiqueta.getText().toString().toLowerCase());
                    Categoria categoria = manager.obtenerCategoria(spCategoria.getSelectedItem().toString());
                    producto.setCategoria(categoria);

                    manager.modificarProducto(producto);

                    actualizarLista();
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

        final Button header = v.findViewById(R.id.btn_header);
        final EditText et_categoria = v.findViewById(R.id.et_nombre_producto);

        if(tipo=="categoria"){
            header.setText("Crear Categoria");
        }else{
            header.setText("Crear Subcategoria");
            et_categoria.setHint("subcategoria");
        }
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(tipo=="categoria"){
                    Categoria categoria = new Categoria(et_categoria.getText().toString().toLowerCase());
                    long idCategoria = manager.insertarCategoria(categoria);
                    categoria.setId(String.valueOf(idCategoria));
                    nombreCategorias.remove(nombreCategorias.size()-1);
                    String nombre = et_categoria.getText().toString().toLowerCase();
                    nombreCategorias.add(nombre);
                    Collections.sort(nombreCategorias);
                    nombreCategorias.add("nueva categoria");
                    posCat = nombreCategorias.indexOf(nombre);
                    actualizarCategorias();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpInsertarEnLista(final Producto producto){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_copiar_en_lista, null);
        builder.setTitle("Insertar en lista");
        builder.setCustomTitle(v);
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
                            manager.insertarProducto_Lista(producto,lista);
                        }
                        Toast.makeText(getApplicationContext(),"Producto insertado con exito!",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminar(final Producto producto){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.popup_mensaje, null);

        Button header = v.findViewById(R.id.btn_header);
        TextView mensaje = v.findViewById(R.id.tv_mensaje);

        header.setText("Eliminar producto");
        mensaje.setText("¿Desea eliminar el producto '" + producto.getNombre() + "'?");

        builder.setView(v);
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

    public void popUpCompararProducto(final Producto producto){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_comparar, null);
        builder.setView(v);

        ListView lvComparar = v.findViewById(R.id.lv_comparar);

        TreeMap<String,String> comparaciones = manager.obtenerLocales_Producto(producto.getNombre());

        AdapterComparar listAdapter2 = new AdapterComparar(this, comparaciones);
        lvComparar.setAdapter(listAdapter2);

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
