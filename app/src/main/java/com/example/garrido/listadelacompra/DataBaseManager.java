package com.example.garrido.listadelacompra;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Map;
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

        this.crearTablaLocal();
        this.crearTablaProducto();
        this.crearTablaCategoria();
        this.crearTablaSubcategoria();
        this.crearTablaProducto_Local();
        this.crearTablaProducto_Categoria();
        this.crearTablaProducto_Subcategoria();
        this.crearTablaCategoria_Subcategoria();

        this.crearTablaTicket();
        this.crearTablaFact();


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
        actualizar();
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
        local.setNif("A-23425270");
        local.setDireccion("C/Campezo 16-Poligono Las Mercedes,28022 Madrid");
        local.setTlfn("902202000");
        local.setHorario("LUN-SAB, 09:30-22:00");
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

        long idinsert = insertarProducto_Local(cacacola,mercadona);
        long idinsert2 = insertarProducto_Local(libritos,mercadona);
        long idinsert3 = insertarProducto_Local(pipas,mercadona);
        //insertarLocal(l);

        ArrayList<Categoria> categorias = obtenerCategorias();
        ArrayList<Subcategoria> subcategorias = obtenerSubcategorias();
        ArrayList<Local> locales = obtenerLocales();

        ArrayList<Ticket> tickets = this.obtenerTickets();
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
        } else if (id == R.id.nav_listas) {
            intent = new Intent(context,MainActivity.class);
        } else if (id == R.id.nav_productos) {
            intent = new Intent(context,Activity_Productos.class);
        } else if (id == R.id.nav_conjuntos) {
            intent = new Intent(context,MainActivity.class);
        } else if (id == R.id.nav_locales) {
            intent = new Intent(context,MainActivity.class);
        } else if (id == R.id.nav_tickets) {
            intent = new Intent(context,Activity_Tickets.class);
        } else if (id == R.id.nav_est) {
            intent = new Intent(context,MainActivity.class);
        }else if (id == R.id.nav_faq) {
            intent = new Intent(context,MainActivity.class);
        }else{
            intent = new Intent(context,MainActivity.class);
        }
        context.startActivity(intent);

    }

            /*TABLAS*/
    private static final String TABLE_PRODUCTO = "producto";
    private static final String TABLE_LOCAL = "local";
    private static final String TABLE_CATEGORIA = "categoria";
    private static final String TABLE_SUBCATEGORIA = "subcategoria";
    private static final String TABLE_LISTA = "lista";
    private static final String TABLE_CONJUNTO = "conjunto";
    private static final String TABLE_TICKET = "ticket";
    private static final String TABLE_FACT = "fact";

    private static final String TABLE_PRODUCTO_LOCAL = "producto_local";
    private static final String TABLE_PRODUCTO_CATEGORIA = "producto_categoria";
    private static final String TABLE_PRODUCTO_SUBCATEGORIA = "producto_subcategoria";
    private static final String TABLE_PRODUCTO_LISTA = "producto_lista";
    private static final String TABLE_PRODUCTO_CONJUNTO = "producto_conjunto";
    private static final String TABLE_CATEGORIA_SUBCATEGORIA = "categoria_subcategoria";

            /*COLUMNAS COMUNES*/
    private static final String CN_ID = "_id";
    private static final String CN_NOMBRE = "nombre";

            /*COLUMNAS TABLA PRODUCTO*/
    private static final String CN_DESCRIPCION = "descripcion";
    private static final String CN_ETIQUETA = "etiqueta";
    private static final String CN_CATEGORIA = "categoria";
    private static final String CN_SUBCATEGORIA = "subcategoria";
    private static final String CN_LOCAL = "local";
    private static final String CN_PRECIO = "precio";
    private static final String CN_MARCA = "marca";

            /*COLUMNAS TABLA LOCAL*/
    private static final String CN_DIRECCION = "direccion";
    private static final String CN_NIF = "nif";
    private static final String CN_WEB = "web";
    private static final String CN_TLFN = "tlfn";
    private static final String CN_HORARIO = "horario";

            /*COLUMNAS TABLA TICKET*/

    private static final String CN_FECHA = "fecha";
    private static final String CN_HORA = "hora";
    private static final String CN_TOTAL = "total";
    private static final String CN_IDTICKET = "idticket";

            /*COLUMNAS TABLAS CRUZADAS*/
    private static final String CN_IDPRODUCTO = "idproducto";
    private static final String CN_IDLOCAL = "idlocal";
    private static final String CN_IDCATEGORIA = "idcategoria";
    private static final String CN_IDSUBCATEGORIA = "idsubcategoria";
    private static final String CN_IDLISTA = "idlista";
    private static final String CN_IDCONJUNTO = "idconjunto";

            /*todo CREACION TABLA PRODUCTO*/
    private static final String CREATE_TABLE_PRODUCTO = "create table " + TABLE_PRODUCTO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null,"
            + CN_DESCRIPCION + " text,"
            + CN_ETIQUETA + " text,"
            + CN_CATEGORIA + " text,"
            + CN_SUBCATEGORIA + " text,"
            + CN_LOCAL + " text,"
            + CN_MARCA + " text,"
            + CN_PRECIO + " real"
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
        valores.put(CN_MARCA,producto.getMarca());
        valores.put(CN_SUBCATEGORIA, producto.getSubcategoria().getId());
        if(producto.getLocal() != null){
            valores.put(CN_LOCAL,producto.getLocal().getId());
        }else{
            valores.put(CN_LOCAL,"-1");
        }
        valores.put(CN_PRECIO,producto.getPrecio());
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
        /*Categoria categoria = this.obtenerCategoria(producto.getCategoria().getNombre());
        if(categoria.isNull()){
            insertarCategoria(producto.getCategoria());
            categoria = obtenerCategoria(producto.getCategoria().getNombre());
            producto.setCategoria(categoria);
        }

        Subcategoria subcategoria = this.obtenerSubcategoria(producto.getSubcategoria().getNombre());
        if(subcategoria.isNull()){
            insertarSubcategoria(producto.getSubcategoria());
            subcategoria = obtenerSubcategoria(producto.getSubcategoria().getNombre());
            producto.setSubcategoria(subcategoria);
        }

        Local local = this.obtenerLocal(producto.getLocal().getNombre());
        if(producto.getLocal() != null && local.isNull()){
            insertarLocal(producto.getLocal());
            local = obtenerLocal(producto.getLocal().getNombre());
            producto.setLocal(local);
        }*/
        insertarProducto_Categoria(producto,producto.getCategoria());
        insertarProducto_Subcategoria(producto,producto.getSubcategoria());
        if(producto.getLocal() != null){
            insertarProducto_Local(producto,producto.getLocal());
        }
        long id = db.insert(TABLE_PRODUCTO, null, generarValoresProducto(producto));
        return id;
    }
    @Override
    public ArrayList<Producto> obtenerProductos() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA,CN_SUBCATEGORIA,CN_LOCAL,CN_MARCA,CN_PRECIO};
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
                producto.setSubcategoria(this.obtenerSubcategoriaById(cursor.getInt(5)+""));
                producto.setLocal(this.obtenerLocalById(cursor.getInt(6)+""));
                producto.setMarca(cursor.getString(7));
                producto.setPrecio(cursor.getDouble(8));

                productos.add(producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public ArrayList<Producto> obtenerProductosByCategoria(Categoria categoria) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA,CN_SUBCATEGORIA,CN_LOCAL,CN_MARCA,CN_PRECIO};
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
                producto.setSubcategoria(new Subcategoria(cursor.getString(5)));
                producto.setLocal(new Local(cursor.getString(6)));
                producto.setMarca(cursor.getString(7));
                producto.setPrecio(cursor.getDouble(8));

                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public ArrayList<Producto> obtenerProductosByLocal(Local local) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA,CN_SUBCATEGORIA,CN_LOCAL,CN_MARCA,CN_PRECIO};
        ArrayList<Producto> productos = new ArrayList<>();
        Producto producto;
        Cursor cursor = db.query(TABLE_PRODUCTO, columnas, CN_LOCAL + " = ?", new String[]{local.getNombre()}, null, null, CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                producto = new Producto();
                producto.setId(cursor.getString(0));
                producto.setNombre(cursor.getString(1));
                producto.setDescripcion(cursor.getString(2));
                producto.setEtiqueta(cursor.getString(3));
                producto.setCategoria(new Categoria(cursor.getString(4)));
                producto.setSubcategoria(new Subcategoria(cursor.getString(5)));
                producto.setLocal(new Local(cursor.getString(6)));
                producto.setMarca(cursor.getString(7));
                producto.setPrecio(cursor.getDouble(8));

                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }


    @Override
    public Producto obtenerProductoByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA,CN_SUBCATEGORIA,CN_LOCAL,CN_MARCA,CN_PRECIO};
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
                Subcategoria subcategoria = obtenerSubcategoriaById(cursor.getString(5));
                producto.setSubcategoria(subcategoria);
                producto.setLocal(new Local(cursor.getString(6)));
                producto.setMarca(cursor.getString(7));
                producto.setPrecio(cursor.getDouble(8));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return producto;
    }

    @Override
    public Producto obtenerProductoById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION, CN_ETIQUETA,CN_CATEGORIA,CN_SUBCATEGORIA,CN_LOCAL,CN_MARCA,CN_PRECIO};
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
                String idSubcategoria = cursor.getString(5);
                producto.setSubcategoria(obtenerSubcategoriaById(idSubcategoria));
                producto.setLocal(new Local(cursor.getString(6)));
                producto.setMarca(cursor.getString(7));
                producto.setPrecio(cursor.getDouble(8));

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
        eliminarProducto_Subcategoria(producto);

        int del = db.delete(TABLE_PRODUCTO, CN_NOMBRE + "=?", new String[]{producto.getNombre()});
        return true;
    }

            /*todo CREACION TABLA LOCAL*/

    private static final String CREATE_TABLE_LOCAL = "create table " + TABLE_LOCAL + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null,"
            + CN_DIRECCION + " text,"
            + CN_NIF + " text,"
            + CN_WEB + " text,"
            + CN_TLFN + " text,"
            + CN_HORARIO + " text"
            + ");";

    private ContentValues generarValoresLocal(Local local){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, local.getNombre());
        valores.put(CN_DIRECCION, local.getDireccion());
        valores.put(CN_HORARIO,local.getHorario());
        valores.put(CN_NIF, local.getNif());
        valores.put(CN_TLFN, local.getTlfn());
        valores.put(CN_WEB, local.getWeb());
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
        long id = db.insert(TABLE_LOCAL, null, generarValoresLocal(local));
        return id;
    }

    @Override
    public ArrayList<Local> obtenerLocales() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DIRECCION,CN_HORARIO,CN_NIF,CN_TLFN,CN_WEB};
        ArrayList<Local> locales = new ArrayList<>();
        Local local;
        Cursor cursor = db.query(TABLE_LOCAL,columnas,null,null,null,null,CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                local = new Local();
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));
                local.setDireccion(cursor.getString(2));
                local.setHorario(cursor.getString(3));
                local.setNif(cursor.getString(4));
                local.setTlfn(cursor.getString(5));
                local.setWeb(cursor.getString(6));

                locales.add(local);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return locales;
    }

    @Override
    public Local obtenerLocalByNombre(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DIRECCION,CN_HORARIO,CN_NIF,CN_TLFN,CN_WEB};
        Local local = new Local();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));
                local.setDireccion(cursor.getString(2));
                local.setHorario(cursor.getString(3));
                local.setNif(cursor.getString(4));
                local.setTlfn(cursor.getString(5));
                local.setWeb(cursor.getString(6));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return local;
    }

    @Override
    public Local obtenerLocalById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DIRECCION,CN_HORARIO,CN_NIF,CN_TLFN,CN_WEB};
        Local local = new Local();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));
                local.setDireccion(cursor.getString(2));
                local.setHorario(cursor.getString(3));
                local.setNif(cursor.getString(4));
                local.setTlfn(cursor.getString(5));
                local.setWeb(cursor.getString(6));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return local;
    }

    @Override
    public Local obtenerLocal(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DIRECCION,CN_HORARIO,CN_NIF,CN_TLFN,CN_WEB};
        Local local = new Local();
        Cursor cursor = db.query(TABLE_LOCAL,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                local.setId(cursor.getString(0));
                local.setNombre(cursor.getString(1));
                local.setDireccion(cursor.getString(2));
                local.setHorario(cursor.getString(3));
                local.setNif(cursor.getString(4));
                local.setTlfn(cursor.getString(5));
                local.setWeb(cursor.getString(6));

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
        db.delete(TABLE_LOCAL, CN_ID + "=?", new String[]{local.getId()});
        return true;
    }

            /*todo CREACION TABLA CATEGORIA */


    private static final String CREATE_TABLE_CATEGORIA = "create table " + TABLE_CATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null"
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
    }

    @Override
    public long insertarCategoria(Categoria categoria) {
        long id = db.insert(TABLE_CATEGORIA, null, generarValoresCategoria(categoria));
        return id;
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

        /*todo CREACION TABLA SUBCATEGORIA */

    private static final String CREATE_TABLE_SUBCATEGORIA = "create table " + TABLE_SUBCATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null"
            + ");";

    private ContentValues generarValoresSubcategoria(Subcategoria subcategoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, subcategoria.getNombre());
        return valores;
    }

    @Override
    public boolean existeTablaSubcategoria(){
        return this.isTableExists(db,"subcategoria");
    }

    @Override
    public void crearTablaSubcategoria(){
        this.eliminarTabla(TABLE_SUBCATEGORIA);
        db.execSQL(CREATE_TABLE_SUBCATEGORIA);
    }

    @Override
    public long insertarSubcategoria(Subcategoria subcategoria) {
        long id = db.insert(TABLE_SUBCATEGORIA, null, generarValoresSubcategoria(subcategoria));
        return id;
    }

    @Override
    public ArrayList<Subcategoria> obtenerSubcategorias() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        ArrayList<Subcategoria> subcategorias = new ArrayList<>();
        Cursor cursor = db.query(TABLE_SUBCATEGORIA, columnas, null, null, null, null, CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                Subcategoria subcategoria = new Subcategoria(cursor.getString(1));
                subcategoria.setId(cursor.getString(0));
                subcategorias.add(subcategoria);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subcategorias;
    }

    @Override
    public ArrayList<String> obtenerNombreSubcategorias() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        ArrayList<String> subcategorias = new ArrayList<>();
        Cursor cursor = db.query(TABLE_SUBCATEGORIA, columnas, null, null, null, null, CN_NOMBRE);
        if(cursor.moveToFirst()) {
            do {
                subcategorias.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subcategorias;
    }


    @Override
    public Subcategoria obtenerSubcategoriaById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Subcategoria subcategoria = new Subcategoria();
        Cursor cursor = db.query(TABLE_SUBCATEGORIA,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                subcategoria.setId(cursor.getString(0));
                subcategoria.setNombre(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return subcategoria;
    }

    @Override
    public Subcategoria obtenerSubcategoria(String nombre) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE};
        Subcategoria subcategoria = new Subcategoria();
        Cursor cursor = db.query(TABLE_SUBCATEGORIA,columnas,CN_NOMBRE+ " = ?",new String[]{nombre},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                subcategoria.setId(cursor.getInt(0) + "");
                subcategoria.setNombre(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return subcategoria;
    }

    @Override
    public boolean modificarSubcategoria(Subcategoria subcategoria) {
        ContentValues values = generarValoresSubcategoria(subcategoria);
        int result = db.update(TABLE_SUBCATEGORIA, values, CN_ID + " = ?", new String[]{subcategoria.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarSubcategoria(Subcategoria subcategoria) {
        db.delete(TABLE_SUBCATEGORIA, CN_ID + "=?", new String[]{subcategoria.getId()});
        return true;
    }

            /*todo CREACION TABLA CATEGORIA_SUBCATEGORIA */

    private static final String CREATE_TABLE_CATEGORIA_SUBCATEGORIA = "create table " + TABLE_CATEGORIA_SUBCATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDCATEGORIA + " integer not null,"
            + CN_IDSUBCATEGORIA + " integer not null"
            + ");";


    @Override
    public void crearTablaCategoria_Subcategoria(){
        this.eliminarTabla(TABLE_CATEGORIA_SUBCATEGORIA);
        db.execSQL(CREATE_TABLE_CATEGORIA_SUBCATEGORIA);
    }

    @Override
    public long insertarCategoria_Subcategoria(Categoria categoria, Subcategoria subcategoria) {
        long id = db.insert(TABLE_CATEGORIA_SUBCATEGORIA, null, generarValoresCategoria_Subcategoria(categoria, subcategoria));
        return id;
    }

    @Override
    public Map<Categoria,Subcategoria> obtenerCategorias_Subcategorias() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDCATEGORIA,CN_IDSUBCATEGORIA};
        Map<Categoria,Subcategoria> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_CATEGORIA_SUBCATEGORIA, columnas, null, null, null, null, CN_IDCATEGORIA);
        if(cursor.moveToFirst()) {
            do {
                Categoria categoria = this.obtenerCategoriaById(cursor.getString(1));
                Subcategoria subcategoria = this.obtenerSubcategoriaById(cursor.getString(2));

                mapa.put(categoria,subcategoria);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Subcategoria> obtenerSubcategorias_Categoria(Categoria categoria) {
        String[] columnas = new String[] {CN_ID,CN_IDCATEGORIA,CN_IDSUBCATEGORIA};
        ArrayList<Subcategoria> subcategorias = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CATEGORIA_SUBCATEGORIA, columnas, CN_IDCATEGORIA + " = ?", new String[]{categoria.getId()}, null, null, CN_IDSUBCATEGORIA);
        if(cursor.moveToFirst()) {
            do {
                Subcategoria subcategoria = this.obtenerSubcategoriaById(cursor.getString(2));
                subcategorias.add(subcategoria);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subcategorias;
    }

    @Override
    public boolean eliminarSubcategorias_Categoria(Categoria categoria) {
        db.delete(TABLE_PRODUCTO_CATEGORIA, CN_IDCATEGORIA + "=?", new String[]{categoria.getId()});
        return true;
    }

    @Override
    public boolean eliminarSubcategorias_Categoria(ArrayList<Subcategoria> subcategorias) {
        for (Subcategoria sub:subcategorias) {
            db.delete(TABLE_PRODUCTO_CATEGORIA, CN_IDSUBCATEGORIA + "=?", new String[]{sub.getId()});
        }

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
    }

    @Override
    public long insertarProducto_Categoria(Producto producto, Categoria categoria) {
        long id = db.insert(TABLE_PRODUCTO_CATEGORIA, null, generarValoresProducto_Categoria(producto, categoria));
        return id;
    }





            /*todo CREACION TABLA PRODUCTO_LOCAL */

    private static final String CREATE_TABLE_PRODUCTO_LOCAL = "create table " + TABLE_PRODUCTO_LOCAL + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDLOCAL + " integer not null"
            + ");";

    private ContentValues generarValoresProducto_Local(Producto producto, Local local){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDLOCAL, local.getId());
        return valores;
    }

    @Override
    public void crearTablaProducto_Local(){
        this.eliminarTabla(TABLE_PRODUCTO_LOCAL);
        db.execSQL(CREATE_TABLE_PRODUCTO_LOCAL);
    }

    @Override
    public long insertarProducto_Local(Producto producto, Local local) {
        long id = db.insert(TABLE_PRODUCTO_LOCAL, null, generarValoresProducto_Local(producto, local));
        return id;
    }

    @Override
    public Map<Local,Producto> obtenerLocales_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDLOCAL,CN_IDPRODUCTO};
        Map<Local,Producto> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, null, null, null, null, CN_IDLOCAL);
        if(cursor.moveToFirst()) {
            do {
                Local local = this.obtenerLocalById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));

                mapa.put(local,producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerLocal_Productos(Local local) {
        String[] columnas = new String[] {CN_ID,CN_IDLOCAL,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_LOCAL, columnas, CN_IDLOCAL + " = ?", new String[]{local.getId()}, null, null, CN_IDLOCAL);
        if(cursor.moveToFirst()) {
            do {
                String id = cursor.getString(2);
                Producto producto = this.obtenerProductoById(id);
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    @Override
    public boolean modificarProducto_Local(String id, Producto producto, Local local) {
        ContentValues values = generarValoresProducto_Local(producto,local);
        int result = db.update(TABLE_PRODUCTO_LOCAL, values, CN_ID + " = ?", new String[]{id});
        return result == 1;
    }

    @Override
    public boolean eliminarProducto_Local(Producto producto) {
        db.delete(TABLE_PRODUCTO_LOCAL, CN_IDPRODUCTO + "=?", new String[]{producto.getId()});
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

    private ContentValues generarValoresCategoria_Subcategoria(Categoria categoria, Subcategoria subcategoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDCATEGORIA, categoria.getId());
        valores.put(CN_IDSUBCATEGORIA, subcategoria.getId());
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

            /*todo CREACION TABLA PRODUCTO_SUBCATEGORIA */

    private static final String CREATE_TABLE_PRODUCTO_SUBCATEGORIA = "create table " + TABLE_PRODUCTO_SUBCATEGORIA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDPRODUCTO + " integer not null,"
            + CN_IDSUBCATEGORIA + " integer not null"
            + ");";

    private ContentValues generarValoresProducto_Subcategoria(Producto producto, Subcategoria subcategoria){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDPRODUCTO, producto.getId());
        valores.put(CN_IDSUBCATEGORIA, subcategoria.getId());
        return valores;
    }

    @Override
    public void crearTablaProducto_Subcategoria(){
        this.eliminarTabla(TABLE_PRODUCTO_SUBCATEGORIA);
        db.execSQL(CREATE_TABLE_PRODUCTO_SUBCATEGORIA);
    }

    @Override
    public long insertarProducto_Subcategoria(Producto producto, Subcategoria subcategoria) {
        long id = db.insert(TABLE_PRODUCTO_SUBCATEGORIA, null, generarValoresProducto_Subcategoria(producto, subcategoria));
        return id;
    }

    @Override
    public Map<Subcategoria,Producto> obtenerSubcategorias_Productos() {
        /*probablemente habra que cambiar a Map<Local,ArrayList<Producto>>*/
        String[] columnas = new String[] {CN_ID,CN_IDSUBCATEGORIA,CN_IDPRODUCTO};
        Map<Subcategoria,Producto> mapa = new TreeMap<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_SUBCATEGORIA, columnas, null, null, null, null, CN_IDSUBCATEGORIA);
        if(cursor.moveToFirst()) {
            do {
                Subcategoria subcategoria = this.obtenerSubcategoriaById(cursor.getString(1));
                Producto producto = this.obtenerProductoById(cursor.getString(2));

                mapa.put(subcategoria,producto);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mapa;
    }

    @Override
    public ArrayList<Producto> obtenerProductos_Subcategoria(Subcategoria subcategoria) {
        String[] columnas = new String[] {CN_ID,CN_IDSUBCATEGORIA,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_PRODUCTO_SUBCATEGORIA, columnas, CN_ID + " = ?", new String[]{subcategoria.getId()}, null, null, null);
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
    public boolean modificarProducto_Subcategoria(String id, Producto producto, Subcategoria subcategoria) {
        ContentValues values = generarValoresProducto_Subcategoria(producto,subcategoria);
        int result = db.update(TABLE_PRODUCTO_SUBCATEGORIA, values, CN_ID + " = ?", new String[]{id});
        return result == 1;
    }

    @Override
    public boolean eliminarProducto_Subcategoria(Producto producto) {
        db.delete(TABLE_PRODUCTO_SUBCATEGORIA, CN_IDPRODUCTO + "=?", new String[]{producto.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Subcategoria(Subcategoria subcategoria) {
        db.delete(TABLE_PRODUCTO_SUBCATEGORIA, CN_IDSUBCATEGORIA + "=?", new String[]{subcategoria.getId()});
        return true;
    }

            /*todo CREACION TABLA LISTA */

    private static final String CREATE_TABLE_LISTA = "create table " + TABLE_LISTA + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null,"
            + CN_DESCRIPCION + " text not null"
            + ");";

    private ContentValues generarValoresLista(Lista lista){
        /* Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, lista.getNombre());
        valores.put(CN_DESCRIPCION, lista.getNombre());
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
        long id = db.insert(TABLE_LISTA, null, generarValoresLista(lista));
        return id;
    }

    @Override
    public ArrayList<Lista> obtenerListas() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        ArrayList<Lista> lista = new ArrayList<>();
        Cursor cursor = db.query(TABLE_LISTA, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {

                lista.add(new Lista(cursor.getString(1),cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    @Override
    public Lista obtenerListaById(String id) {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        Lista lista = new Lista();
        Cursor cursor = db.query(TABLE_LISTA,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                lista.setId(cursor.getString(0));
                lista.setNombre(cursor.getString(1));
                lista.setDescripcion(cursor.getString(2));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    @Override
    public boolean modificarLista(Lista lista) {
        ContentValues values = generarValoresLista(lista);
        int result = db.update(TABLE_LISTA, values, CN_ID + " = ?", new String[]{lista.getId()});
        return result == 1;
    }

    @Override
    public boolean eliminarLista(Lista lista) {
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
        long id = db.insert(TABLE_PRODUCTO_LISTA, null, generarValoresProducto_Lista(producto, lista));
        return id;
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
        Cursor cursor = db.query(TABLE_PRODUCTO_LISTA, columnas,CN_ID + "=?", new String[]{lista.getId()}, null, null, CN_IDLISTA);
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
        db.delete(TABLE_PRODUCTO_LISTA, CN_IDPRODUCTO + "=? &" + CN_IDLISTA + "=?", new String[]{producto.getId(),lista.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Lista(Lista lista) {
        db.delete(TABLE_PRODUCTO_LISTA, CN_IDLISTA + "=?", new String[]{lista.getId()});
        return true;
    }

            /*TODO CONJUNTO*/

    private static final String CREATE_TABLE_CONJUNTO = "create table " + TABLE_CONJUNTO + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_NOMBRE + " text not null,"
            + CN_DESCRIPCION + " text not null"
            + ");";

    private ContentValues generarValoresConjunto(Conjunto conjunto){
        /* Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, conjunto.getNombre());
        valores.put(CN_DESCRIPCION, conjunto.getNombre());
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
        long id = db.insert(TABLE_CONJUNTO, null, generarValoresConjunto(conjunto));
        return id;
    }

    @Override
    public ArrayList<Conjunto> obtenerConjuntos() {
        String[] columnas = new String[] {CN_ID,CN_NOMBRE,CN_DESCRIPCION};
        ArrayList<Conjunto> conjunto = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CONJUNTO, columnas, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {

                conjunto.add(new Conjunto(cursor.getString(1),cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conjunto;
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
        long id = db.insert(TABLE_PRODUCTO_CONJUNTO, null, generarValoresProducto_Conjunto(producto, conjunto));
        return id;
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
        Cursor cursor = db.query(TABLE_PRODUCTO_CONJUNTO, columnas,CN_ID + "=?", new String[]{conjunto.getId()}, null, null, CN_IDCONJUNTO);
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
        db.delete(TABLE_PRODUCTO_CONJUNTO, CN_IDPRODUCTO + "=? &" + CN_IDCONJUNTO + "=?", new String[]{producto.getId(),conjunto.getId()});
        return true;
    }

    @Override
    public boolean eliminarProductos_Conjunto(Conjunto conjunto) {
        db.delete(TABLE_PRODUCTO_CONJUNTO, CN_IDPRODUCTO + "=?", new String[]{conjunto.getId()});
        return true;
    }

            /*TODO TICKETS*/

    private static final String CREATE_TABLE_TICKET = "create table " + TABLE_TICKET + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDTICKET + " text not null,"
            + CN_FECHA + " text,"
            + CN_HORA + " text,"
            + CN_LOCAL + " text,"
            + CN_TOTAL + " integer"
            + ");";

    private ContentValues generarValoresTicket(Ticket ticket){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDTICKET, ticket.getIdTicket());
        valores.put(CN_FECHA, ticket.getFecha());
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
        insertarFacts(ticket);

        return id;
    }

    @Override
    public ArrayList<Ticket> obtenerTickets() {
        String[] columnas = new String[] {CN_ID,CN_IDTICKET,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        ArrayList<Ticket> tickets = new ArrayList<>();
        Ticket ticket;
        Cursor cursor = db.query(TABLE_TICKET,columnas,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                ticket = new Ticket();
                ticket.setId(cursor.getString(0));
                ticket.setIdTicket(cursor.getString(1));
                ticket.setFecha(cursor.getString(2));
                ticket.setHora(cursor.getString(3));
                ticket.setLocal(obtenerLocalById(cursor.getString(4)));
                ticket.setTotal(cursor.getFloat(5));

                tickets.add(ticket);

            } while (cursor.moveToNext());

        }
        cursor.close();
        return tickets;
    }

    @Override
    public Ticket obtenerTicket(String id) {
        String[] columnas = new String[] {CN_ID,CN_IDTICKET,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        Ticket ticket = new Ticket();
        Cursor cursor = db.query(TABLE_TICKET,columnas,CN_ID+ " = ?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                ticket.setId(cursor.getString(0));
                ticket.setIdTicket(cursor.getString(1));
                ticket.setFecha(cursor.getString(2));
                ticket.setHora(cursor.getString(3));
                ticket.setLocal(obtenerLocalById(cursor.getString(4)));
                ticket.setTotal(cursor.getFloat(5));

            } while (cursor.moveToNext());

        }
        cursor.close();
        return ticket;
    }

    @Override
    public Ticket obtenerProductos_Ticket(Ticket ticket) {
        String[] columnas = new String[] {CN_ID,CN_IDTICKET,CN_FECHA, CN_HORA,CN_LOCAL,CN_TOTAL};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_TICKET, columnas,CN_ID + "=?", new String[]{ticket.getId()}, null, null, null);
        productos = obtenerFacts(ticket.getIdTicket());
        ticket.setProductos(productos);
        cursor.close();
        return ticket;
    }

    public boolean eliminarTicket(Ticket ticket){
        db.delete(TABLE_TICKET, CN_IDTICKET + "=?", new String[]{ticket.getIdTicket()});
        eliminarFact(ticket.getIdTicket());
        return true;
    }
            /*TODO TABLA FACT*/

    private static final String CREATE_TABLE_FACT = "create table " + TABLE_FACT + "("
            + CN_ID + " integer primary key autoincrement,"
            + CN_IDTICKET + " text not null,"
            + CN_IDPRODUCTO + " text not null"
            + ");";

    private ContentValues generarValoresFact(Fact fact){
        /*Habra que hacer un if x!= null {val.put(CN_X,x)}*/
        ContentValues valores = new ContentValues();
        valores.put(CN_IDTICKET, fact.getIdTicket());
        valores.put(CN_IDPRODUCTO, fact.getIdProducto());

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
        long id = db.insert(TABLE_FACT, null, generarValoresFact(fact));
        return id;
    }

    @Override
    public boolean insertarFacts(Ticket ticket) {
        for (Producto producto :
                ticket.getProductos()) {
            Fact fact = new Fact(ticket.getIdTicket(),producto.getId());
            db.insert(TABLE_FACT, null, generarValoresFact(fact));
        }
        return true;
    }

    @Override
    public ArrayList<Producto> obtenerFacts(String idTicket){
        String[] columnas = new String[] {CN_ID,CN_IDTICKET,CN_IDPRODUCTO};
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cursor = db.query(TABLE_FACT, columnas,CN_IDTICKET + "=?", new String[]{idTicket}, null, null, null);
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
