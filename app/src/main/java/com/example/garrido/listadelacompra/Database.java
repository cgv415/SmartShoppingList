package com.example.garrido.listadelacompra;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;

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
    ArrayList<Producto> obtenerProductosByLocal(Local local);

    Producto obtenerProductoByNombre(String nombre);
    Producto obtenerProductoById(String id);
    ArrayList<String> obtenerNombreProductos();

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
    Categoria obtenerCategoriaById(String id);
    Categoria obtenerCategoria(String nombre);

    boolean modificarCategoria(Categoria categoria);

    boolean eliminarCategoria(Categoria categoria);

            /*SUBCATEGORIAS*/
    void crearTablaSubcategoria();
    long insertarSubcategoria(Subcategoria subcategoria);

    ArrayList<Subcategoria> obtenerSubcategorias();
    Subcategoria obtenerSubcategoriaById(String id);
    Subcategoria obtenerSubcategoria(String nombre);

    boolean modificarSubcategoria(Subcategoria subcategoria);

    boolean eliminarSubcategoria(Subcategoria subcategoria);

            /*PRODUCTO_LOCAL*/
    void crearTablaProducto_Local();

    long insertarProducto_Local(Producto producto, Local local);

    Map<Local,Producto> obtenerLocales_Productos();
    ArrayList<Producto> obtenerLocal_Productos(Local local);
    Local obtenerLocal(String nombre);

    boolean modificarProducto_Local(String id, Producto producto, Local local);

    boolean eliminarProducto_Local(Producto producto);
    boolean eliminarProductos_Local(Local local);


            /*PRODUCTO_CATEGORIA*/
    void crearTablaProducto_Categoria();

    long insertarProducto_Categoria(Producto producto, Categoria categoria);

    Map<Categoria,Producto> obtenerCategorias_Productos();
    ArrayList<Producto> obtenerProductos_Categoria(Categoria categoria);

    boolean modificarProducto_Categoria(String id, Producto producto, Categoria categoria);

    boolean eliminarProducto_Categoria(Producto producto);
    boolean eliminarProductos_Categoria(Categoria categoria);


            /*PRODUCTO_SUBCATEGORIA*/
    void crearTablaProducto_Subcategoria();

    long insertarProducto_Subcategoria(Producto producto, Subcategoria subcategoria);

    Map<Subcategoria,Producto> obtenerSubcategorias_Productos();
    ArrayList<Producto> obtenerProductos_Subcategoria(Subcategoria subcategoria);

    boolean modificarProducto_Subcategoria(String id, Producto producto, Subcategoria subcategoria);

    /*Eliminar un echo que afecta a un solo producto*/
    boolean eliminarProducto_Subcategoria(Producto producto);
    /*Elimina todos los productos con esa subcategoria todo */
    boolean eliminarProductos_Subcategoria(Subcategoria subcategoria);

        /* CATEGORIA_SUBCATEGORIA */

    void crearTablaCategoria_Subcategoria();

    long insertarCategoria_Subcategoria(Categoria categoria, Subcategoria subcategoria);

    Map<Categoria,Subcategoria> obtenerCategorias_Subcategorias();
    ArrayList<Subcategoria> obtenerSubcategorias_Categoria(Categoria categoria);

    boolean eliminarSubcategorias_Categoria(Categoria categoria);
    boolean eliminarSubcategorias_Categoria(ArrayList<Subcategoria> subcategorias);

        /*LISTA*/

    void crearTablaLista();

    //Pueden haber varias listas (barbacoa,de la casa, del cortijo,...)
    long insertarLista(Lista lista);

    ArrayList<Lista> obtenerListas();
    Lista obtenerListaById(String id);

    boolean modificarLista(Lista lista);

    boolean eliminarLista(Lista lista);

        /*PRODUCTO_LISTA*/

    void crearTablaProducto_Lista();

    long insertarProducto_Lista(Producto producto, Lista lista);

    Map<Lista,Producto> obtenerListas_Productos();

    ArrayList<Producto> obtenerProductos_Lista(Lista lista);

    boolean eliminarProducto_Lista(Producto producto, Lista lista);

    boolean eliminarProductos_Lista(Lista lista);

    /*CONJUNTO*/

    void crearTablaConjunto();

    long insertarConjunto(Conjunto conjunto);

    ArrayList<Conjunto> obtenerConjuntos();
    Conjunto obtenerConjuntoById(String id);

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

    ArrayList<Ticket> obtenerTickets();
    Ticket obtenerProductos_Ticket(Ticket ticket);

    boolean eliminarTicket(Ticket ticket);

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
    boolean existeTablaSubcategoria();
    boolean existeTablaTickets();
}
