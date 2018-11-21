package com.example.garrido.listadelacompra;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Activity_Import extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    ListView listView;
    ArrayList<String> opciones;
    CheckBox seleccionarTodo;
    AdapterImport adapter;

    public static String ruta;
    public static String nombreArchivo;
    String tipo;
    String archivo;

    int permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__import);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            popupPermisos();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    exportar();
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
        MenuItem nav = menu.findItem(R.id.nav_import);
        nav.setChecked(true);

        manager = new DataBaseManager(this);

        tipo = "export";
        listView = findViewById(R.id.listView);
        opciones = new ArrayList<>();

        opciones.add("Categorias");
        opciones.add("Productos");
        opciones.add("Listas");
        opciones.add("Conjuntos");
        opciones.add("Locales");
        opciones.add("Tickets");

        actualizarLista(false);

        listView.setItemsCanFocus(true);

        seleccionarTodo = findViewById(R.id.cb_selectTodo);
        seleccionarTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    actualizarLista(true);
                }
            }
        });
    }

    public void popupPermisos(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permiso de lectura/escritura");
        builder.setMessage("Es necesario dar permisos de lectura/almacenamiento para acceder a esta funcionalidad");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

    public void actualizarLista(boolean todos){
        adapter = new AdapterImport(this,opciones,todos);
        listView.setAdapter(adapter);
    }

    public void exportar(){
        archivo = "";
        ArrayList<String> checked = adapter.getChecked();
        String finChecked = "";
        try{
           finChecked  = checked.get(checked.size()-1);

            archivo +="{";
            for(String check
                    : checked){
                archivo += exportar(check);
                if(!finChecked.equals(check)){
                    archivo +=",";
                }
            }
            archivo +="}";

            tipo = "export";
            Intent intent = new Intent(this,Explorador.class);
            intent.putExtra("tipo","export");
            startActivity(intent);


        }catch (Exception e){
            Toast.makeText(this,"Debes seleccionar al menos una opcion.",Toast.LENGTH_LONG).show();
        }

    }

    public String exportar(String opcion){
        String exportado = "";
        switch (opcion){
            case "Sub/Categorias":
                exportado += "'categoria':{";
                ArrayList<Categoria> categorias = manager.obtenerCategorias();
                Categoria finCategorias = categorias.get(categorias.size()-1);
                for(Categoria categoria : categorias){
                    exportado += categoria.toString();
                    if(!finCategorias.getNombre().equals(categoria.getNombre())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
            case "Productos":
                exportado += "'producto':{";
                ArrayList<Producto> productos = manager.obtenerProductos();
                Producto finProductos = productos.get(productos.size()-1);
                for(Producto producto : productos){
                    exportado += producto.toString();
                    if(!finProductos.getNombre().equals(producto.getNombre())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
            case "Listas":
                exportado += "'lista':{";
                ArrayList<Lista> listas = manager.obtenerListas();
                Lista finListas = listas.get(listas.size()-1);
                for(Lista lista : listas){
                    exportado += lista.toString();
                    if(!finListas.getNombre().equals(lista.getNombre())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
            case "Conjuntos":
                exportado += "'conjunto':{";
                ArrayList<Conjunto> conjuntos = manager.obtenerConjuntos();
                Conjunto finConjuntos = conjuntos.get(conjuntos.size()-1);
                for(Conjunto conjunto:conjuntos){
                    exportado += conjunto.toString();
                    if(!finConjuntos.getNombre().equals(conjunto.getNombre())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
            case "Locales":
                exportado += "'local':{";
                ArrayList<Local> locales = manager.obtenerLocales();
                Local finLocales = locales.get(locales.size()-1);
                for(Local local:locales){
                    exportado += local.toString();
                    if(!finLocales.getNombre().equals(local.getNombre())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
            case "Tickets":
                exportado += "'ticket':{";
                ArrayList<Ticket> tickets = manager.obtenerTickets("fecha");
                Ticket finTickets = tickets.get(tickets.size()-1);
                for(Ticket ticket:tickets){
                    exportado += ticket.toString();
                    if(!finTickets.getIdTicket().equals(ticket.getIdTicket())){
                        exportado += ",";
                    }
                }
                exportado += "}";
                break;
        }

        return exportado;
    }

    public void escribirTexto(){
        try {
            File fav;
            //fav = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
            //"/Android/data/com.example.garrido.readtextfile/","fichero.txt");

            fav = new File(ruta,nombreArchivo);

            //fav.createNewFile();
            OutputStreamWriter fout1 = new OutputStreamWriter(new FileOutputStream(fav));

            fout1.write(archivo);
            fout1.close();

            Toast.makeText(this,fav.getAbsolutePath(),Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void leerTexto(){
        try{

            //TODO Falta insertar en la base de datos

            File file;
            file = new File(ruta,nombreArchivo);

            BufferedReader fin = new BufferedReader(new FileReader(file));

            String txt = fin.readLine();
            fin.close();

            JSONObject object = new JSONObject(txt);

            Iterator<String> it = object.keys();

            while(it.hasNext()){
                String key = it.next();
                JSONObject value = (JSONObject) object.get(key);
                ArrayList<Producto> productos;
                JSONArray jsonArray;
                switch (key){
                    case "categoria":
                        jsonArray = value.names();

                        ArrayList<Categoria> categorias = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            ArrayList<Subcategoria> subcategorias = new ArrayList<>();
                            Categoria categoria = new Categoria();

                            String nombreCategoria = (String) jsonArray.get(i);
                            JSONArray nombresSubcategorias = value.getJSONArray(nombreCategoria);
                            for(int j = 0; j <nombresSubcategorias.length(); j++){
                                String nombreSub = (String) nombresSubcategorias.get(j);
                                Subcategoria subcategoria = new Subcategoria(nombreSub);
                                subcategorias.add(subcategoria);
                            }
                            categoria.setNombre(nombreCategoria);
                            categoria.setSubcategorias(subcategorias);

                            categorias.add(categoria);
                        }

                        for(Categoria categoria: categorias) {
                            manager.insertarCategoria(categoria);
                            ArrayList<Subcategoria> subcategorias = categoria.getSubcategorias();
                            for (Subcategoria subcategoria : subcategorias) {
                                manager.insertarSubcategoria(subcategoria,categoria);
                            }
                        }
                        break;
                    case "producto":
                        jsonArray = value.names();
                        productos = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            Producto producto = new Producto();
                            String nombreProducto =(String) jsonArray.get(i);
                            JSONObject JSONproducto = value.getJSONObject(nombreProducto);

                            String nombreCategoria = JSONproducto.getString("categoria");
                            Categoria categoria = manager.obtenerCategoria(nombreCategoria);

                            String nombreSubcategoria = JSONproducto.getString("subcategoria");
                            Subcategoria subcategoria = manager.obtenerSubcategoria(nombreSubcategoria);

                            producto.setNombre(nombreProducto);
                            producto.setDescripcion(JSONproducto.getString("descripcion"));
                            producto.setEtiqueta(JSONproducto.getString("etiqueta"));
                            producto.setMarca(JSONproducto.getString("marca"));
                            producto.setCategoria(categoria);
                            producto.setSubcategoria(subcategoria);

                            productos.add(producto);
                        }

                        for(Producto producto:productos){
                            manager.insertarProducto(producto);
                        }
                        break;
                    case "lista":
                        jsonArray = value.names();
                        ArrayList<Lista> listas = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            Lista lista = new Lista();
                            String nombreLista =(String) jsonArray.get(i);
                            JSONObject JSONLista = value.getJSONObject(nombreLista);

                            lista.setNombre(nombreLista);
                            lista.setDescripcion(JSONLista.getString("descripcion"));
                            lista.setPrincipal(JSONLista.getBoolean("principal"));

                            JSONObject JSONProductos = JSONLista.getJSONObject("producto");
                            JSONArray nombresProductos = JSONProductos.names();
                            productos = new ArrayList<>();
                            for(int j = 0; j < nombresProductos.length(); j++){
                                Producto producto = new Producto();
                                String nombreProducto = (String) nombresProductos.get(j);
                                JSONObject JSONproducto = (JSONObject) JSONProductos.get(nombreProducto);

                                String nombreCategoria = JSONproducto.getString("categoria");
                                Categoria categoria = manager.obtenerCategoria(nombreCategoria);

                                String nombreSubcategoria = JSONproducto.getString("subcategoria");
                                Subcategoria subcategoria = manager.obtenerSubcategoria(nombreSubcategoria);

                                producto.setNombre(nombreProducto);
                                producto.setDescripcion(JSONproducto.getString("descripcion"));
                                producto.setEtiqueta(JSONproducto.getString("etiqueta"));
                                producto.setMarca(JSONproducto.getString("marca"));
                                producto.setCategoria(categoria);
                                producto.setSubcategoria(subcategoria);

                                productos.add(producto);
                            }
                            lista.setProductos(productos);

                            listas.add(lista);
                        }

                        for(Lista lista:listas){
                            manager.insertarLista(lista);
                        }
                        break;
                    case "conjunto":
                        jsonArray = value.names();
                        ArrayList<Conjunto> conjuntos = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            Conjunto conjunto = new Conjunto();
                            String nombreConjunto =(String) jsonArray.get(i);
                            JSONObject JSONConjunto = value.getJSONObject(nombreConjunto);

                            conjunto.setNombre(nombreConjunto);
                            conjunto.setDescripcion(JSONConjunto.getString("descripcion"));

                            JSONObject JSONProductos = JSONConjunto.getJSONObject("producto");
                            JSONArray nombresProductos = JSONProductos.names();
                            productos = new ArrayList<>();
                            for(int j = 0; j < nombresProductos.length(); j++){
                                Producto producto = new Producto();
                                String nombreProducto = (String) nombresProductos.get(j);
                                JSONObject JSONproducto = (JSONObject) JSONProductos.get(nombreProducto);

                                String nombreCategoria = JSONproducto.getString("categoria");
                                Categoria categoria = manager.obtenerCategoria(nombreCategoria);

                                String nombreSubcategoria = JSONproducto.getString("subcategoria");
                                Subcategoria subcategoria = manager.obtenerSubcategoria(nombreSubcategoria);

                                producto.setNombre(nombreProducto);
                                producto.setDescripcion(JSONproducto.getString("descripcion"));
                                producto.setEtiqueta(JSONproducto.getString("etiqueta"));
                                producto.setMarca(JSONproducto.getString("marca"));
                                producto.setCategoria(categoria);
                                producto.setSubcategoria(subcategoria);

                                productos.add(producto);
                            }
                            conjunto.setProductos(productos);

                            conjuntos.add(conjunto);
                        }

                        for(Conjunto conjunto:conjuntos){
                            manager.insertarConjunto(conjunto);
                        }
                        break;
                    case "local":
                        jsonArray = value.names();
                        ArrayList<Local> locales = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            Local local = new Local();
                            String nombreLocal =(String) jsonArray.get(i);
                            JSONObject JSONLocal = value.getJSONObject(nombreLocal);
                            local.setNombre(nombreLocal);

                            JSONArray nombresProductos = JSONLocal.names();

                            productos = new ArrayList<>();
                            for(int j = 0; j < nombresProductos.length(); j++){
                                Producto producto;
                                String nombreProducto = (String) nombresProductos.get(j);
                                producto = manager.obtenerProductoByNombre(nombreProducto);
                                producto.setPrecio(JSONLocal.getDouble(nombreProducto));

                                productos.add(producto);
                            }
                            local.setProductos(productos);

                            locales.add(local);
                        }

                        for(Local local:locales){
                            manager.insertarLocal(local);
                        }
                        break;
                    case "ticket":
                        jsonArray = value.names();
                        ArrayList<Ticket> tickets = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            Ticket ticket = new Ticket();
                            String nombreTicket =(String) jsonArray.get(i);
                            JSONObject JSONTicket = value.getJSONObject(nombreTicket);

                            ticket.setFecha(JSONTicket.getString("fecha"));
                            ticket.setHora(JSONTicket.getString("hora"));
                            String nombreLocal = JSONTicket.getString("local");
                            Local local = manager.obtenerLocal(nombreLocal);
                            ticket.setLocal(local);
                            ticket.setTotal(JSONTicket.getDouble("total"));


                            JSONObject JSONProductos = JSONTicket.getJSONObject("producto");
                            JSONArray nombresProductos = JSONProductos.names();
                            productos = new ArrayList<>();
                            for(int j = 0; j < nombresProductos.length(); j++){
                                Producto producto;
                                String nombreProducto = (String) nombresProductos.get(j);
                                producto = manager.obtenerProductoByNombre(nombreProducto);
                                producto.setPrecio(JSONProductos.getDouble(nombreProducto));

                                productos.add(producto);
                            }
                            ticket.setProductos(productos);

                            tickets.add(ticket);
                        }

                        for(Ticket ticket:tickets){
                            manager.insertarTicket(ticket);
                        }
                        break;
                }
            }

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
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
    protected void onResume() {
        super.onResume();

        if(ruta != null){
            if(tipo.equals("import")){
                leerTexto();
            }else if(tipo.equals("export")){
                escribirTexto();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_import) {
            if (permission == PackageManager.PERMISSION_GRANTED) {
                tipo="import";
                Intent intent = new Intent(this,Explorador.class);
                intent.putExtra("tipo","import");
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
