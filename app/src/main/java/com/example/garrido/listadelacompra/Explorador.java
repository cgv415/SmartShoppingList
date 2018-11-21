package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Explorador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    DataBaseManager manager;

    private List<String> rutasArchivos;
    private String directorioRaiz;
    private TextView carpetaActual;
    private ListView listas;

    private final String rutaActual = "";

    private String camino;

    private String tipo;

    private TextView nombreArchivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nombreArchivo.getText().toString().equals("")) {
                    Activity_Import.ruta = camino ;
                    Activity_Import.nombreArchivo = nombreArchivo.getText().toString();
                    finish();
                }else{
                    String mensaje = "";
                    if(tipo.equals("import")){
                        mensaje = "Debes seleccionar un archivo para abrir";
                    }else{
                        mensaje = "Debes seleccionar un nombre de archivo para descargar";
                    }
                 Toast.makeText(view.getContext(),mensaje,Toast.LENGTH_LONG).show();
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

        Bundle bundle = getIntent().getExtras();
        tipo = bundle.getString("tipo");

        nombreArchivo = findViewById(R.id.et_nombre_archivo);
        nombreArchivo.setClickable(true);
        nombreArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpNombreArchivo();
            }
        });
        if(tipo.equals("import")){
            nombreArchivo.setText("");
        }


        carpetaActual = findViewById(R.id.rutaActual);
        listas = findViewById(R.id.listview_Lista);

        directorioRaiz = Environment.getExternalStorageDirectory().getPath();
        carpetaActual.setText(String.format("%s%s", rutaActual, directorioRaiz));

        listas.setOnItemClickListener(this);
        verDirectorio(directorioRaiz);
    }

    public void popUpNombreArchivo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_nombre_archivo, null);

        final EditText etTexto = v.findViewById(R.id.et_texto);
        etTexto.setText(nombreArchivo.getText().toString());


        builder.setTitle("Nombre de archivo");
        builder.setView(v);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                nombreArchivo.setText(etTexto.getText().toString());
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.explorador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

    public void verDirectorio(String rutaDirectorio){
        List<String> nombresArchivos = new ArrayList<>();
        rutasArchivos = new ArrayList<>();

        int count = 0;

        File directorioActual = new File(rutaDirectorio);
        File[] listaArchivos = directorioActual.listFiles();

        if(!rutaDirectorio.equals(directorioRaiz)){
            nombresArchivos.add("../");
            rutasArchivos.add(directorioActual.getParent());
            count = 1;
        }

        for(File archivo
                : listaArchivos){
            if(tipo.equals("export")){
                if(archivo.isDirectory()){
                    rutasArchivos.add(archivo.getPath());
                }
            }else{
                rutasArchivos.add(archivo.getPath());
            }
        }

        Collections.sort(rutasArchivos,String.CASE_INSENSITIVE_ORDER);

        for(int i = count; i < rutasArchivos.size(); i++){
            File archivo = new File(rutasArchivos.get(i));
            if(archivo.isFile()){
                nombresArchivos.add(archivo.getName());
            }else{
                nombresArchivos.add("/" + archivo.getName());
            }
        }

        if(listaArchivos.length < 1){
            nombresArchivos.add("No hay ningun archivo");
            rutasArchivos.add(rutaDirectorio);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.explorador_lista_archivos, nombresArchivos);
        listas.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        File archivo = new File(rutasArchivos.get(i));

        if(archivo.isFile()){
            //Toast.makeText(this,"Ruta: " + camino,Toast.LENGTH_LONG).show();
            if(tipo.equals("import")){
                //Activity_Import.ruta = camino;
                //finish();
                nombreArchivo.setText(archivo.getName());
            }
        }else{

            carpetaActual.setText(String.format("%s%s", rutaActual, rutasArchivos.get(i)));
            camino = rutasArchivos.get(i);
            verDirectorio(rutasArchivos.get(i));
        }
    }
}
