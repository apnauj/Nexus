package com.nexus.controller;

import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.exceptions.EClienteYaExiste;
import com.nexus.exceptions.EHistorialOrden;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EProductoYaExiste;
import com.nexus.exceptions.EStockInsuficiente;
import com.nexus.exceptions.EUsuarioNoEncontrado;
import com.nexus.exceptions.EUsuarioYaExiste;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.*;
import com.nexus.model.enums.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class StoreController {
    private Orden[] historialOrdenes;
    private Producto[] productos;
    private Cliente[] clientes;
    private Usuario[] usuarios;
    private Usuario currentUser;

    public StoreController() {
        // Inicializamos los arreglos vacíos (tamaño 0) para evitar NullPointerException
        this.historialOrdenes = new Orden[0];
        this.productos = new Producto[0];
        this.clientes = new Cliente[0];
        this.usuarios = new Usuario[0];
        this.currentUser = null;
    }

    public Orden[] getHistorial() {
        return this.historialOrdenes;
    }

    public static Producto[] getProductos() {
        return this.productos;
    }

    public Cliente[] getClientes() {
        return this.clientes;
    }

    public Usuario[] getUsuarios() {
        return this.usuarios;
    }

    // --- Métodos de add (Añadir) ---


    /*
        Crear una nueva orden, se necesita por parte del Usuario el tipo de documento del cliente, numero de documento del cliente y como va a pagar
        Se busca un cliente en el arreglo de clientes. Este Metodo puede devolver una excepción: EClienteNoEncontrado (Si no se encuentra el cliente)
        Se pone la fecha en formato dd/MM/yyyy que es con el que se manejaran todas las fechas del sistema
        Para esto se llama a la fecha actual y se formatea con el formato que se pasa
        Ahora, ya teniendo el cliente podemos crear una nueva orden con los parametros de su constructor. La orden tiene un estado de PENDIENTE por defecto.
        Añadimos la orden al atrreglo de historialOrdenes
        Añadimos la orden al arreglo de historialOrdenes del cliente
        Devolvemos la el UUID en caso de haber completado el flujo exitosamente (para así reutilizar este UUID en otras operaciones de la interfaz mientras estamos trabjando con ella), de lo contrario se habría lanzado una excepción
    */

    public UUID addOrden(TipoDocumento tipoDoc, String numDoc, MetodoPago metodoPago) throws EClienteNoEncontrado, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipoDoc");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("numDoc");
        if (metodoPago == null) throw new EParametroNulo("metodoPago");
        Cliente cliente = searchCliente(tipoDoc, numDoc);
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fecha = (LocalDate.now()).format(formateador);
        Orden o = new Orden(cliente, fecha, metodoPago);
        historialOrdenes = Arrays.copyOf(historialOrdenes, historialOrdenes.length + 1);
        historialOrdenes[historialOrdenes.length - 1] = o;
        cliente.AgregarCompras(o);
        return o.getIdPedido();
    }

    /*
    Añadir un item a una orden, recibimos el UUID, que deberíamos de tener en una variable temporal producida por la creación de la orden
    También el nombre del producto para así buscarlo en nuestro arreglo de productos
    Verificamos que la orden este en estado pendiente, pues de no ser así no podemos añadir productos
    Capturamos las posibles excepciones que puede producir añadir un item con
    */

    public void addItemToOrden(UUID idOrden, String producto, int cantidad) throws EOrdenNoEncontrada, EProductoNoEncontrado, EStockInsuficiente, EParametroNulo, ECantidadNegativa {
        if (idOrden == null) throw new EParametroNulo("idOrden");
        if (producto == null || producto.isBlank()) throw new EParametroNulo("producto");
        Orden o = searchOrden(idOrden);
        Producto p = searchProducto(producto);
        if (o.getEstado() != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden agregar items a órdenes pendientes");
        }
        OrdenItem oi = new OrdenItem(p, cantidad);
        o.addItemOrden(oi);
    }

    public void addCliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) throws EClienteYaExiste, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipoDoc");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("numDoc");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (existeCliente(tipoDoc, numDoc)) {
            throw new EClienteYaExiste(tipoDoc, numDoc);
        }
        Cliente nuevoCliente = new Cliente(tipoDoc, numDoc, nombre, apellido, email);
        this.clientes = Arrays.copyOf(this.clientes, this.clientes.length + 1);
        this.clientes[this.clientes.length - 1] = nuevoCliente;
    }

    public void addUsuario(String username, String password, Rol rol) throws EUsuarioYaExiste, EParametroNulo {
        if (username == null || username.isBlank()) throw new EParametroNulo("username");
        if (password == null) throw new EParametroNulo("password");
        if (rol == null) throw new EParametroNulo("rol");
        if (existeUsuario(username)) {
            throw new EUsuarioYaExiste(username);
        }
        Usuario nuevoUsuario = new Usuario(username, password, rol);
        this.usuarios = Arrays.copyOf(this.usuarios, this.usuarios.length + 1);
        this.usuarios[this.usuarios.length - 1] = nuevoUsuario;
    }

    public void addHardware(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante) throws EProductoYaExiste, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (existeProducto(nombre)) {
            throw new EProductoYaExiste(nombre);
        }
        Producto nuevoHardware = new Hardware(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, consumo, fabricante);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoHardware;
    }

    public void addVideojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String[] desarrolladores, String[] generos, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano) throws EProductoYaExiste, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (existeProducto(nombre)) {
            throw new EProductoYaExiste(nombre);
        }
        Producto nuevoVideojuego = new Videojuego(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, desarrolladores, generos, multijugador, fechaLanzamiento, plataforma, tamano);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoVideojuego;
    }

    //Para las ordenes


    // --- Métodos auxiliares de existencia ---
    //TODO: cambiar todo esto por whiles

    private boolean existeCliente(TipoDocumento tipoDoc, String numDoc) {
        int i = 0;
        while (i < this.clientes.length) {
            if(this.clientes[i].getNumDoc().equals(numDoc) && this.clientes[i].getTipoDoc().equals(tipoDoc)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeUsuario(String username) {
        int i = 0;
        while (i < this.usuarios.length) {
            if(this.usuarios[i].getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeProducto(String nombre) {
        int i = 0;
        while (i < this.productos.length) {
            if(this.productos[i].getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    // --- Métodos de Búsqueda (Search) ---

    public Orden searchOrden(UUID id) throws EOrdenNoEncontrada, EParametroNulo {
        if (id == null) throw new EParametroNulo("id");
        int i = 0;
        while (i < historialOrdenes.length) {
            if (historialOrdenes[i].getIdPedido().equals(id)) {
                return historialOrdenes[i];
            }
            i++;
        }
        throw new EOrdenNoEncontrada(id);
    }

    public Producto searchProducto(String nombre) throws EProductoNoEncontrado, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        int i = 0;
        while (i < productos.length) {
            if (productos[i].getNombre().equals(nombre)) {
                return productos[i];
            }
            i++;
        }
        throw new EProductoNoEncontrado(nombre);
    }

    public Cliente searchCliente(TipoDocumento tipoDoc, String numDoc) throws EClienteNoEncontrado, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipoDoc");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("numDoc");
        int i = 0;
        while (i < clientes.length) {
            if (clientes[i].getNumDoc().equals(numDoc) && clientes[i].getTipoDoc().equals(tipoDoc)) {
                return clientes[i];
            }
            i++;
        }
        throw new EClienteNoEncontrado(tipoDoc, numDoc);
    }

    public Usuario searchUsuario(String username) throws EUsuarioNoEncontrado, EParametroNulo {
        if (username == null || username.isBlank()) throw new EParametroNulo("username");
        int i = 0;
        while (i < usuarios.length) {
            if (usuarios[i].getUsername().equals(username)) {
                return usuarios[i];
            }
            i++;
        }
        throw new EUsuarioNoEncontrado(username);
    }

    // --- Métodos de Eliminación (Delete) ---

    public void deleteProducto(String nombre) throws EProductoNoEncontrado, EHistorialOrden, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        Producto p = searchProducto(nombre);
        int i = 0;


        while (i < historialOrdenes.length) {

            OrdenItem[] items = historialOrdenes[i].getItems();

            int j = 0; // 🔹 Se reinicia para cada orden

            while (items != null && j < items.length) {
                if (items[j].getProducto().getId().equals(p.getId())) {
                    throw new EHistorialOrden(
                        "No se puede eliminar el producto porque está asociado a una orden."
                    );
                }
                j++;
            }

            i++;
        }


        // 2. Creamos el nuevo arreglo
        Producto[] nuevoArreglo = new Producto[this.productos.length - 1];
        int j = 0;

        // 3. Copiamos all excepto el que queremos borrar
        for (Producto prod : this.productos) {
            if (!prod.getNombre().equals(nombre)) {
                nuevoArreglo[j] = prod;
                j++;
            }
        }
        this.productos = nuevoArreglo;
    }

    public void deleteCliente(TipoDocumento tipoDoc, String numDoc) throws EClienteNoEncontrado, EHistorialOrden, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipoDoc");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("numDoc");
        Cliente c = searchCliente(tipoDoc, numDoc);
        for (Orden ord : historialOrdenes) {
            if (ord.getCliente() != null && ord.getCliente().getId().equals(c.getId())) {
                throw new EHistorialOrden("No se puede eliminar el cliente porque tiene órdenes asociadas.");
            }
        }

        Cliente[] nuevoArreglo = new Cliente[this.clientes.length - 1];
        int j = 0;

        for (Cliente cli : this.clientes) {
            // Comparamos identificadores únicos
            if (!(cli.getTipoDoc().equals(tipoDoc) && cli.getNumDoc().equals(numDoc))) {
                nuevoArreglo[j] = cli;
                j++;
            }
        }
        this.clientes = nuevoArreglo;
    }

    public void deleteUsuario(String username) throws EUsuarioNoEncontrado, EParametroNulo {
        if (username == null || username.isBlank()) throw new EParametroNulo("username");
        Usuario u = searchUsuario(username);
        Usuario[] nuevoArreglo = new Usuario[this.usuarios.length - 1];
        int j = 0;

        for (Usuario user : this.usuarios) {
            if (!user.getUsername().equals(username)) {
                nuevoArreglo[j] = user;
                j++;
            }
        }
        this.usuarios = nuevoArreglo;
    }

    public void removeItemOrden(UUID idOrden, String nombre) throws EOrdenNoEncontrada, EProductoNoEncontrado, EValorNegativo, EParametroNulo {
        if (idOrden == null) throw new EParametroNulo("idOrden");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        Orden orden = searchOrden(idOrden);
        Producto producto = searchProducto(nombre);
        if (orden.getEstado() != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden quitar items de órdenes pendientes");
        }
        OrdenItem[] items = orden.getItems();
        if (items == null) {
            throw new IllegalStateException("La orden no tiene items.");
        }
        int index = -1;
        int i = 0;
        while (i < items.length) {
            if (items[i].getProducto().getId().equals(producto.getId())) {
                index = i;
                break;
            }
            i++;
        }
        if (index == -1) {
            throw new IllegalStateException("El producto '" + nombre + "' no está en la orden.");
        }
        OrdenItem removed = orden.removeItemAt(index);
        Producto p = removed.getProducto();
        p.setStock(p.getStock() + removed.getCantidad());
    }

    public void verificarPago(UUID idOrden, boolean pago) throws EOrdenNoEncontrada, EParametroNulo {
        if (idOrden == null) throw new EParametroNulo("idOrden");
        Orden orden = searchOrden(idOrden);
        if (pago) {
            if (orden.getItems() == null || orden.getItems().length == 0) {
                throw new IllegalStateException("No se puede aprobar una orden sin items");
            }
            orden.setEstado(Estado.APROBADO);
        } else {
            if (orden.getEstado() == Estado.PENDIENTE) {
                for (OrdenItem item : orden.getItems()) {
                    Producto p = item.getProducto();
                    p.setStock(p.getStock() + item.getCantidad());
                }
            }
            orden.setEstado(Estado.RECHAZADO);
        }
    }

    //TODO: Métodos de login y logout

    public Usuario getCurrentUser(){
        return currentUser;
    }

    //TODO: generar reportes
    //método que pase por el historial y con el uso de la fecha filtre

}