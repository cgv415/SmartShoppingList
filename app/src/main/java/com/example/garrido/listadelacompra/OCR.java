package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class OCR extends AppCompatActivity
        implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    private CompoundButton useFlash;
    private TextView tvLocal;
    //private TextView textValue;
    private ListView listView;
    private TextView tvFecha;
    private TextView tvHora;
    private TextView tvPrecio;
    private TextView tvPagar;
    private TextView tvInsertar;
    private Button btAceptar;



    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    private int status;
    private String idTicket;
    private DataBaseManager manager;

    private ArrayList<Producto> productos;
    private ArrayList<String> nombresProductos;
    private ArrayList<String> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new DataBaseManager(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status == 1){
                    Snackbar.make(view, "Ticket copiado a lista", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Intent intent = new Intent(view.getContext(), OcrCaptureActivity.class);
                    intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                    intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

                    startActivityForResult(intent, RC_OCR_CAPTURE);
                }

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem nav = menu.findItem(R.id.nav_tickets);
        nav.setChecked(true);

        Bundle bundle = getIntent().getExtras();

        status = bundle.getInt("status");
        tvLocal = findViewById(R.id.status_message);
        tvFecha = findViewById(R.id.et_fecha);
        tvHora = findViewById(R.id.et_hora);
        tvPrecio = findViewById(R.id.et_precio);
        tvPagar = findViewById(R.id.tv_pagar);
        tvInsertar = findViewById(R.id.tv_insertar);
        listView = findViewById(R.id.lv_ticket);


        useFlash = (CompoundButton) findViewById(R.id.use_flash);


        if(status == 1){
            idTicket = bundle.getString("idTicket");
            fab.setImageResource(R.drawable.ic_copy);
            Ticket ticket = manager.obtenerTicket(idTicket);
            ArrayList<Producto> productos = manager.obtenerFacts(idTicket);
            tvLocal.setText(ticket.getLocal().getNombre());

            tvFecha.setText(ticket.getFecha());

            tvHora.setText(ticket.getHora());

            tvPrecio.setText(String.format("%.2f",ticket.getTotal()));

            AdapterTicket adapter = new AdapterTicket(this,productos);
            listView.setAdapter(adapter);
            useFlash.setVisibility(View.INVISIBLE);

        }else{
            nombresProductos = bundle.getStringArrayList("productos");
            datos = bundle.getStringArrayList("datos");
            productos = new ArrayList<>();
            for(int i = 0 ; i < nombresProductos.size() ; i ++){
                Producto pr = new Producto();
                StringTokenizer tokenizer = new StringTokenizer(nombresProductos.get(i),"\t");
                pr.setNombre(tokenizer.nextToken());
                pr.setPrecio(Double.parseDouble(tokenizer.nextToken()));
                productos.add(pr);
            }

            tvLocal.setText(datos.get(0));
            tvFecha.setText(datos.get(1));
            tvHora.setText(datos.get(2));
            tvPrecio.setText(datos.get(3));
            tvPagar.setVisibility(View.VISIBLE);

            AdapterTicket adapter = new AdapterTicket(this,productos);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Producto producto = productos.get(i);
                    popUpProducto(producto);
                }
            });

                    /*String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    //textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                    */
        }

    }

    @Override
    public void onClick(View v) {
        if(v == tvLocal || v == tvFecha || v == tvHora){
            popUpDatos();
        }else if(v == tvPrecio){
            popUpPrecio();
        } else if (v == tvInsertar) {
            popUpInsertarProducto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Ya no sirve
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    nombresProductos = data.getStringArrayListExtra("productos");
                    datos = data.getStringArrayListExtra("datos");
                    productos = new ArrayList<>();
                    for(int i = 0 ; i < nombresProductos.size() ; i ++){
                        Producto pr = new Producto();
                        StringTokenizer tokenizer = new StringTokenizer(nombresProductos.get(i),"\t");
                        pr.setNombre(tokenizer.nextToken());
                        pr.setPrecio(Double.parseDouble(tokenizer.nextToken()));
                        productos.add(pr);
                    }

                    tvLocal.setText(datos.get(0));
                    tvFecha.setText(datos.get(1));
                    tvHora.setText(datos.get(2));
                    tvPrecio.setText(datos.get(3));
                    tvPagar.setVisibility(View.VISIBLE);

                    AdapterTicket adapter = new AdapterTicket(this,productos);
                    listView.setAdapter(adapter);

                    /*String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    //textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                    */
                } else {
                    tvLocal.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                tvLocal.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void popUpDatos() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_datos_ticket, null);

        final EditText et_nombre_local = v.findViewById(R.id.et_nombre_local);
        final EditText et_fecha = v.findViewById(R.id.et_fecha);
        final EditText et_hora = v.findViewById(R.id.et_hora);

        et_nombre_local.setText(tvLocal.getText());
        et_fecha.setText(tvFecha.getText());
        et_hora.setText(tvHora.getText());


        builder.setTitle("Modificar datos");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tvLocal.setText(et_nombre_local.getText());
                tvFecha.setText(et_fecha.getText());
                tvHora.setText(et_hora.getText());

            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpPrecio() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_precio_ticket, null);

        final EditText et_precio_ticket = v.findViewById(R.id.et_precio_ticket);


        et_precio_ticket.setText(tvPrecio.getText());



        builder.setTitle("Modificar precio");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tvPrecio.setText(et_precio_ticket.getText());


            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpProducto(final Producto producto) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_producto_ticket, null);

        final EditText et_nombre_producto = v.findViewById(R.id.et_nombre_producto);
        et_nombre_producto.setText(producto.getNombre());

        final EditText et_precio_producto = v.findViewById(R.id.et_precio_producto);
        et_precio_producto.setText(String.format("%.2f",producto.getPrecio()));



        builder.setTitle("Modificar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //int i = productos.indexOf(producto);
                producto.setNombre(et_nombre_producto.getText().toString());
                try{
                    producto.setPrecio(Double.parseDouble(et_precio_producto.getText().toString()));
                    actualizarPrecio();
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

    public void popUpInsertarProducto() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_producto_ticket, null);

        final EditText et_nombre_producto = v.findViewById(R.id.et_nombre_producto);

        final EditText et_precio_producto = v.findViewById(R.id.et_precio_producto);


        builder.setTitle("Insertar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Producto producto = new Producto();
                producto.setNombre(et_nombre_producto.getText().toString());
                try{
                    producto.setPrecio(Double.parseDouble(et_precio_producto.getText().toString()));
                    productos.add(producto);
                    actualizarPrecio();
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

    public void actualizarPrecio(){
        double precio = 0.0;
        for(Producto producto:productos){
            precio+=producto.getPrecio();
        }
        tvPrecio.setText(String.format("%.2f",precio));
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
        getMenuInflater().inflate(R.menu.ocr, menu);
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
        manager.gestionarMenu(item,this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
