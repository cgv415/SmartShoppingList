package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;

public class Activity_Tickets extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBaseManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__tickets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(),OCR.class);
                //intent.putExtra("estatico",false);

                popUp();
                //Intent intent = new Intent(view.getContext(), OcrCaptureActivity.class);
                //intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                //intent.putExtra(OcrCaptureActivity.UseFlash, false);
                //startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem nav = menu.findItem(R.id.nav_tickets);
        nav.setChecked(true);

        ListView lista = this.findViewById(R.id.listview_tickets);
        final ArrayList<Ticket> tickets = manager.obtenerTickets();
        final ArrayList<String> list = new ArrayList<>();
        for (Ticket ticket :
                tickets) {
            Local local = ticket.getLocal();
            list.add(local.getNombre() + "," + ticket.getFecha() + "," + ticket.getHora());
        }

        ArrayAdapter adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),"Ticket seleccionado",Toast.LENGTH_LONG).show();
                ArrayList<Producto> productos = manager.obtenerFacts(tickets.get(i).getIdTicket());

                Intent intent = new Intent(getApplicationContext(),OCR.class);
                intent.putExtra("status",1);
                intent.putExtra("idTicket",tickets.get(i).getIdTicket());
                startActivity(intent);
            }
        });
    }

    public void popUp() {
        // Use the Builder class for convenient dialog construction

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String [] opciones = {"Escanear Ticket","Crear Ticket Manual"};

        builder.setTitle("Crear un nuevo Ticket");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Intent intent = new Intent(builder.getContext(), OcrCaptureActivity.class);
                    intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                    intent.putExtra(OcrCaptureActivity.UseFlash, false);
                    startActivity(intent);
                }else{

                }
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__tickets, menu);
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
