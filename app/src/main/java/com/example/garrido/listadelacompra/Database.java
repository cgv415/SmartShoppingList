package com.example.garrido.listadelacompra;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Carlos on 03/09/2018.
 */
public interface Database {
    void eliminarTabla(String nombreTabla);
    boolean isTableExists(SQLiteDatabase db, String tableName);
    void actualizar();
    //int login(String nick, String password, boolean recordar);

            /*PRODUCTO*/
    void crearTablaProducto();
    long insertarProducto(Producto producto);

    ArrayList<Producto> obtenerProductos();
    ArrayList<Producto> obtenerProductosByCategoria(Categoria categoria);
   // ArrayList<Producto> obtenerProductosByLocal(Local local);
    Producto obtenerProductoByNombre(String nombre);
    Producto obtenerProductoById(String id);
    ArrayList<String> obtenerNombreProductos();
    TreeMap<String,String> obtenerLocales_Producto(String nombre);

    boolean modificarProducto(Producto producto);

    boolean eliminarProducto(Producto producto);

            /*LOCAL*/
    void crearTablaLocal();
    long insertarLocal(Local local);

    ArrayList<Local> obtenerLocales();
    Local obtenerLocalById(String id);
    Local obtenerLocalByNombre(String nombre);
    ArrayList<String> obtenerNombreLocales();

    boolean modificarLocal(Local local);

    boolean eliminarLocal(Local local);

            /*CATEGORIAS*/
    void crearTablaCategoria();
    long insertarCategoria(Categoria categoria);

    ArrayList<Categoria> obtenerCategorias();
    ArrayList<String> obtenerNombreCategorias();
    Categoria obtenerCategoriaById(String id);
    Categoria obtenerCategoria(String nombre);

    boolean modificarCategoria(Categoria categoria);

    boolean eliminarCategoria(Categoria categoria);

            /*PRODUCTO_LOCAL*/
    void crearTablaProducto_Local();

    long insertarProducto_Local(Producto producto, Local local, Double precio);

    Map<String,ArrayList<Producto>> obtenerLocales_Productos();
    ArrayList<Producto> obtenerLocal_Productos(Local local);
    Local obtenerLocal(String nombre);
    double obtenerProducto_Precio(Local local,Producto pro);

    boolean modificarProducto_Local(Producto producto, Local local, Double precio);

    boolean eliminarProducto_Local(Producto producto,Local local);
    boolean eliminarProductos_Local(Local local);


            /*PRODUCTO_CATEGORIA*/
    void crearTablaProducto_Categoria();

    long insertarProducto_Categoria(Producto producto, Categoria categoria);

    Map<Categoria,Producto> obtenerCategorias_Productos();
    ArrayList<Producto> obtenerProductos_Categoria(Categoria categoria);

    boolean modificarProducto_Categoria(String id, Producto producto, Categoria categoria);

    boolean eliminarProducto_Categoria(Producto producto);
    boolean eliminarProductos_Categoria(Categoria categoria);

        /*LISTA*/

    void crearTablaLista();

    //Pueden haber varias listas (barbacoa,de la casa, del cortijo,...)
    long insertarLista(Lista lista);

    ArrayList<Lista> obtenerListas();
    ArrayList<String> obtenerNombreListas();
    Lista obtenerListaById(String id);
    Lista obtenerListaByNombre(String nombre);
    Lista obtenerListaPrincipal();

    boolean modificarLista(Lista lista);

    boolean eliminarLista(Lista lista);

        /*PRODUCTO_LISTA*/

    void crearTablaProducto_Lista();

    long insertarProducto_Lista(Producto producto, Lista lista);
    long insertarProductos_Lista(ArrayList<Producto> productos, Lista lista);

    Map<Lista,Producto> obtenerListas_Productos();

    ArrayList<Producto> obtenerProductos_Lista(Lista lista);

    boolean eliminarProducto_Lista(Producto producto, Lista lista);

    boolean eliminarProductos_Lista(Lista lista);

    /*CONJUNTO*/

    void crearTablaConjunto();

    long insertarConjunto(Conjunto conjunto);

    ArrayList<Conjunto> obtenerConjuntos();
    ArrayList<String> obtenerNombreConjuntos();
    Conjunto obtenerConjuntoById(String id);
    Conjunto obtenerConjuntoByNombre(String nombre);

    boolean modificarConjunto(Conjunto conjunto);

    boolean eliminarConjunto(Conjunto conjunto);

    /*PRODUCTO_CONJUNTO*/

    void crearTablaProducto_Conjunto();

    long insertarProducto_Conjunto(Producto producto, Conjunto conjunto);

    Map<Conjunto,Producto> obtenerConjuntos_Productos();

    ArrayList<Producto> obtenerProductos_Conjunto(Conjunto conjunto);

    boolean eliminarProducto_Conjunto(Producto producto, Conjunto conjunto);

    boolean eliminarProductos_Conjunto(Conjunto conjunto);

    /*TICKET*/

    void crearTablaTicket();

    long insertarTicket(Ticket ticket);

    ArrayList<Ticket> obtenerTickets(String orden);
    Ticket obtenerTicket(String id);
    Ticket obtenerProductos_Ticket(Ticket ticket);

    boolean modificarTicket(Ticket ticket);
    boolean eliminarTicket(Ticket ticket);
    boolean eliminarTicketById(String idTicket);

    /*FACT*/
    void crearTablaFact();

    long insertarFact(Fact fact);
    boolean insertarFacts(Ticket ticket);

    ArrayList<Producto> obtenerFacts(String idTicket);

    boolean eliminarFact(String idTicket);

    boolean existeTablaCategoria();
    boolean existeTablaConjunto();
    boolean existeTablaFact();
    boolean existeTablaLista();
    boolean existeTablaLocal();
    boolean existeTablaProducto();
    boolean existeTablaTickets();
}
