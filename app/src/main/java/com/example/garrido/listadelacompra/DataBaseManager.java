package com.example.garrido.listadelacompra;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class DataBaseManager implements Database {

    /*db.update(String table, Content values,String where clause,String[]where args);
        db.update(TABLE_INGREDIENES, generarValores(nombre,stock),CN_INGREDIENTE +"= ?",new String[]{nombre});*/

        /* db.insert(String table, String nullColumnHack, ContentValues values
        * db.insert(TABLE_PRODUCTO_TIPO, null, generarValoresProducto_Tipo(producto, tipo));*/

        /* db.query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy)
        * db.query(TABLE_PRODUCTO_TIPO,columnas,CN_IDPRODUCTO+ " = ?",new String[]{producto},null,null,null);*/

    private SQLiteDatabase db;
    public DataBaseManager(Context context) {

        DBHelper helper;
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /*TODO UTILIDADES*/

    public void eliminarTabla(String nombreTabla){
        db.execSQL("Drop table if exists " + nombreTabla);
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public void actualizar(){

        if(!existeTablaProducto()) {
            this.crearTablaProducto();
        }
        if(!existeTablaCategoria()) {
            this.crearTablaCategoria();
            this.crearTablaProducto_Categoria();
        }
        if(!existeTablaLocal()) {
            this.crearTablaLocal();
            this.crearTablaProducto_Local();
        }
        if(!existeTablaLista()) {
            this.crearTablaLista();
            this.crearTablaProducto_Lista();
        }
        if(!existeTablaConjunto()) {
            this.crearTablaConjunto();
            this.crearTablaProducto_Conjunto();
        }
        if(!existeTablaLocal()) {
            this.crearTablaLocal();
            this.crearTablaProducto_Local();
        }
        if(!existeTablaTickets()) {
            this.crearTablaTicket();
            this.crearTablaFact();
        }
    }

    public void reset(){

        this.eliminarTabla("Producto");
        this.eliminarTabla("Local");
        this.eliminarTabla("Categoria");
        this.eliminarTabla("Subcategoria");
        this.eliminarTabla("Producto_Local");
        this.eliminarTabla("Producto_Categoria");
        this.eliminarTabla("Producto_Subcategoria");
        this.eliminarTabla("Categoria_Subcategoria");

    }

    public void crear(){
        /*actualizar();
        //String nombre, String descripcion, String etiqueta, Categoria categoria, Subcategoria subcategoria, Local local, String marca, float precio
        Categoria categoria = new Categoria("pan");
        insertarCategoria(categoria);
        Categoria cat = obtenerCategoria(categoria.getNombre());

        Subcategoria subcategoria = new Subcategoria("pan");
        insertarSubcategoria(subcategoria);
        Subcategoria sub = obtenerSubcategoria(subcategoria.getNombre());


        Categoria bebidas = new Categoria("bebidas");
        long idBebidas = insertarCategoria(bebidas);
        bebidas.setId(String.valueOf(idBebidas));

        Subcategoria refrescos = new Subcategoria("refrescos");
        long idRefrescos = insertarSubcategoria(refrescos);
        refrescos.setId(String.valueOf(idRefrescos));

        insertarCategoria_Subcategoria(bebidas,refrescos);

        Local local = new Local("carrefour");
        insertarLocal(local);
        Local loc = obtenerLocal(local.getNombre());

        Producto producto1 = new Producto("pan pistola/barra","","panes",cat,sub,loc,"carrefour",0.45);
        insertarProducto(producto1);
        producto1.setId(obtenerProductoByNombre(producto1.getNombre()).getId());

        Producto producto3 = new Producto("pan leche c/pepita","","panes",cat,sub,loc,"carrefour",0.45);
        insertarProducto(producto3);
        producto3.setId(obtenerProductoByNombre(producto3.getNombre()).getId());

        Producto cocacola2l = new Producto("coca cola zero 2L","","bebidas",bebidas,refrescos,loc,"coca cola",1.46);
        insertarProducto(cocacola2l);
        cocacola2l.setId(obtenerProductoByNombre(cocacola2l.getNombre()).getId());

        insertarCategoria(new Categoria("agua"));
        insertarSubcategoria(new Subcategoria("agua mineral"));
        Local l = new Local("dia");

        Local mercadona = new Local("mercadona s.a");
        insertarLocal(mercadona);
        mercadona = this.obtenerLocal(mercadona.getNombre());
        Categoria carne = new Categoria("carne");
        Subcategoria cerdo = new Subcategoria("cerdo");
        long idCarne = insertarCategoria(carne);
        carne.setId(idCarne+"");
        long idCerdo = insertarSubcategoria(cerdo);
        cerdo.setId(idCerdo+"");

        insertarCategoria_Subcategoria(carne,cerdo);

        Producto libritos = new Producto("libritos lomo","","carne",carne,cerdo,mercadona,"Hacendado",2.28);
        Producto cacacola = new Producto("cocacola n 33","","refrescos",bebidas,refrescos,mercadona,"Cocacola",0.60);
        Producto pipas = new Producto("pipas gigantes","","aperitivos",bebidas,refrescos,mercadona,"Hacendado",0.95);

        long idLibritos = insertarProducto(libritos);
        libritos.setId(idLibritos+"");
        long idCacacola = insertarProducto(cacacola);
        cacacola.setId(idCacacola+"");
        long idPipas = insertarProducto(pipas);
        pipas.setId(idPipas+"");

        long idinsert = insertarProducto_Local(cacacola,mercadona,0.35);
        long idinsert2 = insertarProducto_Local(libritos,mercadona,1.00);
        long idinsert3 = insertarProducto_Local(pipas,mercadona,0.95);
        //insertarLocal(l);

        ArrayList<Categoria> categorias = obtenerCategorias();
        ArrayList<Subcategoria> subcategorias = obtenerSubcategorias();
        ArrayList<Local> locales = obtenerLocales();

        ArrayList<Ticket> tickets = this.obtenerTickets("fecha");
        String idTicket;
        if(tickets.size()>0){
            idTicket  = tickets.get(tickets.size()-1).getIdTicket();
        }else{
            idTicket = "1";
        }

        Ticket ticket = new Ticket();
        ticket.setLocal(loc);
        ticket.setHora("11:55:05");
        ticket.setFecha("11/07/18");
        ticket.setTotal(2.36);
        ticket.setIdTicket(idTicket);

        ArrayList<Producto> productos = new ArrayList<>();
        productos.add(producto1);
        productos.add(cocacola2l);
        productos.add(producto3);

        ticket.setProductos(productos);

        this.insertarTicket(ticket);


        //facts.add(new Fact(idTicket,producto1.));
*/
/*
        Subcategoria aguas = new Subcategoria("aguas");
        Subcategoria zumos = new Subcategoria("zumos");

        this.insertarCategoria(bebidas);
        this.insertarSubcategoria(refrescos);
        this.insertarSubcategoria(aguas);
        this.insertarSubcategoria(zumos);

        this.insertarCategoria_Subcategoria(bebidas,refrescos);
        this.insertarCategoria_Subcategoria(bebidas,aguas);
        this.insertarCategoria_Subcategoria(bebidas,zumos);

        Categoria carne = new Categoria("carne");
        Subcategoria pollo = new Subcategoria("pollo");
        Subcategoria cerdo = new Subcategoria("cerdo");
        Subcategoria vaca = new Subcategoria("vaca");

        this.insertarCategoria(carne);
        this.insertarSubcategoria(pollo);
        this.insertarSubcategoria(cerdo);
        this.insertarSubcategoria(vaca);

        this.insertarCategoria_Subcategoria(carne,pollo);
        this.insertarCategoria_Subcategoria(carne,cerdo);
        this.insertarCategoria_Subcategoria(carne,vaca);
*/
    }

    /*public int login(String nick, String password, boolean recordar){
        //db.query(TABLE_NAME,)
        int login = 0;

        String[] columna = new String[] {CN_NICK,CN_PASSWORD};
        Cursor cursor = db.query(TABLE_PERSONAL,columna,CN_NICK + "= ?",new String[]{nick},null,null,null);
        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            if(!password.equals(cursor.getString(1))){
                login = 2;
            }
        }else{
            login = 1;
        }

        //Para pruebas
        if(nick.isEmpty()){
            rol = "admin";
            login = 0;
        }
        cursor.close();
        return login;
    }*/

    public ArrayList<String> toLowerCase(ArrayList<String> strings){
        ArrayList<String> lower = new ArrayList<>();
        for(int i = 0 ; i< strings.size();i++){
            lower.add(strings.get(i).toLowerCase());
        }
        return lower;
    }

    public void gestionarMenu(MenuItem item, Context context) {
// Handle navigation view item clicks here.

        int id = item.getItemId();
        Intent intent;
        if(id==R.id.nav_principal){
            intent = new Intent(context,MainActivity.class);
        } else if (id == R.id.nav_productos) {
            intent = new Intent(context,Activity_Productos.class);
        } else if (id == R.id.nav_conjuntos) {
            intent = new Intent(context,Activity_Conjuntos.class);
        } else if (id == R.id.nav_locales) {
            intent = new Intent(context,Activity_Local.class);
        } else if (id == R.id.nav_tickets) {
            intent = new Intent(context,Activity_Tickets.class);
        } else if (id == R.id.nav_est) {
            intent = new Intent(context,MainActivity.class);
        }else if (id == R.id.nav_import) {
            intent = new Intent(context,Activity_Import.class);
        }else if (id == R.id.nav_faq) {
            intent = new Intent(context,MainActivity.class);
        }else if (id == R.id.nav_reinicio) {
            intent = new Intent(context,Activity_Reinicio.class);
        }else{
            intent = new Intent(context,MainActivity.class);
        }
        context.startActivity(intent);

    }

            /*TABLAS*/
    private static final String TABLE_PRODUCTO = "producto";
    private static final String TABLE_LOCAL = "local";
    private static final String TABLE_CATEGORIA = "categoria";
    private static final String TABLE_LISTA = "lista";
    private static final String TABLE_CONJUNTO = "conjunto";
    private static final String TABLE_TICKET = "ticket";
    private static final String TABLE_FACT = "fact";

    private static final String TABLE_PRODUCTO_LOCAL = "producto_local";
    private static final String TABLE_PRODUCTO_CATEGORIA = "producto_categoria";
    private static final String TABLE_PRODUCTO_LISTA = "producto_lista";
    private static final String TABLE_PRODUCTO_CONJUNTO = "producto_conjunto";

            /*COLUMNAS COMUNES*/
    private static final String CN_ID = "_id";
    private static final String CN_NOMBRE = "nombre";

            /*COLUMNAS TABLA PRODUCTO*/
    private static final String CN_DESCRIPCION = "descripcion";
    private static final String CN_ETIQUETA = "etiqueta";
    private static final String CN_CATEGORIA = "categoria";
    private static final String CN_LOCAL = "local";
    private static final String CN_PRECIO = "precio";

            /*COLUMNAS TABLA TICKET*/
    private static final String CN_FECHA = "fecha";
    private static final String CN_ANO = "ano";
    private static final String CN_MES = "mes";
    private static final String CN_DIA = "dia";
    private static final String CN_HORA = "hora";
    private static final String CN_TOTAL = "total";
    private static final String CN_IDTICKET = "idticket";

            /*COLUMNAS TABLA LISTA*/
    private static final String CN_PRINCIPAL = "principal";

            /*COLUMNAS TABLAS CRUZADAS*/
    private static final String CN_IDPRODUCTO = "idproducto";
    private static final String CN_IDLOCAL = "idlocal";
    private static final String CN_IDCATEGORIA = "idcategoria";
    private static final String CN_IDLISTA = "idlista";
    private static final String CN_IDCONJUNTO = "idconjunto";

            /*TODO CREACION TABLA PRODUCTO*/
    private static final String CREATE_TABLE_PRODUCTO = "create table " + TABLE_PRODUCTO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null unique,"
            + CN_DESCRIPCION + " text,"
            + CN_ETIQUETA + " text,"
            + CN_CATEGORIA + " text"
            + ");";

    private ContentValues generarValoresProducto(Producto producto){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, producto.getNombre());
        valores.put(CN_DESCRIPCION, producto.getDescripcion());
        if(producto.getEtiqueta().equals("")){
            producto.setEtiqueta("Sin etiqueta");
        }
        valores.put(CN_ETIQUETA, producto.getEtiqueta());
        valores.put(CN_CATEGORIA, producto.getCategoria().getId());
        return valores;
    }

    @Override
    public void crearTablaProducto(){
        this.eliminarTabla(TABLE_PRODUCTO);
        db.execSQL(CREATE_TABLE_PRODUCTO);
    }

    @Override
    public boolean existeTablaProducto(){
        return this.isTableExists(db,"producto");
    }

    @Override
    public long insertarProducto(Producto producto) {
        insertarProducto_Categoria(producto,producto.getCategoria());

        return db.insert(TABLE_PRODUCTO, null, generarValoresProducto(producto));
    }
    @Override
    public ArrayList<Producto> obtenerProductos() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA};
        ArrayList<Producto> productos = new ArrayList<>();
        Producto producto;
        Cursor cursor = db.query(TABLE_PRODUCTO,columnas,null,null,null,null,CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                producto = new Producto();
                producto.setId(cursor.getString(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setEtiqueta(cursor.getString(3));
                Categoria categoria = this.obtenerCategoriaById(cursor.getInt(4)+"");
                producto.setCategoria(categoria);

                productos.add(producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public ArrayList<Producto> obtenerProductosByCategoria(Categoria categoria) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA};
        ArrayList<Producto> productos = new ArrayList<>();
        Producto producto;
        Cursor cursor = db.query(TABLE_PRODUCTO, columnas, CN_CATEGORIA + " = ?", new String[]{categoria.getNombre()}, null, null, CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                producto = new Producto();
                producto.setId(cursor.getString(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setEtiqueta(cursor.getString(3));
                producto.setCategoria(new Categoria(cursor.getString(4)));
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public TreeMap<String,String> obtenerLocales_Producto(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_IDPRODUCTO,CN_IDLOCAL,CN_PRECIO};
        Producto p = obtenerProductoByNombre(nombre);
        TreeMap<String,String> mapa = new TreeMap<>();
        ArrayList<Producto> productos = new ArrayList<>();
        Producto producto;
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, CN_IDPRODUCTO + " = ?", new String[]{p.getId()}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Local local = obtenerLocalById(cursor.getString(2));
                mapa.put(local.getNombre(),(cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }


    @Override
    public Producto obtenerProductoByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA};
        Producto producto = new Producto();
        Cursor cursor = db.query(TABLE_PRODUCTO,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                producto.setId(cursor.getString(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setEtiqueta(cursor.getString(3));
                Categoria categoria = obtenerCategoriaById(cursor.getString(4));
                producto.setCategoria(categoria);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return producto;
    }

    @Override
    public Producto obtenerProductoById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA};
        Producto producto = new Producto();
        Cursor cursor = db.query(TABLE_PRODUCTO,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                producto.setId(cursor.getString(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setEtiqueta(cursor.getString(3));
                String idCategoria = cursor.getString(4);
                producto.setCategoria(obtenerCategoriaById(idCategoria));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return producto;
    }

    @Override
    public ArrayList<String> obtenerNombreProductos(){
        String[] columnas = new String[] {CN_NOMBRE};
        ArrayList<String> columna = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO,columnas,null,null,null,null,CN_CATEGORIA);
        if(cursor.moveToFirst()) {
            do {
                columna.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return columna;
    }

    @Override
    public boolean modificarProducto(Producto producto) {
        ContentValues values = generarValoresProducto(producto);
        int result = db.update(TABLE_PRODUCTO, values, CN_ID + " = ?", new String[]{producto.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarProducto(Producto producto) {
        eliminarProducto_Categoria(producto);

        db.delete(TABLE_PRODUCTO, CN_NOMBRE + "=?", new String[]{producto.getNombre()});
        return true;
    }

            /*TODO CREACION TABLA LOCAL*/

    private static final String CREATE_TABLE_LOCAL = "create table " + TABLE_LOCAL + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null unique,"
            + CN_DESCRIPCION + " text"
            + ");";

    private ContentValues generarValoresLocal(Local local){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, local.getNombre());
        return valores;
    }

    @Override
    public void crearTablaLocal(){
        this.eliminarTabla(TABLE_LOCAL);
        db.execSQL(CREATE_TABLE_LOCAL);
    }

    @Override
    public boolean existeTablaLocal(){
        return this.isTableExists(db,"local");
    }

    @Override
    public long insertarLocal(Local local) {
        return db.insert(TABLE_LOCAL, null, generarValoresLocal(local));
    }

    @Override
    public ArrayList<Local> obtenerLocales() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        ArrayList<Local> locales = new ArrayList<>();
        Local local;
        Cursor cursor = db.query(TABLE_LOCAL,columnas,null,null,null,null,CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                local = new Local();
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));

                local.setProductos(obtenerLocal_Productos(local));
                locales.add(local);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return locales;
    }

    @Override
    public Local obtenerLocalByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Local local = new Local();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));

                local.setProductos(obtenerLocal_Productos(local));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return local;
    }

    @Override
    public Local obtenerLocalById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Local local = new Local();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));

                local.setProductos(obtenerLocal_Productos(local));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return local;
    }

    @Override
    public Local obtenerLocal(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Local local = new Local();
        local.setId("-1");
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return local;
    }

    @Override
    public ArrayList<String> obtenerNombreLocales(){
        String[] columnas = new String[] {CN_NOMBRE};
        ArrayList<String> columna = new ArrayList<>();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                columna.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return columna;
    }

    @Override
    public boolean modificarLocal(Local local) {
        ContentValues values = generarValoresLocal(local);
        int result = db.update(TABLE_LOCAL, values, CN_ID + " = ?", new String[]{local.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarLocal(Local local) {
        eliminarProductos_Local(local);
        db.delete(TABLE_LOCAL, CN_ID + "=?", new String[]{local.getId()});
        return true;
    }

            /*todo CREACION TABLA CATEGORIA */


    private static final String CREATE_TABLE_CATEGORIA = "create table " + TABLE_CATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null unique"
            + ");";

    private ContentValues generarValoresCategoria(Categoria categoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, categoria.getNombre());
        return valores;
    }

    @Override
    public boolean existeTablaCategoria(){
        return this.isTableExists(db,"categoria");
    }

    @Override
    public void crearTablaCategoria(){
        this.eliminarTabla(TABLE_CATEGORIA);
        db.execSQL(CREATE_TABLE_CATEGORIA);

        Categoria c = new Categoria("sin categoria");
        insertarCategoria(c);
    }

    @Override
    public long insertarCategoria(Categoria categoria) {
            return db.insert(TABLE_CATEGORIA, null, generarValoresCategoria(categoria));
    }

    @Override
    public ArrayList<Categoria> obtenerCategorias() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        ArrayList<Categoria> categorias = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CATEGORIA, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Categoria categoria = new Categoria();
                categoria.setId(cursor.getInt(0) + "");
                categoria.setNombre(cursor.getString(1));
                categorias.add(categoria);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categorias;
    }

    @Override
    public ArrayList<String> obtenerNombreCategorias() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        ArrayList<String> categorias = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CATEGORIA, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(1);
                categorias.add(nombre);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categorias;
    }

    @Override
    public Categoria obtenerCategoriaById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Categoria categoria = new Categoria();
        Cursor cursor = db.query(TABLE_CATEGORIA,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                categoria.setId(cursor.getInt(0) + "");
                categoria.setNombre(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoria;
    }

    @Override
    public Categoria obtenerCategoria(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Categoria categoria = new Categoria();
        Cursor cursor = db.query(TABLE_CATEGORIA,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                categoria.setId(cursor.getInt(0) + "");
                categoria.setNombre(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoria;
    }

    @Override
    public boolean modificarCategoria(Categoria categoria) {
        ContentValues values = generarValoresCategoria(categoria);
        int result = db.update(TABLE_CATEGORIA, values, CN_ID + " = ?", new String[]{categoria.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarCategoria(Categoria categoria) {
        db.delete(TABLE_CATEGORIA, CN_ID + "=?", new String[]{categoria.getId()});
        return true;
    }



    private ContentValues generarValoresProducto_Categoria(Producto producto, Categoria categoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDCATEGORIA, categoria.getId());
        return valores;
    }

    @Override
    public void crearTablaProducto_Categoria(){
        this.eliminarTabla(TABLE_PRODUCTO_CATEGORIA);
        db.execSQL(CREATE_TABLE_PRODUCTO_CATEGORIA);

        Categoria categoria = obtenerCategoriaById("1");

        ArrayList<Producto> productos = obtenerProductos();
        for(Producto producto:productos){
            insertarProducto_Categoria(producto,categoria);
        }
    }

    @Override
    public long insertarProducto_Categoria(Producto producto, Categoria categoria) {
        return db.insert(TABLE_PRODUCTO_CATEGORIA, null, generarValoresProducto_Categoria(producto, categoria));
    }


            /*todo CREACION TABLA PRODUCTO_LOCAL */

    private static final String CREATE_TABLE_PRODUCTO_LOCAL = "create table " + TABLE_PRODUCTO_LOCAL + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDLOCAL + " integer not null,"
            + CN_PRECIO + " real"
            + ");";

    private ContentValues generarValoresProducto_Local(Producto producto, Local local,Double precio){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDLOCAL, local.getId());
        valores.put(CN_PRECIO, precio);
        return valores;
    }

    @Override
    public void crearTablaProducto_Local(){
        this.eliminarTabla(TABLE_PRODUCTO_LOCAL);
        db.execSQL(CREATE_TABLE_PRODUCTO_LOCAL);
    }

    @Override
    public long insertarProducto_Local(Producto producto, Local local,Double precio) {
        return db.insert(TABLE_PRODUCTO_LOCAL, null, generarValoresProducto_Local(producto, local,precio));
    }

    @Override
    public Map<String,ArrayList<Producto>> obtenerLocales_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDLOCAL,CN_IDPRODUCTO,CN_PRECIO};
        Map<String,ArrayList<Producto>> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, null, null, null, null, CN_IDLOCAL);
        if(cursor.moveToFirst()) {
            do {
                ArrayList<Producto> p;
                Local local = this.obtenerLocalById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));
                producto.setPrecio(Double.parseDouble(cursor.getString(3)));

                if(mapa.containsKey(local.getNombre())){
                    p = mapa.get(local.getNombre());
                    p.add(producto);
                }else{
                    p = new ArrayList<>();
                }
                mapa.put(local.getNombre(),p);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerLocal_Productos(Local local) {
        String[] columnas = new String[] {CN_ID,CN_IDLOCAL,CN_IDPRODUCTO,CN_PRECIO};
        String idLocal = obtenerLocal(local.getNombre()).getId();
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, CN_IDLOCAL + " = ?", new String[]{idLocal}, null, null, CN_IDLOCAL);
        if(cursor.moveToFirst()) {
            do {
                String id = cursor.getString(2);
                Producto producto = this.obtenerProductoById(id);
                producto.setPrecio(Double.parseDouble(cursor.getString(3)));
                productos.add(producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public double obtenerProducto_Precio(Local local,Producto pro) {
        String[] columnas = new String[] {CN_ID,CN_IDLOCAL,CN_IDPRODUCTO,CN_PRECIO};
        double precio = 0.0;
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, CN_IDLOCAL + " = ? AND " + CN_IDPRODUCTO + " = ?", new String[]{local.getId(),pro.getId()}, null, null, CN_IDLOCAL);
        if(cursor.moveToFirst()) {
            do {
                precio = Double.parseDouble(cursor.getString(3));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return precio;
    }

    @Override
    public boolean modificarProducto_Local(Producto producto, Local local, Double precio) {
        ContentValues values = generarValoresProducto_Local(producto,local,precio);
        int result = db.update(TABLE_PRODUCTO_LOCAL, values, CN_IDPRODUCTO + "=? AND " + CN_IDLOCAL + "=?", new String[]{producto.getId(),local.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarProducto_Local(Producto producto, Local local) {
        db.delete(TABLE_PRODUCTO_LOCAL, CN_IDPRODUCTO + "= ? AND " + CN_IDLOCAL + "= ?", new String[]{producto.getId(),local.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Local(Local local) {
        db.delete(TABLE_PRODUCTO_LOCAL, CN_IDLOCAL + "=?", new String[]{local.getId()});
        return true;
    }

            /*todo CREACION TABLA PRODUCTO_CATEGORIA */

    private static final String CREATE_TABLE_PRODUCTO_CATEGORIA = "create table " + TABLE_PRODUCTO_CATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDCATEGORIA + " integer not null"
            + ");";

    private ContentValues generarValoresCategoria_Subcategoria(Categoria categoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDCATEGORIA, categoria.getId());
        return valores;
    }

    @Override
    public Map<Categoria,Producto> obtenerCategorias_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDCATEGORIA,CN_IDPRODUCTO};
        Map<Categoria,Producto> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_CATEGORIA, columnas, null, null, null, null, CN_IDCATEGORIA);
        if(cursor.moveToFirst()) {
            do {
                Categoria categoria = this.obtenerCategoriaById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));

                mapa.put(categoria,producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerProductos_Categoria(Categoria categoria) {
        String[] columnas = new String[] {CN_ID,CN_IDCATEGORIA,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_CATEGORIA, columnas, CN_ID + " = ?", new String[]{categoria.getId()}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Producto producto = this.obtenerProductoById(cursor.getString(2));
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public boolean modificarProducto_Categoria(String id, Producto producto, Categoria categoria) {
        ContentValues values = generarValoresProducto_Categoria(producto,categoria);
        int result = db.update(TABLE_PRODUCTO_CATEGORIA, values, CN_ID + " = ?", new String[]{id});
        return result == 1;
    }

    @Override
    public boolean eliminarProducto_Categoria(Producto producto) {
        db.delete(TABLE_PRODUCTO_CATEGORIA, CN_IDPRODUCTO + "=?", new String[]{producto.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Categoria(Categoria categoria) {
        db.delete(TABLE_PRODUCTO_CATEGORIA, CN_IDCATEGORIA + "=?", new String[]{categoria.getId()});
        return true;
    }

            /*todo CREACION TABLA LISTA */

    private static final String CREATE_TABLE_LISTA = "create table " + TABLE_LISTA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null unique,"
            + CN_DESCRIPCION + " text not null,"
            + CN_PRINCIPAL + " text not null"
            + ");";

    private ContentValues generarValoresLista(Lista lista){
        /* Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, lista.getNombre());
        valores.put(CN_DESCRIPCION, lista.getDescripcion());
        valores.put(CN_PRINCIPAL, lista.isPrincipal()?"1":"0");
        return valores;
    }

    @Override
    public boolean existeTablaLista(){
        return this.isTableExists(db,"lista");
    }

    @Override
    public void crearTablaLista(){
        this.eliminarTabla(TABLE_LISTA);
        db.execSQL(CREATE_TABLE_LISTA);
    }

    @Override
    public long insertarLista(Lista lista) {
        if(lista.isPrincipal()){
            ArrayList<Lista> listas = obtenerListas();
            for(Lista list
                    : listas){
                list.setPrincipal(false);
                modificarLista(list);
            }
        }
        return db.insert(TABLE_LISTA, null, generarValoresLista(lista));
    }

    @Override
    public ArrayList<Lista> obtenerListas() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION,CN_PRINCIPAL};
        ArrayList<Lista> listas = new ArrayList<>();
        Cursor cursor = db.query(TABLE_LISTA, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Lista lista = new Lista();
                String strP = cursor.getString(3);
                boolean principal;
                principal = !strP.equals("0");
                lista.setId(cursor.getString(0));
                lista.setNombre(cursor.getString(1));
                lista.setDescripcion(cursor.getString(2));
                lista.setPrincipal(principal);
                lista.setProductos(obtenerProductos_Lista(lista));
                listas.add(lista);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listas;
    }

    @Override
    public ArrayList<String> obtenerNombreListas() {
        String[] columnas = new String[] {CN_NOMBRE};
        ArrayList<String> listas = new ArrayList<>();
        Cursor cursor = db.query(TABLE_LISTA, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                listas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listas;
    }

    @Override
    public Lista obtenerListaPrincipal(){
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION,CN_PRINCIPAL};
        Lista lista = new Lista();
        Cursor cursor = db.query(TABLE_LISTA, columnas, CN_PRINCIPAL + "= ?", new String[]{"1"}, null, null, null);
        if(cursor.moveToFirst()) {
            do {

                String strP = cursor.getString(3);
                boolean principal;
                principal = !strP.equals("0");
                lista.setId(cursor.getString(0));
                lista.setNombre(cursor.getString(1));
                lista.setDescripcion(cursor.getString(2));
                lista.setPrincipal(principal);
                lista.setProductos(obtenerProductos_Lista(lista));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    @Override
    public Lista obtenerListaById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION,CN_PRINCIPAL};
        Lista lista = new Lista();
        Cursor cursor = db.query(TABLE_LISTA,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                lista.setId(cursor.getString(0));
                lista.setNombre(cursor.getString(1));
                lista.setDescripcion(cursor.getString(2));
                String strP = cursor.getString(3);
                boolean principal;
                principal = !strP.equals("0");
                lista.setPrincipal(principal);
                lista.setProductos(obtenerProductos_Lista(lista));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    @Override
    public Lista obtenerListaByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION,CN_PRINCIPAL};
        Lista lista = new Lista();
        Cursor cursor = db.query(TABLE_LISTA,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                lista.setId(cursor.getString(0));
                lista.setNombre(cursor.getString(1));
                lista.setDescripcion(cursor.getString(2));
                String strP = cursor.getString(3);
                boolean principal;
                principal = !strP.equals("0");
                lista.setPrincipal(principal);
                lista.setProductos(obtenerProductos_Lista(lista));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    @Override
    public boolean modificarLista(Lista lista) {
        ContentValues values = generarValoresLista(lista);
        if(lista.isPrincipal()){
            ArrayList<Lista> listas = obtenerListas();
            for(Lista list
                    : listas){
                list.setPrincipal(false);
                modificarLista(list);
            }
        }
        int result = db.update(TABLE_LISTA, values, CN_ID + " = ?", new String[]{lista.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarLista(Lista lista) {
        eliminarProductos_Lista(lista);
        if(lista.isPrincipal()){
            try{
                Lista nuevaPrincipal = obtenerListas().get(0);
                nuevaPrincipal.setPrincipal(true);
                modificarLista(lista);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        db.delete(TABLE_LISTA, CN_ID + "=?", new String[]{lista.getId()});
        return true;
    }

            /*TODO CREACION TABLA PRODUCTO_LISTA */

    private static final String CREATE_TABLE_PRODUCTO_LISTA = "create table " + TABLE_PRODUCTO_LISTA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDLISTA + " integer not null"
            + ");";

    private ContentValues generarValoresProducto_Lista(Producto producto, Lista lista){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDLISTA, lista.getId());
        return valores;
    }

    @Override
    public void crearTablaProducto_Lista(){
        this.eliminarTabla(TABLE_PRODUCTO_LISTA);
        db.execSQL(CREATE_TABLE_PRODUCTO_LISTA);
    }

    @Override
    public long insertarProducto_Lista(Producto producto, Lista lista) {
        return db.insert(TABLE_PRODUCTO_LISTA, null, generarValoresProducto_Lista(producto, lista));
    }

    @Override
    public long insertarProductos_Lista(ArrayList<Producto> productos, Lista lista) {
        long count = 0;
        for(Producto producto
                : productos){
            ArrayList<Producto> pl = obtenerListaById(lista.getId()).getProductos();
            if(!pl.contains(producto)){
                db.insert(TABLE_PRODUCTO_LISTA, null, generarValoresProducto_Lista(producto, lista));
                count++;
            }
        }
        return count;
    }

    @Override
    public Map<Lista,Producto> obtenerListas_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDLISTA,CN_IDPRODUCTO};
        Map<Lista,Producto> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LISTA, columnas, null, null, null, null, CN_IDLISTA);
        if(cursor.moveToFirst()) {
            do {
                Lista lista = this.obtenerListaById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));

                mapa.put(lista,producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerProductos_Lista(Lista lista) {
        String[] columnas = new String[] {CN_ID,CN_IDLISTA,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LISTA, columnas,CN_IDLISTA + "=?", new String[]{lista.getId()}, null, null, CN_IDLISTA);
        if(cursor.moveToFirst()) {
            do {
                Producto producto = this.obtenerProductoById(cursor.getString(2));
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public boolean eliminarProducto_Lista(Producto producto,Lista lista) {
        int result = db.delete(TABLE_PRODUCTO_LISTA, CN_IDPRODUCTO + "=? AND " + CN_IDLISTA + "=?", new String[]{producto.getId(),lista.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarProductos_Lista(Lista lista) {
        db.delete(TABLE_PRODUCTO_LISTA, CN_IDLISTA + "=?", new String[]{lista.getId()});
        return true;
    }

            /*TODO CONJUNTO*/

    private static final String CREATE_TABLE_CONJUNTO = "create table " + TABLE_CONJUNTO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null unique,"
            + CN_DESCRIPCION + " text not null"
            + ");";

    private ContentValues generarValoresConjunto(Conjunto conjunto){
        /* Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, conjunto.getNombre());
        valores.put(CN_DESCRIPCION, conjunto.getDescripcion());
        return valores;
    }

    @Override
    public boolean existeTablaConjunto(){
        return this.isTableExists(db,"conjunto");
    }

    @Override
    public void crearTablaConjunto(){
        this.eliminarTabla(TABLE_CONJUNTO);
        db.execSQL(CREATE_TABLE_CONJUNTO);
    }

    @Override
    public long insertarConjunto(Conjunto conjunto) {
        return db.insert(TABLE_CONJUNTO, null, generarValoresConjunto(conjunto));
    }

    @Override
    public ArrayList<Conjunto> obtenerConjuntos() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        ArrayList<Conjunto> conjuntos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CONJUNTO, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Conjunto conjunto = new Conjunto();
                conjunto.setId(cursor.getString(0));
                conjunto.setNombre(cursor.getString(1));
                conjunto.setDescripcion(cursor.getString(2));
                conjunto.setProductos(obtenerProductos_Conjunto(conjunto));
                conjuntos.add(conjunto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conjuntos;
    }

    @Override
    public ArrayList<String> obtenerNombreConjuntos() {
        String[] columnas = new String[] {CN_NOMBRE};
        ArrayList<String> conjuntos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CONJUNTO, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                conjuntos.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conjuntos;
    }

    @Override
    public Conjunto obtenerConjuntoById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        Conjunto conjunto = new Conjunto();
        Cursor cursor = db.query(TABLE_CONJUNTO,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                conjunto.setId(cursor.getString(0));
                conjunto.setNombre(cursor.getString(1));
                conjunto.setDescripcion(cursor.getString(2));
                conjunto.setProductos(obtenerProductos_Conjunto(conjunto));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return conjunto;
    }

    @Override
    public Conjunto obtenerConjuntoByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        Conjunto conjunto = new Conjunto();
        Cursor cursor = db.query(TABLE_CONJUNTO,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                conjunto.setId(cursor.getString(0));
                conjunto.setNombre(cursor.getString(1));
                conjunto.setDescripcion(cursor.getString(2));
                conjunto.setProductos(obtenerProductos_Conjunto(conjunto));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conjunto;
    }

    @Override
    public boolean modificarConjunto(Conjunto conjunto) {
        ContentValues values = generarValoresConjunto(conjunto);
        int result = db.update(TABLE_CONJUNTO, values, CN_ID + " = ?", new String[]{conjunto.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarConjunto(Conjunto conjunto) {
        eliminarProductos_Conjunto(conjunto);
        db.delete(TABLE_CONJUNTO, CN_ID + "=?", new String[]{conjunto.getId()});
        return true;
    }

            /*TODO PRODUCTO_CONJUNTO*/

    private static final String CREATE_TABLE_PRODUCTO_CONJUNTO = "create table " + TABLE_PRODUCTO_CONJUNTO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDCONJUNTO + " integer not null"
            + ");";

    private ContentValues generarValoresProducto_Conjunto(Producto producto, Conjunto conjunto){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDCONJUNTO, conjunto.getId());
        return valores;
    }

    @Override
    public void crearTablaProducto_Conjunto(){
        this.eliminarTabla(TABLE_PRODUCTO_CONJUNTO);
        db.execSQL(CREATE_TABLE_PRODUCTO_CONJUNTO);
    }

    @Override
    public long insertarProducto_Conjunto(Producto producto, Conjunto conjunto) {
        return db.insert(TABLE_PRODUCTO_CONJUNTO, null, generarValoresProducto_Conjunto(producto, conjunto));
    }

    @Override
    public Map<Conjunto,Producto> obtenerConjuntos_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDCONJUNTO,CN_IDPRODUCTO};
        Map<Conjunto,Producto> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_CONJUNTO, columnas, null, null, null, null, CN_IDCONJUNTO);
        if(cursor.moveToFirst()) {
            do {
                Conjunto conjunto = this.obtenerConjuntoById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));

                mapa.put(conjunto,producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerProductos_Conjunto(Conjunto conjunto) {
        String[] columnas = new String[] {CN_ID,CN_IDCONJUNTO,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_CONJUNTO, columnas,CN_IDCONJUNTO + "=?", new String[]{conjunto.getId()}, null, null, CN_IDCONJUNTO);
        if(cursor.moveToFirst()) {
            do {
                Producto producto = this.obtenerProductoById(cursor.getString(2));
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public boolean eliminarProducto_Conjunto(Producto producto,Conjunto conjunto) {
        db.delete(TABLE_PRODUCTO_CONJUNTO, CN_IDPRODUCTO + "=? AND " + CN_IDCONJUNTO + "=?", new String[]{producto.getId(),conjunto.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Conjunto(Conjunto conjunto) {
        db.delete(TABLE_PRODUCTO_CONJUNTO, CN_IDCONJUNTO + "=?", new String[]{conjunto.getId()});
        return true;
    }

            /*TODO TICKETS*/

    private static final String CREATE_TABLE_TICKET = "create table " + TABLE_TICKET + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_FECHA + " text,"
            + CN_ANO + " integer,"
            + CN_MES + " integer,"
            + CN_DIA + " integer,"
            + CN_HORA + " text,"
            + CN_LOCAL + " text,"
            + CN_TOTAL + " integer"
            + ");";

    private ContentValues generarValoresTicket(Ticket ticket){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA, ticket.getFecha());
        valores.put(CN_ANO, ticket.getAno());
        valores.put(CN_MES, ticket.getMes());
        valores.put(CN_DIA, ticket.getDia());
        valores.put(CN_HORA,ticket.getHora());
        valores.put(CN_TOTAL, ticket.getTotal());
        valores.put(CN_LOCAL, ticket.getLocal().getId());
        return valores;
    }

    @Override
    public boolean existeTablaTickets(){
        return this.isTableExists(db,"ticket");
    }

    @Override
    public void crearTablaTicket(){
        this.eliminarTabla(TABLE_TICKET);
        db.execSQL(CREATE_TABLE_TICKET);
    }

    @Override
    public long insertarTicket(Ticket ticket) {
        long id = db.insert(TABLE_TICKET, null, generarValoresTicket(ticket));
        ticket.setIdTicket(String.valueOf(id));
        insertarFacts(ticket);

        return id;
    }

    @Override
    public ArrayList<Ticket> obtenerTickets(String orden) {
        String[] columnas = new String[] {CN_ID,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        ArrayList<Ticket> tickets = new ArrayList<>();
        Ticket ticket;
        Cursor cursor = db.query(TABLE_TICKET,columnas,null,null,null,null,orden);
        if(cursor.moveToFirst()) {
            do {
                ticket = new Ticket();
                ticket.setIdTicket(cursor.getString(0));
                ticket.setFecha(cursor.getString(1));

                StringTokenizer tokenizer = new StringTokenizer(cursor.getString(1),"/");
                ticket.setDia(Integer.parseInt(tokenizer.nextToken()));
                ticket.setMes(Integer.parseInt(tokenizer.nextToken()));
                ticket.setAno(Integer.parseInt(tokenizer.nextToken()));

                ticket.setHora(cursor.getString(2));
                ticket.setLocal(obtenerLocalById(cursor.getString(3)));
                ticket.setTotal(cursor.getFloat(4));
                ticket.setProductos(obtenerFacts(ticket.getIdTicket()));

                tickets.add(ticket);

            } while (cursor.moveToNext());

        }
        cursor.close();
        return tickets;
    }

    @Override
    public Ticket obtenerTicket(String id) {
        String[] columnas = new String[] {CN_ID,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        Ticket ticket = new Ticket();
        Cursor cursor = db.query(TABLE_TICKET,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                ticket.setIdTicket(cursor.getString(0));
                ticket.setFecha(cursor.getString(1));
                ticket.setHora(cursor.getString(2));
                ticket.setLocal(obtenerLocalById(cursor.getString(3)));
                ticket.setTotal(cursor.getFloat(4));
                ArrayList<Producto> productos = obtenerFacts(id);
                ticket.setProductos(productos);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return ticket;
    }

    @Override
    public Ticket obtenerProductos_Ticket(Ticket ticket) {
        String[] columnas = new String[] {CN_ID,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        ArrayList<Producto> productos;
        Cursor cursor = db.query(TABLE_TICKET, columnas,CN_ID + "=?", new String[]{ticket.getIdTicket()}, null, null, null);
        productos = obtenerFacts(ticket.getIdTicket());
        ticket.setProductos(productos);
        cursor.close();
        return ticket;
    }

    @Override
    public boolean modificarTicket(Ticket ticket) {
        ContentValues values = generarValoresTicket(ticket);
        eliminarFact(ticket.getIdTicket());
        insertarFacts(ticket);

        int result = db.update(TABLE_TICKET, values, CN_ID + " = ?", new String[]{ticket.getIdTicket()});
        return result == 1;
    }

    @Override
    public boolean eliminarTicket(Ticket ticket){
        db.delete(TABLE_TICKET, CN_IDTICKET + "=?", new String[]{ticket.getIdTicket()});
        eliminarFact(ticket.getIdTicket());
        return true;
    }

    @Override
    public boolean eliminarTicketById(String idTicket){
        db.delete(TABLE_TICKET, CN_ID + "=?", new String[]{idTicket});
        eliminarFact(idTicket);
        return true;
    }
            /*TODO TABLA FACT*/

    private static final String CREATE_TABLE_FACT = "create table " + TABLE_FACT + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDTICKET + " text not null,"
            + CN_IDPRODUCTO + " text not null,"
            + CN_PRECIO + " real"
            + ");";

    private ContentValues generarValoresFact(Fact fact){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDTICKET, fact.getIdTicket());
        valores.put(CN_IDPRODUCTO, fact.getIdProducto());
        valores.put(CN_PRECIO, fact.getPrecio());

        return valores;
    }

    @Override
    public boolean existeTablaFact(){
        return this.isTableExists(db,"fact");
    }

    @Override
    public void crearTablaFact(){
        this.eliminarTabla(TABLE_FACT);
        db.execSQL(CREATE_TABLE_FACT);
    }

    @Override
    public long insertarFact(Fact fact) {
        return db.insert(TABLE_FACT, null, generarValoresFact(fact));
    }

    @Override
    public boolean insertarFacts(Ticket ticket) {
        ArrayList<Producto> productos = obtenerLocal_Productos(ticket.getLocal());

        for (Producto producto :
                ticket.getProductos()) {
            if(producto.getId().equals("-1")){
                long idProducto = insertarProducto(producto);
                producto.setId(String.valueOf(idProducto));
            }

            if(!productos.contains(producto)){
                insertarProducto_Local(producto,ticket.getLocal(),producto.getPrecio());
            }

            Fact fact = new Fact(ticket.getIdTicket(),producto.getId(),producto.getPrecio());
            db.insert(TABLE_FACT, null, generarValoresFact(fact));
        }
        return true;
    }

    @Override
    public ArrayList<Producto> obtenerFacts(String idTicket){
        String[] columnas = new String[] {CN_ID,CN_IDTICKET,CN_IDPRODUCTO,CN_PRECIO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_FACT, columnas,CN_IDTICKET + "=?", new String[]{idTicket}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Producto producto = this.obtenerProductoById(cursor.getString(2));
                producto.setPrecio(Double.parseDouble(cursor.getString(3)));
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public boolean eliminarFact(String idTicket){
        db.delete(TABLE_FACT, CN_IDTICKET + "=?", new String[]{idTicket});
        return true;
    }
    /*----PRODUCTO_TIPO*/
    private static final String TABLE_PRODUCTO_TIPO = "producto_tipo";
    private static final String CN_IDTIPO = "idtipo";
    private static final String CREATE_TABLE_PRODUCTO_TIPO = "create table " + TABLE_PRODUCTO_TIPO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDTIPO + " integer not null"
            + ");";




    /*----PRODUCTO_INGREDIENTE*/
    private static final String TABLE_PRODUCTO_INGREDIENTE = "producto_ingrediente";
    private static final String CN_NOMBREINGREDIENTE = "nombreingrediente";
    private static final String CN_NOMBREPRODUCTO = "nombreproducto";
    private static final String CREATE_TABLE_PRODUCTO_INGREDIENTE = "create table " + TABLE_PRODUCTO_INGREDIENTE + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBREPRODUCTO + " text not null,"
            + CN_NOMBREINGREDIENTE + " text not null"
            + ");";


}
