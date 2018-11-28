package com.example.garrido.listadelacompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class OCR extends AppCompatActivity
        implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    private CompoundButton useFlash;
    private TextView tvLocal;
    //private TextView textValue;
    private ListView listView;
    private TextView tvFecha;
    private TextView tvHora;
    private TextView tvPagar;
    private Button btAceptar;

    private String local;
    private String fecha;
    private String hora;
    private String total;

    final private String INSERTAR = "Insertar nuevo producto";
    final private String PRECIO_TOTAL = "Precio total";
    private ArrayList<Producto> insertarProducto;

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

        FloatingActionButton fab_aceptar = findViewById(R.id.fab_aceptar);
        fab_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptarTicket();
            }
        });

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem nav = menu.findItem(R.id.nav_tickets);
        nav.setChecked(true);

        Bundle bundle = getIntent().getExtras();

        insertarProducto = new ArrayList<>();
        Producto espacio = new Producto();
        espacio.setNombre("");
        espacio.setPrecio(0.0);
        insertarProducto.add(espacio);

        Producto precioTotal = new Producto();
        precioTotal.setNombre(PRECIO_TOTAL);
        precioTotal.setPrecio(0.0);
        insertarProducto.add(precioTotal);

        Producto nuevoP = new Producto();
        nuevoP.setNombre(INSERTAR);
        nuevoP.setPrecio(0.0);
        insertarProducto.add(nuevoP);

        status = bundle.getInt("status");
        tvLocal = findViewById(R.id.status_message);
        tvFecha = findViewById(R.id.et_fecha);
        tvHora = findViewById(R.id.et_hora);
        listView = findViewById(R.id.lv_ticket);

        useFlash = findViewById(R.id.use_flash);


        if(status == 1){
            idTicket = bundle.getString("idTicket");
            fab.setImageResource(R.drawable.ic_copy);
            if(idTicket.equals("0")){

                productos = new ArrayList<>();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                fecha = mdformat.format(calendar.getTime());

                tvFecha.setText(fecha);

                mdformat = new SimpleDateFormat("HH:mm");
                hora = mdformat.format(calendar.getTime());
                tvHora.setText(hora);

                try{
                    local = manager.obtenerNombreLocales().get(0);
                }catch (Exception e){
                    local = "";
                }

                tvLocal.setText(local);

                actualizarLista();
            }else{
                Ticket ticket = manager.obtenerTicket(idTicket);

                productos = ticket.getProductos();

                tvLocal.setText(ticket.getLocal().getNombre());
                local = ticket.getLocal().getNombre();
                fecha = ticket.getFecha();
                tvFecha.setText(fecha);

                hora = ticket.getHora();
                tvHora.setText(hora);

                String p = String.format("%.2f",ticket.getTotal());
                p = p.replace(",",".");

                useFlash.setVisibility(View.INVISIBLE);

                insertarProducto.get(1).setPrecio(Double.parseDouble(p));
                actualizarLista();
            }
        }else{
            idTicket = "0";
            nombresProductos = bundle.getStringArrayList("productos");
            ArrayList<Producto> productosExistentes = manager.obtenerProductos();
            ArrayList<Double> precios =(ArrayList<Double>) bundle.get("precios");
            datos = bundle.getStringArrayList("datos");
            productos = new ArrayList<>();

            for(int i = 0 ; i < nombresProductos.size() ; i ++){
                int iprecio = nombresProductos.size() - precios.size();
                if(i >= iprecio){
                    Producto pr = new Producto(nombresProductos.get(i),precios.get(i-iprecio));

                    forProductos:
                    for(int j = 0 ; j < productosExistentes.size(); j++){
                        if(productosExistentes.get(j).getNombre().contains(nombresProductos.get(i)) ||
                                nombresProductos.get(i).contains(productosExistentes.get(j).getNombre())){
                            pr = productosExistentes.get(j);
                            pr.setPrecio(precios.get(i-iprecio));
                            break forProductos;
                        }else{
                            pr = new Producto(nombresProductos.get(i),precios.get(i-iprecio));
                        }
                    }

                    productos.add(pr);
                }
            }

            local = datos.get(0);
            tvLocal.setText(local);

            fecha = datos.get(1);
            tvFecha.setText(fecha);

            hora = datos.get(2);
            tvHora.setText(hora);

            total = datos.get(3);
            insertarProducto.get(1).setPrecio(Double.parseDouble(total));
            //TODO arreglar el precio

            tvPagar.setVisibility(View.VISIBLE);

            actualizarLista();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombre = productos.get(i).getNombre();
                if(nombre.equals(INSERTAR)){
                    popUpInsertarProducto();
                }else if(nombre.equals(PRECIO_TOTAL)){
                    popUpPrecio();
                }else if(!nombre.equals("")){
                    popUpProducto(productos.get(i));
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == tvLocal || v == tvFecha || v == tvHora){
            popUpDatos();
        }
    }

    public void popUpDatos() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_datos_ticket, null);

        final AutoCompleteTextView actv_local = v.findViewById(R.id.actv_local);
        actv_local.setText(local);
        final ArrayList<String> locales = manager.obtenerNombreLocales();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, locales);
        actv_local.setAdapter(adapter);
        actv_local.setThreshold(0);

        final TextView et_fecha = v.findViewById(R.id.tv_fecha);
        final TextView et_hora = v.findViewById(R.id.et_hora);

        et_fecha.setText(tvFecha.getText());
        et_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFecha(et_fecha,fecha);
            }
        });
        et_hora.setText(tvHora.getText());
        et_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpHora(et_hora,hora);
            }
        });

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                tvFecha.setText(et_fecha.getText());
                fecha = tvFecha.getText().toString();
                tvHora.setText(et_hora.getText());
                hora = tvHora.getText().toString();

                local = actv_local.getText().toString();
                tvLocal.setText(local);


            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpFecha(final TextView et_fecha,String fecha){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_fecha, null);


        builder.setTitle("Modificar fecha");
        builder.setView(v);

        final CalendarView calendario = v.findViewById(R.id.calendarView);

        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                String dia = "";
                String mes = "";
                String anyo = "";
                i1++;

                if(i2<=9){
                    dia = "0" + i2;
                }else{
                    dia = String.valueOf(i2);
                }

                if(i1<=9){
                    mes = "0" + i1;
                }else{
                    mes = String.valueOf(i1);
                }

                anyo = String.valueOf(i);

                et_fecha.setText(dia + "/" + mes + "/" + anyo);
            }
        });

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            Date date = sdf.parse(fecha);
            long startDate = date.getTime();

            calendario.setDate(startDate);
        }catch (Exception e){
            e.printStackTrace();
        }


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

    public void popUpHora(final TextView et_hora,String tiempo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_hora, null);

        int hora = 0;
        int minutos = 0;

        StringTokenizer token = new StringTokenizer(tiempo,":");
        if(token.hasMoreTokens()){
            hora = Integer.parseInt(token.nextToken());
            minutos = Integer.parseInt(token.nextToken());
        }


        builder.setTitle("Modificar hora");
        builder.setView(v);

        final NumberPicker pickerHora = v.findViewById(R.id.np_hora);
        pickerHora.setMaxValue(23);
        pickerHora.setMinValue(0);
        pickerHora.setValue(hora);

        final NumberPicker pickerMinutos = v.findViewById(R.id.np_minutos);
        pickerMinutos.setMaxValue(59);
        pickerMinutos.setMinValue(0);
        pickerMinutos.setValue(minutos);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String hora = String.valueOf(pickerHora.getValue());

                if(pickerHora.getValue()<10){
                    hora = "0" + hora;
                }

                if(pickerMinutos.getValue()<10){
                    hora += ":0" + pickerMinutos.getValue();
                }else{
                    hora += ":" + pickerMinutos.getValue();
                }
                et_hora.setText(hora);
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


        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int pos = productos.size()-2;

                Producto pro = productos.get(pos);
                String pre = et_precio_ticket.getText().toString();
                pre = pre.replace(",",".");
                pro.setPrecio(Double.parseDouble(pre));

                productos.set(pos,pro);
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

        final AutoCompleteTextView at_nombre_producto = v.findViewById(R.id.at_productos);
        at_nombre_producto.setText(producto.getNombre());

        final EditText et_precio_producto = v.findViewById(R.id.et_precio_producto);
        et_precio_producto.setText(String.valueOf(producto.getPrecio()));

        final ArrayList<String> nombreProductos = manager.obtenerNombreProductos();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(),android.R.layout.simple_dropdown_item_1line, nombreProductos);
        at_nombre_producto.setAdapter(adapter);
        at_nombre_producto.setThreshold(1);

        at_nombre_producto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Local loc = manager.obtenerLocal(local);
                String nombre = at_nombre_producto.getText().toString();
                Producto pro = manager.obtenerProductoByNombre(nombre);
                double precio = manager.obtenerProducto_Precio(loc,pro);
                et_precio_producto.setText(String.valueOf(precio));
            }
        });

        builder.setTitle("Modificar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                producto.setNombre(at_nombre_producto.getText().toString());
                try{
                    producto.setPrecio(Double.parseDouble(et_precio_producto.getText().toString()));
                    if(!producto.getNombre().equals(PRECIO_TOTAL)){
                        actualizarPrecio();
                    }else{
                        total = String.valueOf(producto.getPrecio());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNeutralButton(R.string.delete,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                popUpEliminarProducto(producto);
            }
        });
        builder.show();
    }

    public void popUpInsertarProducto() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View v = inflater.inflate(R.layout.popup_producto_ticket, null);

        final EditText et_precio_producto = v.findViewById(R.id.et_precio_producto);

        final AutoCompleteTextView at_nombre_producto = v.findViewById(R.id.at_productos);
        final ArrayList<String> nombreProductos = manager.obtenerNombreProductos();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(),android.R.layout.simple_dropdown_item_1line, nombreProductos);
        at_nombre_producto.setAdapter(adapter);
        at_nombre_producto.setThreshold(1);

        at_nombre_producto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Local loc = manager.obtenerLocal(local);
                String nombre = at_nombre_producto.getText().toString();
                Producto pro = manager.obtenerProductoByNombre(nombre);
                double precio = manager.obtenerProducto_Precio(loc,pro);
                et_precio_producto.setText(String.valueOf(precio));
            }
        });

        builder.setTitle("Insertar Producto");
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String nombre = at_nombre_producto.getText().toString();
                Producto producto = manager.obtenerProductoByNombre(nombre);
                try{
                    producto.setPrecio(Double.parseDouble(et_precio_producto.getText().toString()));
                    productos.add(producto);
                    actualizarPrecio();

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

    public void popUpEliminarTicket() {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();


        builder.setTitle("Eliminar Ticket");
        builder.setMessage("¿Desea eliminar este Ticket?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                manager.eliminarTicketById(idTicket);
                enviarMensaje("Ticket eliminado con exito");
                activityTickets();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void popUpEliminarProducto(final Producto producto) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar Producto de Ticket");
        builder.setMessage("¿Desea eliminar el producto '" + producto.getNombre() + "' del ticket?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                productos.remove(producto);
                actualizarLista();
                actualizarPrecio();
                enviarMensaje("Producto eliminado con exito");
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public void enviarMensaje(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public void activityTickets(){
        finish();
        Intent intent = new Intent(this,Activity_Tickets.class);
        startActivity(intent);
    }

    public void aceptarTicket() {
        if(idTicket.equals("0")) {
            try {
                Ticket ticket = new Ticket();
                ticket.setHora(tvHora.getText().toString());
                ticket.setFecha(tvFecha.getText().toString());

                Local l = manager.obtenerLocal(tvLocal.getText().toString());
                if(l.getId().equals("-1")){
                    l.setNombre(tvLocal.getText().toString());
                    long id = manager.insertarLocal(l);
                    l.setId(String.valueOf(id));
                }
                ticket.setLocal(l);


                String precio = total.replace(",", ".");
                ticket.setTotal(Double.parseDouble(precio));

                ArrayList<Producto> productosTicket = new ArrayList<>();
                productosTicket.addAll(productos);
                productosTicket.removeAll(insertarProducto);
                ticket.setProductos(productosTicket);
                idTicket = String.valueOf(manager.insertarTicket(ticket));
                ticket.setIdTicket(idTicket);

                enviarMensaje("Ticket creado con exito");
                activityTickets();

            } catch (Exception e) {
                Toast.makeText(this,"El precio no es correcto",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Ticket ticket = manager.obtenerTicket(idTicket);
            ticket.setHora(hora);
            ticket.setFecha(fecha);
            Local l = manager.obtenerLocal(local);
            ticket.setLocal(l);
            ticket.setTotal(Double.parseDouble(total));

            ArrayList<Producto> productosTicket = new ArrayList<>();
            productosTicket.addAll(productos);
            productosTicket.removeAll(insertarProducto);
            ticket.setProductos(productosTicket);

            manager.modificarTicket(ticket);
            activityTickets();

        }
    }


    public void nuevoProductoEnLista(){
        productos.removeAll(insertarProducto);
        productos.addAll(insertarProducto);
    }

    public void actualizarLista(){
        nuevoProductoEnLista();
        AdapterContenidoTicket adapter = new AdapterContenidoTicket(this,productos);
        listView.setAdapter(adapter);


    }

    public void actualizarPrecio(){
        double precio = 0.0;
        for(Producto producto:productos){
            if(!insertarProducto.contains(producto)){
                precio+=producto.getPrecio();
            }
        }
        total = String.valueOf(precio);
        Producto pr = insertarProducto.get(1);
        pr.setPrecio(precio);
        insertarProducto.set(1,pr);

        String p = String.format("%.2f",precio);
        p = p.replace(",",".");
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
        if (id == R.id.action_delete) {
            popUpEliminarTicket();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        manager.gestionarMenu(item,this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
