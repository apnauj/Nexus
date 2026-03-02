package com.nexus.controller;

import com.nexus.exceptions.*;
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

    public Producto[] getProductos() {
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
        Añadimos la orden al arreglo de historialOrdenes
        Añadimos la orden al arreglo de historialOrdenes del cliente
        Devolvemos la el UUID en caso de haber completado el flujo exitosamente (para así reutilizar este UUID en otras operaciones de la interfaz mientras estamos trabjando con ella), de lo contrario se habría lanzado una excepción
    */

    public UUID addOrden(TipoDocumento tipoDoc, String numDoc, MetodoPago metodoPago) {
        try {
            Cliente cliente = searchCliente(tipoDoc,numDoc);
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fecha = (LocalDate.now()).format(formateador);
            Orden o = new Orden(cliente, fecha, metodoPago);
            historialOrdenes = Arrays.copyOf(historialOrdenes, historialOrdenes.length + 1);
            historialOrdenes[historialOrdenes.length-1] = o;

            cliente.setHistorial(Arrays.copyOf(cliente.getHistorial(), cliente.getHistorial().length + 1));
            cliente.getHistorial()[cliente.getHistorial().length-1] = o;

            return o.getIdPedido();
        } catch (EClienteNoEncontrado e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
    Añadir un item a una orden, recibimos el UUID, que deberíamos de tener en una variable temporal producida por la creación de la orden
    También el nombre del producto para así buscarlo en nuestro arreglo de productos
    Verificamos que la orden este en estado pendiente, pues de no ser así no podemos añadir productos
    Capturamos las posibles excepciones que puede producir añadir un item con
    */

    public void addItemToOrden(UUID idOrden, String producto, int cantidad){
        try{
           Orden o = searchOrden(idOrden);
           Producto p = searchProducto(producto);

            if (o.getEstado() != Estado.Pendiente) {
                throw new IllegalStateException("Solo se pueden agregar items a órdenes pendientes");
            } else {
                OrdenItem oi = new OrdenItem(p, cantidad);
                o.addItemOrden(oi);
            }
        } catch (EOrdenNoEncontrada | EProductoNoEncontrado | EStockInsuficiente | ECantidadNegativa | IllegalStateException e){
            System.out.println(e.getMessage());
        }
    }

    public void addCliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) {
        try {
            // Intentamos buscarlo primero. Si lo encuentra, ¡no podemos agregarlo!
            searchCliente(tipoDoc, numDoc);
            System.out.println("Error: El cliente con documento " + numDoc + " ya existe.");
            return;
        } catch (EClienteNoEncontrado e) {
            // Si entra aquí, es porque NO existe. ¡Perfecto para agregar!
        }

        Cliente nuevoCliente = new Cliente(tipoDoc, numDoc, nombre, apellido, email);
        this.clientes = Arrays.copyOf(this.clientes, this.clientes.length + 1);
        this.clientes[this.clientes.length - 1] = nuevoCliente;
        System.out.println("Cliente agregado exitosamente.");
    }

    public void addUsuario(String username, String password, Rol rol) {
        try {
            searchUsuario(username);
            System.out.println("Error: El nombre de usuario '" + username + "' ya está ocupado.");
            return;
        } catch (EUsuarioNoEncontrado e) {
            // Si no existe, procedemos
        }

        Usuario nuevoUsuario = new Usuario(username, password, rol);
        this.usuarios = Arrays.copyOf(this.usuarios, this.usuarios.length + 1);
        this.usuarios[this.usuarios.length - 1] = nuevoUsuario;
        System.out.println("Usuario agregado exitosamente.");
    }

    public void addHardware(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante) {
        try {
            searchProducto(nombre);
            System.out.println("Error: Ya existe un producto llamado '" + nombre + "'.");
            return;
        } catch (EProductoNoEncontrado e) {
            // Si no existe, procedemos
        }

        Producto nuevoHardware = new Hardware(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, consumo, fabricante);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoHardware;
        System.out.println("Hardware agregado exitosamente.");
    }

    // Para Videojuego la lógica es idéntica
    public void addVideojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String[] desarrolladores, String[] generos, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano) {
        try {
            searchProducto(nombre);
            System.out.println("Error: Ya existe un producto llamado '" + nombre + "'.");
            return;
        } catch (EProductoNoEncontrado e) {
            // Si no existe, procedemos
        }

        Producto nuevoVideojuego = new Videojuego(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, desarrolladores, generos, multijugador, fechaLanzamiento, plataforma, tamano);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoVideojuego;
        System.out.println("Videojuego agregado exitosamente.");
    }



    // --- Métodos de Búsqueda (Search) ---

    public Orden searchOrden(UUID id) throws EOrdenNoEncontrada {
        int i = 0;
        while (i < historialOrdenes.length) {
            if (historialOrdenes[i].getIdPedido().equals(id)) {
                return historialOrdenes[i];
            }
            i++;
        }
        throw new EOrdenNoEncontrada(id);
    }

    public Producto searchProducto(String nombre) throws EProductoNoEncontrado{
        int i = 0;
        while (i < productos.length) {
            if (productos[i].getNombre().equals(nombre)) {
                return productos[i];
            }
            i++;
        }
        throw new EProductoNoEncontrado(nombre);
    }

    public Cliente searchCliente(TipoDocumento tipoDoc, String numDoc) throws EClienteNoEncontrado {
        int i = 0;
        while (i < clientes.length) {
            if (clientes[i].getNumDoc().equals(numDoc) && clientes[i].getTipoDoc().equals(tipoDoc)) {
                return clientes[i];
            }
            i++;
        }
        throw new EClienteNoEncontrado(tipoDoc, numDoc);
    }

    public Usuario searchUsuario(String username) throws EUsuarioNoEncontrado {
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

    public void deleteProducto(String nombre) {
        // 1. Validamos que exista (esto lanza la excepción si no lo encuentra)
        Producto p = searchProducto(nombre);

        // 2. Creamos el nuevo arreglo
        Producto[] nuevoArreglo = new Producto[this.productos.length - 1];
        int j = 0;

        // 3. Copiamos all excepto el que queremos borrar
        for (Producto prod : this.productos) {
            if (!prod.getNombre().equals(nombre)) {
                nuevoArreglo[j++] = prod;
            }
        }
        this.productos = nuevoArreglo;
        System.out.println("Producto eliminado correctamente.");
    }

    public void deleteCliente(TipoDocumento tipoDoc, String numDoc) {
        Cliente c = searchCliente(tipoDoc, numDoc);

        Cliente[] nuevoArreglo = new Cliente[this.clientes.length - 1];
        int j = 0;

        for (Cliente cli : this.clientes) {
            // Comparamos identificadores únicos
            if (!(cli.getTipoDoc().equals(tipoDoc) && cli.getNumDoc().equals(numDoc))) {
                nuevoArreglo[j++] = cli;
            }
        }
        this.clientes = nuevoArreglo;
        System.out.println("Cliente eliminado correctamente.");
    }

    public void deleteUsuario(String username) {
        Usuario u = searchUsuario(username);

        Usuario[] nuevoArreglo = new Usuario[this.usuarios.length - 1];
        int j = 0;

        for (Usuario user : this.usuarios) {
            if (!user.getUsername().equals(username)) {
                nuevoArreglo[j++] = user;
            }
        }
        this.usuarios = nuevoArreglo;
        System.out.println("Usuario eliminado correctamente.");
    }

    public void removeItemOrden(UUID idOrden, String nombre) {
        try {
            Orden orden = searchOrden(idOrden);
            Producto producto = searchProducto(nombre);

            if (orden.getEstado() != Estado.Pendiente) {
                throw new IllegalStateException("Solo se pueden quitar items de órdenes pendientes");
            }

            OrdenItem[] items = orden.getItems();
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
                System.out.println("El producto '" + nombre + "' no está en la orden.");
                return;
            }

            OrdenItem removed = orden.removeItemAt(index);
            Producto p = removed.getProducto();
            p.setStock(p.getStock() + removed.getCantidad());
            System.out.println("Item eliminado correctamente. Stock restaurado.");
        } catch (EOrdenNoEncontrada | EProductoNoEncontrado | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    //Méto do para validar orden
    public void verificarPago(UUID idOrden, boolean pago){
        try {
            Orden orden =  searchOrden(idOrden);
            if (pago) {
                if (orden.getItems() == null || orden.getItems().length == 0) {
                    throw new IllegalStateException("No se puede aprobar una orden sin items");
                }
                orden.setEstado(Estado.Aprobado);
            } else {

                if (orden.getEstado() == Estado.Pendiente) {
                    for (OrdenItem item : orden.getItems()) {
                        Producto p = item.getProducto();
                        p.setStock(p.getStock() + item.getCantidad());
                    }
                }
                orden.setEstado(Estado.Rechazado);
            }
        } catch (EOrdenNoEncontrada | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    //Métodos de login

    public Usuario getCurrentUser(){
        return currentUser;
    }

}