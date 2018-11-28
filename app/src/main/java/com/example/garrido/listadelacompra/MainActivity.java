package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
    ArrayList<String> contraidos;
    android.widget.ExpandableListAdapter listAdapter;
    ArrayList<Lista> listas;

    Spinner spTipo;

    Lista lista;

    String agruparPor = "categoria";
    TreeMap<String,ArrayList<String>> tachados = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__productos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);
        manager.actualizar();

        FloatingActionButton calcular = findViewById(R.id.fab_calcular);
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpElegirLocal();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lista.getNombre().equals("")){
                    popUpInsertarLista();
                }else{
                    popUpInsertarProductoEnLista();
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

        if(getIntent().hasExtra("lista")){
            String nombreLista = getIntent().getStringExtra("lista");
            lista = manager.obtenerListaByNombre(nombreLista);
        }else{
            lista = manager.obtenerListaPrincipal();
        }

        if(lista.getNombre().equals("")){
            popUpInsertarLista();
        }else{
            this.setTitle(lista.getNombre());
        }

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
                        arrayBuscador = lista.getNombreProductos();
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
                    popUpEliminarDeLista(producto);


                }
                return true;
            }
        });
        buscador = findViewById(R.id.ac_productos);

        actualizarLista();

        arrayBuscador = lista.getNombreProductos();
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

    public void popUpElegirLocal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_elegir_local, null);
        ArrayList<String> nombresLocales = manager.obtenerNombreLocales();
        final Spinner sp_local = v.findViewById(R.id.sp_local);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,nombresLocales);

        sp_local.setAdapter(adapter);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String nombreLocal =sp_local.getSelectedItem().toString();
                calcular(nombreLocal);
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void calcular(String nombreLocal){
        double total = 0.0;

        Local l = manager.obtenerLocal(nombreLocal);
        ArrayList<Producto> productos_local = manager.obtenerLocal_Productos(l);

        for(int i = 0 ; i < productos_local.size() ; i++){
            Producto producto = productos_local.get(i);
            if(productos.contains(producto)){
                total += producto.getPrecio();
                productos.get(i).setPrecio(producto.getPrecio());
            }
        }

        for(int i = 0 ; i < productos.size(); i++){
            Producto producto = productos.get(i);
            if(!productos_local.contains(producto)){
                productos.get(i).setPrecio(null);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_lista_calcular, null);
        Button b = v.findViewById(R.id.btn_header);
        b.setText(nombreLocal);

        ArrayList<Producto> productosCalculados = productos;
        final Producto productoTotal = new Producto("Total",total);
        if(!productosCalculados.contains(productoTotal)){
            productosCalculados.add(productoTotal);
        }else{
            int pos = productosCalculados.indexOf(productoTotal);
            productosCalculados.set(pos,productoTotal);
        }

        final ListView lv_calcular = v.findViewById(R.id.lv_calcular);
        AdapterContenidoTicket ad = new AdapterContenidoTicket(this,productos);

        lv_calcular.setAdapter(ad);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                productos.remove(productoTotal);
            }
        });
        builder.show();
    }

    public void popUpInsertarLista(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_lista, null);

        final EditText etNombre = v.findViewById(R.id.et_nombre_lista);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion);
        final CheckBox cbPrincipal = v.findViewById(R.id.cb_principal);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                lista.setNombre(etNombre.getText().toString());
                lista.setDescripcion(etDescripcion.getText().toString());
                if(manager.obtenerListas().size() > 0){
                    lista.setPrincipal(cbPrincipal.isChecked());
                }else{
                    lista.setPrincipal(true);
                }

                long idLista = manager.insertarLista(lista);
                lista.setId(String.valueOf(idLista));

                //setTitle(lista.getNombre());
                //actualizarLista();

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

    public void popUpModificar(final Lista lista){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_lista, null);


        final EditText etNombre = v.findViewById(R.id.et_nombre_lista);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion);
        final CheckBox cbPrincipal = v.findViewById(R.id.cb_principal);

        etNombre.setText(lista.getNombre());
        etDescripcion.setText(lista.getDescripcion());
        cbPrincipal.setChecked(lista.isPrincipal());

        Button header = v.findViewById(R.id.btn_header);
        header.setText("modificar lista");

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                lista.setNombre(etNombre.getText().toString());
                lista.setDescripcion(etDescripcion.getText().toString());
                if(listas.size()==0){
                    lista.setPrincipal(true);
                }else{
                    lista.setPrincipal(cbPrincipal.isChecked());
                }

                manager.modificarLista(lista);

                setTitle(lista.getNombre());

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminar(final Lista lista){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.popup_eliminar, null);

        Button header = v.findViewById(R.id.btn_header);
        TextView mensaje = v.findViewById(R.id.tv_mensaje);

        header.setText("Eliminar lista");
        mensaje.setText("¿Desea eliminar la lista '" + lista.getNombre() + "' y todos sus productos?");

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarLista(lista);
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

    public void popUpInsertarProductoEnLista(){

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

                        ArrayList<Producto> antiguosProductos = (ArrayList<Producto>) lista.getProductos().clone();

                        StringTokenizer tokenizer = new StringTokenizer(buscadorInsertar.getText().toString(),t.toString());
                        while(tokenizer.hasMoreTokens()){
                            String token = tokenizer.nextToken();
                            Producto producto = manager.obtenerProductoByNombre(token);
                            ArrayList<Producto> productos = lista.getProductos();
                            productos.add(producto);
                            lista.setProductos(productos);
                        }
                        for(int i = 0 ; i < lista.getProductos().size() ; i++){
                            Producto producto = lista.getProductos().get(i);
                            if(!antiguosProductos.contains(producto)){
                                manager.insertarProducto_Lista(producto,lista);
                            }
                        }
                        actualizarLista();
                        Toast.makeText(getApplicationContext(),buscadorInsertar.getText().toString(),Toast.LENGTH_LONG).show();
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

    public void popUpEliminarDeLista(final Producto producto){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar producto");
        builder.setMessage("¿Desea eliminar el producto '" + producto.getNombre() + "' de la lista?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarProducto_Lista(producto,lista);
                ArrayList<Producto> productos = lista.getProductos();
                int pos = -1;
                for(int i = 0 ; i < productos.size(); i++){
                    if(producto.getNombre().equals(productos.get(i).getNombre())){
                        pos = i;
                    }
                }
                if(pos > -1){
                    productos.remove(pos);
                }
                lista.setProductos(productos);
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
        productos = lista.getProductos();
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
        productos = lista.getProductos();
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

        productos = lista.getProductos();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //action_search

        listas = manager.obtenerListas();

        MenuItem item = menu.findItem(R.id.action_listas);

        SubMenu submenu = item.getSubMenu();

        for(int i = 0 ; i < listas.size(); i++){
            String nombre = listas.get(i).getNombre();
            int id = Integer.parseInt(listas.get(i).getId());
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
            lista = manager.obtenerListaById(String.valueOf(id));
            setTitle(lista.getNombre());
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
                    popUpInsertarLista();
                    break;
                case R.id.action_edit:
                    popUpModificar(lista);
                    break;
                case R.id.action_eliminar:
                    popUpEliminar(lista);
                    break;
                case R.id.action_limpiar:
                    limpiarLista();
                    break;
                default:

                    break;
            }
        }

        actualizarLista();

        return super.onOptionsItemSelected(item);
    }

    public void limpiarLista(){
        manager.eliminarProductos_Lista(lista);
        lista.setProductos(new ArrayList<Producto>());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        finish();

        manager.gestionarMenu(item,this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
