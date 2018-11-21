package com.example.garrido.listadelacompra;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;

import java.util.ArrayList;

public class Activity_Reinicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DataBaseManager manager;

    ListView listView;
    ArrayList<String> opciones;
    CheckBox seleccionarTodo;
    AdapterImport adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__reinicio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reiniciar();
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
        MenuItem nav = menu.findItem(R.id.nav_reinicio);
        nav.setChecked(true);

        manager = new DataBaseManager(this);

        listView = findViewById(R.id.listView);
        opciones = new ArrayList<>();

        opciones.add("categoria");
        opciones.add("producto");
        opciones.add("lista");
        opciones.add("conjunto");
        opciones.add("local");
        opciones.add("ticket");

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

    public void actualizarLista(boolean todos){
        adapter = new AdapterImport(this,opciones,todos);
        listView.setAdapter(adapter);
    }

    public void reiniciar(){
        ArrayList<String> checked = adapter.getChecked();

        for(String check : checked){
            switch (check){
                case "categoria":
                    manager.crearTablaCategoria();
                    manager.crearTablaSubcategoria();
                    manager.crearTablaCategoria_Subcategoria();
                case "producto":
                    manager.crearTablaProducto();
                    manager.crearTablaProducto_Local();
                    manager.crearTablaProducto_Conjunto();
                    manager.crearTablaProducto_Categoria();
                    manager.crearTablaProducto_Subcategoria();
                    manager.crearTablaProducto_Lista();
                    manager.crearTablaFact();

                    manager.crearTablaLista();
                    manager.crearTablaConjunto();
                    manager.crearTablaLocal();
                    manager.crearTablaTicket();
                    break;
                case "lista":
                    manager.crearTablaLista();
                    break;
                case "conjunto":
                    manager.crearTablaConjunto();
                    break;
                case "local":
                    manager.crearTablaLocal();
                    break;
                case "ticket":
                    manager.crearTablaTicket();
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__reinicio, menu);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        finish();

        manager.gestionarMenu(item,this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
