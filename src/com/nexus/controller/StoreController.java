package com.nexus.controller;

import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EUsuarioNoEncontrado;
import com.nexus.model.entities.*;
import com.nexus.model.entities.TipoDocumento;
import com.nexus.model.enums.*;
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

    public Orden searchOrder(UUID id) throws EOrdenNoEncontrada {
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

}