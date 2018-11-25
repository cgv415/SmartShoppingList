package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class Activity_Listas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    private ArrayList<String> listas;
    private ListView lista;
    private EditText etNombre;
    private EditText etDescripcion;
    private CheckBox cbPrincipal;

//TODO SIN USO ACTUAL, REEMPLAZADO POR MAINACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new DataBaseManager(this);
        setContentView(R.layout.activity__listas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInsertar();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listas = manager.obtenerNombreListas();
        lista = findViewById(R.id.listview_listas);

        ArrayAdapter adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listas);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombre = listas.get(i);
                Lista lista = manager.obtenerListaByNombre(nombre);
                popUpModificar(lista);
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                finish();
                Intent intent = new Intent(view.getContext(),MainActivity.class);
                intent.putExtra("lista",listas.get(i));
                startActivity(intent);
                return false;
            }
        });

    }

    public void popUpInsertar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_insertar_lista, null);


        etNombre = v.findViewById(R.id.et_nombre_lista);
        etDescripcion = v.findViewById(R.id.et_descripcion);
        cbPrincipal = v.findViewById(R.id.cb_principal);

        builder.setTitle("Insertar Lista");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Lista lista = new Lista();
                lista.setNombre(etNombre.getText().toString());
                lista.setDescripcion(etDescripcion.getText().toString());
                if(listas.size()==0){
                    lista.setPrincipal(true);
                }else{
                    lista.setPrincipal(cbPrincipal.isChecked());
                }


                long idLista = manager.insertarLista(lista);
                lista.setId(String.valueOf(idLista));

                listas.add(etNombre.getText().toString());
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


        etNombre = v.findViewById(R.id.et_nombre_lista);
        etDescripcion = v.findViewById(R.id.et_descripcion);
        cbPrincipal = v.findViewById(R.id.cb_principal);

        etNombre.setText(lista.getNombre());
        etDescripcion.setText(lista.getDescripcion());
        cbPrincipal.setChecked(lista.isPrincipal());

        builder.setTitle("Modificar Lista");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int pos = listas.indexOf(lista.getNombre());
                lista.setNombre(etNombre.getText().toString());
                lista.setDescripcion(etDescripcion.getText().toString());
                if(listas.size()==0){
                    lista.setPrincipal(true);
                }else{
                    lista.setPrincipal(cbPrincipal.isChecked());
                }

                manager.modificarLista(lista);

                listas.set(pos,lista.getNombre());

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__listas, menu);
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
}
