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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Activity_Local extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> nombreLocales;
    ArrayList<Local> locales;
    Local local;
    DataBaseManager manager;
    AutoCompleteTextView buscador;

    android.widget.ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;

    ArrayList<String> agrupacion;
    ArrayList<String> contraidos;
    Map<String,ArrayList<Producto>> map;
    ArrayList<Producto> productos;
    ArrayList<String> arrayBuscador;

    Spinner spTipo;

    String agruparPor = "categoria";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__productos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);
        try{
            local = manager.obtenerLocales().get(0);
            setTitle(local.getNombre());
        }catch (Exception e){
            local = new Local();
            local.setNombre("");
            local.setProductos(new ArrayList<Producto>());
            setTitle("Sin locales");
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombreLocales.size() > 0){
                    popUpInsertarProductoEnLocal();
                }else{
                    popUpInsertarLocal();
                }
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
        MenuItem item = menu.findItem(R.id.nav_locales);
        item.setChecked(true);

        contraidos = new ArrayList<>();
        nombreLocales = manager.obtenerNombreLocales();

        expandableListView = findViewById(R.id.lv_dropdow);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(getApplicationContext(), agrupacion.get(groupPosition)+ ":" + map.get(agrupacion.get(groupPosition)).get(childPosition),Toast.LENGTH_SHORT).show();
                Producto producto = map.get(agrupacion.get(groupPosition)).get(childPosition);
                popUpModificarProducto(producto);
                return false;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //Quitar de la lista

                    String nombreproducto = expandableListView.getItemAtPosition(i).toString();
                    Producto producto = manager.obtenerProductoByNombre(nombreproducto);
                    popUpEliminarProductoDeLocal(producto);


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
                Map<String,ArrayList<Producto>> mapAux = new TreeMap<>();
                ArrayList<String> agrupacionAux = new ArrayList<>();
                String tipo = spTipo.getSelectedItem().toString();
                ArrayList<Producto> value;

                if(charSequence.toString().equals("")){
                    actualizarLista();
                }else {
                    Set<Map.Entry<String, ArrayList<Producto>>> entrySet = map.entrySet();
                    for (Map.Entry<String, ArrayList<Producto>> entry : entrySet) {
                        ArrayList<Producto> productosMap = entry.getValue();
                        for (int j = 0; j < productosMap.size(); j++) {
                            Producto producto =productosMap.get(j);
                            if (producto.getNombre().toLowerCase().contains(secuencia)
                                    || producto.getCategoria().getNombre().toLowerCase().contains(secuencia)
                                    || producto.getEtiqueta().toLowerCase().contains(secuencia)) {
                                if (!agrupacionAux.contains(entry.getKey())) {
                                    value = new ArrayList<>();
                                    agrupacionAux.add(entry.getKey());
                                    value.add(producto);
                                    mapAux.put(entry.getKey(), value);
                                } else {
                                    value = mapAux.get(entry.getKey());
                                    value.add(producto);
                                    mapAux.put(entry.getKey(), value);
                                }
                            }
                        }
                    }

                    listAdapter = new AdapterLocal(getApplicationContext(), agrupacionAux,mapAux);
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
        productos = local.getProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){
            Producto producto = productos.get(i);
            String letter = String.valueOf(producto.getNombre().charAt(0));
            if(!agrupacion.contains(letter)){
                agrupacion.add(letter);
            }
            ArrayList<Producto> p = new ArrayList<>();
            if(map.containsKey(letter)){
                p  = map.get(letter);
            }
            p.add(producto);
            map.put(letter,p);
        }

        actualizarAdapter();
    }

    public void actualizarListaPorEtiqueta(){
        productos = local.getProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){
            String etiqueta = productos.get(i).getEtiqueta();
            if(!agrupacion.contains(etiqueta)){
                agrupacion.add(etiqueta);
            }
            ArrayList<Producto> p = new ArrayList<>();
            if(map.containsKey(etiqueta)){
                p  = map.get(etiqueta);
            }
            p.add(productos.get(i));
            map.put(etiqueta,p);
        }

        actualizarAdapter();
    }

    public void actualizarListaPorCategoria(){
        productos = local.getProductos();
        agrupacion = new ArrayList<>();
        map = new TreeMap<>();

        for(int i = 0 ; i < productos.size() ; i ++){
            Categoria categoria = productos.get(i).getCategoria();
            if(!agrupacion.contains(categoria.getNombre())){
                agrupacion.add(categoria.getNombre());
            }
            Collections.sort(agrupacion);
            ArrayList<Producto> p = new ArrayList<>();
            if(map.containsKey(categoria.getNombre())){
                p  = map.get(categoria.getNombre());
            }
            p.add(productos.get(i));
            map.put(categoria.getNombre(),p);
        }

        actualizarAdapter();
    }

    public void actualizarAdapter(){
        listAdapter = new AdapterLocal(this, agrupacion,map);
        expandableListView.setAdapter(listAdapter);

        for(int j = 0; j < agrupacion.size() ; j ++){
            expandableListView.expandGroup(j);
        }
    }

    public void popUpInsertarLocal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_conjunto, null);

        final Button header = v.findViewById(R.id.btn_header);
        header.setText("Crear local");
        final EditText etNombre = v.findViewById(R.id.et_nombre_conjunto);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion_conjunto);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Local local = new Local();
                local.setNombre(etNombre.getText().toString());
                local.setDescripcion(etDescripcion.getText().toString());

                manager.insertarLocal(local);
                nombreLocales.add(local.getNombre());

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

    public void popUpModificarLocal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_productos_conjuntos, null);

        final EditText etNombre = v.findViewById(R.id.et_nombre);
        final EditText etDescripcion = v.findViewById(R.id.et_descripcion);


        etNombre.setText(local.getNombre());

        builder.setTitle("Modificar local");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Conjunto conjunto = new Conjunto();
                local.setNombre(etNombre.getText().toString());
                manager.modificarLocal(local);
                setTitle(local.getNombre());
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpInsertarProductoEnLocal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_en_local, null);

        final AutoCompleteTextView buscador2 = v.findViewById(R.id.atv_producto);

        ArrayList<String> arrayBuscador2 = manager.obtenerNombreProductos();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, arrayBuscador2);
        buscador2.setAdapter(adapter2);
        buscador2.setThreshold(1);

        final EditText etPrecio = v.findViewById(R.id.et_precio);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    Producto producto = manager.obtenerProductoByNombre(buscador2.getText().toString());
                    Double precio = Double.parseDouble(etPrecio.getText().toString());
                    producto.setPrecio(precio);
                    manager.insertarProducto_Local(producto,local,precio);
                    productos.add(producto);
                    local.setProductos(productos);
                    actualizarLista();
                }catch (Exception e){
                    e.printStackTrace();
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

        final View v = inflater.inflate(R.layout.popup_modificar_en_local, null);

        final AutoCompleteTextView buscador2 = v.findViewById(R.id.atv_producto);
        final EditText etPrecio = v.findViewById(R.id.et_precio);

        buscador2.setText(producto.getNombre());
        buscador2.setEnabled(false);

        etPrecio.setText(producto.getPrecio().toString());

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Double precio = Double.parseDouble(etPrecio.getText().toString());
                producto.setPrecio(precio);
                manager.modificarProducto_Local(producto,local,precio);

                int pos = productos.indexOf(producto);
                productos.set(pos,producto);
                local.setProductos(productos);
                actualizarLista();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        popUpEliminarProductoDeLocal(producto);
                    }
                });
        builder.show();
    }

    public void popUpEliminarProductoDeLocal(final Producto producto){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_mensaje, null);

        final Button header = v.findViewById(R.id.btn_header);
        header.setText("Eliminar de local");
        final TextView mensaje = v.findViewById(R.id.tv_mensaje);
        mensaje.setText("¿Desea eliminar el producto '" + producto.getNombre() + "' del local?");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarProducto_Local(producto, local);
                productos.remove(producto);
                local.setProductos(productos);

                actualizarLista();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminarLocal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_mensaje, null);

        final Button header = v.findViewById(R.id.btn_header);
        header.setText("Eliminar Local");
        final TextView mensaje = v.findViewById(R.id.tv_mensaje);
        mensaje.setText("¿Desea eliminar el local '" + local.getNombre() + "' y todos sus productos?");
        builder.setView(v);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarLocal(local);

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpLimpiar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.popup_mensaje, null);

        Button header = v.findViewById(R.id.btn_header);
        header.setText("Limpiar local");
        TextView mensaje = v.findViewById(R.id.tv_mensaje);

        mensaje.setText("¿Desea limpiar el local '" + local.getNombre() + "'? \n(Esto eliminará todos sus productos)");

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                limpiarLocal();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //action_search

        locales = manager.obtenerLocales();

        MenuItem item = menu.findItem(R.id.action_listas);

        SubMenu submenu = item.getSubMenu();

        for(int i = 0 ; i < locales.size(); i++){
            String nombre = locales.get(i).getNombre();
            int id = Integer.parseInt(locales.get(i).getId());
            submenu.add(1,id,id,nombre);
        }

        return true;
    }

    public void limpiarLocal(){
        manager.eliminarProductos_Local(local);
        local.setProductos(new ArrayList<Producto>());
        actualizarLista();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int group = item.getGroupId();
        contraidos.clear();

        if(group == 1){
            int id = item.getItemId();
            local = manager.obtenerLocalById(String.valueOf(id));
            setTitle(local.getNombre());
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
                    popUpInsertarLocal();
                    break;
                case R.id.action_edit:
                    popUpModificarLocal();
                    break;
                case R.id.action_eliminar:
                    popUpEliminarLocal();
                    break;
                case R.id.action_limpiar:
                    popUpLimpiar();
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
