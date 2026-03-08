package com.nexus.controller;

import com.nexus.exceptions.*;
import com.nexus.model.entities.*;
import com.nexus.model.enums.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class StoreController {
    private Orden[] ordenes;
    private Producto[] productos;
    private Cliente[] clientes;
    private Usuario[] usuarios;
    private Usuario currentUser;

    public StoreController() {
        // Inicializamos los arreglos vacíos (tamaño 0) para evitar NullPointerException
        this.ordenes = new Orden[0];
        this.productos = new Producto[0];
        this.clientes = new Cliente[0];
        this.usuarios = new Usuario[0];
        this.currentUser = null;
    }

    public Orden[] getHistorial() {
        return this.ordenes;
    }

    public Cliente[] getClientes() {
        return this.clientes;
    }

    public Usuario[] getUsuarios() {
        return this.usuarios;
    }

    public Producto[] getProductos(){
        return this.productos;
    }

    // --- Métodos de add (Añadir) ---


    /*
        Crear una nueva orden, se necesita por parte del Usuario el tipo de documento del cliente, numero de documento del cliente y como va a pagar
        Se busca un cliente en el arreglo de clientes. Este Metodo puede devolver una excepción: EClienteNoEncontrado (Si no se encuentra el cliente)
        Se pone la fecha en formato dd/MM/yyyy que es con el que se manejaran todas las fechas del sistema
        Para esto se llama a la fecha actual y se formatea con el formato que se pasa
        Ahora, ya teniendo el cliente podemos crear una nueva orden con los parametros de su constructor. La orden tiene un estado de PENDIENTE por defecto.
        Añadimos la orden al atrreglo de ordenes
        Añadimos la orden al arreglo de ordenes del cliente
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
        ordenes = Arrays.copyOf(ordenes, ordenes.length + 1);
        ordenes[ordenes.length - 1] = o;

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

    public void addCliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) throws EClienteYaExiste, EParametroNulo, EFormatoInvalido {
        if (tipoDoc == null) throw new EParametroNulo("tipoDoc");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("numDoc");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if(apellido == null || apellido.isBlank()) throw new EParametroNulo("apellido");
        if(email == null || email.isBlank()) throw new EParametroNulo("email");
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

    public void addHardware (String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante) throws EProductoYaExiste, EParametroNulo, ECantidadNegativa, EValorNegativo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (existeProducto(nombre)) {
            throw new EProductoYaExiste(nombre);
        }
        Producto nuevoHardware = new Hardware(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, consumo, fabricante);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoHardware;
    }

    public void addVideojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String[] desarrolladores, String[] generos, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano) throws EProductoYaExiste, EParametroNulo, ECantidadNegativa, EValorNegativo {
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

    public boolean existeCliente(TipoDocumento tipoDoc, String numDoc) {
        int i = 0;
        while (i < this.clientes.length) {
            if(this.clientes[i].getNumDoc().equals(numDoc) && this.clientes[i].getTipoDoc().equals(tipoDoc)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean existeUsuario(String username) {
        int i = 0;
        while (i < this.usuarios.length) {
            if(this.usuarios[i].getUsername().equals(username)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean existeProducto(String nombre) {
        int i = 0;
        while (i < this.productos.length) {
            if(this.productos[i].getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
            i++;
        }
        return false;
    }

    // --- Métodos de Búsqueda (Search) ---

    /**
 * Busca una orden dentro del sistema usando su UUID.
 * El método recorre el arreglo de órdenes y compara
 * cada identificador con el recibido como parámetro.
 */
    public Orden searchOrden(UUID id) throws EOrdenNoEncontrada, EParametroNulo {
        if (id == null) throw new EParametroNulo("id");
        int i = 0;
        while (i < ordenes.length) {
            if (ordenes[i].getIdPedido().equals(id)) {
                return ordenes[i];
            }
            i++;
        }
        throw new EOrdenNoEncontrada(id);
    }

    public Producto searchProducto(String nombre) throws EProductoNoEncontrado, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        int i = 0;
        while (i < productos.length) {
            if (productos[i].getNombre().equalsIgnoreCase(nombre)) {
                return productos[i];
            }
            i++;
        }
        throw new EProductoNoEncontrado(nombre);
    }

    /** Obtiene un producto del arreglo real por su ID. Usado para decrementar stock correctamente. */
    public Producto getProductoById(UUID id) throws EProductoNoEncontrado, EParametroNulo {
        if (id == null) throw new EParametroNulo("id");
        int i = 0;
        while (i < productos.length) {
            Producto p = productos[i];
            if (p.getId().equals(id)) {
                return p;
            }
            i++;
        }
        throw new EProductoNoEncontrado("Producto con ID " + id + " no encontrado");
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
            if (usuarios[i].getUsername().equalsIgnoreCase(username)) {
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


        while (i < ordenes.length) {

            OrdenItem[] items = ordenes[i].getItems();

            int j = 0;

            while (items != null && j < items.length) {
                if (items[j] != null && items[j].getProducto() != null && items[j].getProducto().getId().equals(p.getId())) {
                    throw new EHistorialOrden("No se puede eliminar el producto porque está asociado a una orden.");
                }
                j++;
            }

            i++;
        }


        // 2. Creamos el nuevo arreglo
        Producto[] nuevoArreglo = new Producto[this.productos.length - 1];
        int j = 0;

        // 3. Copiamos todos excepto el que queremos borrar (por ID para consistencia con searchProducto)
        for (Producto prod : this.productos) {
            if (!prod.getId().equals(p.getId())) {
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

        // 1. Verificación de órdenes con while
        int i = 0;
        while (i < ordenes.length) {
            Orden ord = ordenes[i];
            if (ord.getCliente() != null && ord.getCliente().getId().equals(c.getId())) {
                throw new EHistorialOrden("No se puede eliminar el cliente porque tiene órdenes asociadas.");
            }
            i++;
        }

        // 2. Creación del nuevo arreglo
        Cliente[] nuevoArreglo = new Cliente[this.clientes.length - 1];
        int j = 0;
        int k = 0;

        while (k < this.clientes.length) {
            Cliente cli = this.clientes[k];

            // Si NO es el cliente que queremos borrar, lo copiamos
            if (!(cli.getTipoDoc().equals(tipoDoc) && cli.getNumDoc().equals(numDoc))) {
                // Verificación de seguridad para no exceder el tamaño del nuevoArreglo
                if (j < nuevoArreglo.length) {
                    nuevoArreglo[j] = cli;
                    j++;
                }
            }
            k++;
        }
        this.clientes = nuevoArreglo;
    }

    public void deleteUsuario(String username) throws EUsuarioNoEncontrado, EParametroNulo {
        if (username == null || username.isBlank()) {
            throw new EParametroNulo("username");
        }

        Usuario u = searchUsuario(username);

        Usuario[] nuevoArreglo = new Usuario[this.usuarios.length - 1];

        int i = 0;
        int j = 0;

        while (i < this.usuarios.length) {
            Usuario user = this.usuarios[i];
            if (!user.getUsername().equalsIgnoreCase(username)) {
                if (j < nuevoArreglo.length) {
                    nuevoArreglo[j] = user;
                    j++;
                }
            }
            i++;
        }

        this.usuarios = nuevoArreglo;
    }

    /**
 * Elimina un item de una orden existente.
 * Este método:
 * 1. Busca la orden mediante su UUID.
 * 2. Busca el producto dentro de la orden.
 * 3. Verifica que la orden esté en estado PENDIENTE.
 * 4. Elimina el item del arreglo de items.
 * 5. Restaura el stock del producto eliminado.
 * @param idOrden Identificador de la orden.
 * @param nombre Nombre del producto que se desea eliminar.
 */
    public void removeItemOrden(UUID idOrden, String nombre) throws EOrdenNoEncontrada, EProductoNoEncontrado, EParametroNulo {
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
        orden.removeItemAt(index);
    }

    /**
 * Verifica el pago de una orden y cambia su estado.
 * Si el pago es válido:
 *  La orden pasa al estado APROBADO.
 * Si el pago es rechazado:
 *  La orden pasa a estado RECHAZADO.
 *  Se restaura el stock de todos los productos que estaban en la orden.
 */

    public void verificarPago(UUID idOrden, double valorPagado) throws EOrdenNoEncontrada, EParametroNulo, EValorNegativo, EProductoNoEncontrado {
        if (idOrden == null) throw new EParametroNulo("idOrden");
        if (valorPagado < 0) throw new EValorNegativo("El valor pagado no puede ser negativo");
        Orden orden = searchOrden(idOrden);
        if (orden.getEstado() != Estado.PENDIENTE) {
            throw new IllegalStateException("La orden ya fue verificada (estado: " + orden.getEstado() + ")");
        }
        if (orden.getItems() == null || orden.getItems().length == 0) {
            throw new IllegalStateException("No se puede verificar pago de una orden sin items");
        }
        orden.calcularTotal();
        orden.setValorPagado(valorPagado);
        orden.cambioEstado();
        if (orden.getEstado() == Estado.APROBADO) {
            decrementarStockEnProductos(orden);
        }
    }

    /** Decrementa el stock en el arreglo real de productos para cada item de la orden aprobada. */
    private void decrementarStockEnProductos(Orden orden) throws EValorNegativo, EProductoNoEncontrado, EParametroNulo {
        OrdenItem[] items = orden.getItems();
        if (items == null) return;
        for (OrdenItem item : items) {
            Producto pEnOrden = item.getProducto();
            Producto pReal = getProductoById(pEnOrden.getId());
            if (pReal.getStock() < item.getCantidad()) {
                throw new EValorNegativo("No hay suficientes productos. Quedan: " + pReal.getStock() + " de " + pReal.getNombre());
            }
            pReal.setStock(pReal.getStock() - item.getCantidad());
        }
    }

    /**
     * Modifica la cantidad de un producto en una orden. La orden debe estar en estado PENDIENTE.
     */
    public String modificarCantidadItem(UUID idOrden, String nombreProducto, int cantidad) throws EOrdenNoEncontrada, EProductoNoEncontrado, EValorNegativo, ECantidadNegativa, EStockInsuficiente, EParametroNulo {
        if (idOrden == null) throw new EParametroNulo("idOrden");
        if (nombreProducto == null || nombreProducto.isBlank()) throw new EParametroNulo("nombreProducto");
        Orden orden = searchOrden(idOrden);
        if (orden.getEstado() != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se puede modificar cantidad en órdenes pendientes");
        }
        Producto producto = searchProducto(nombreProducto);
        if (cantidad <= 0) {
            throw new ECantidadNegativa("La cantidad debe ser positiva");
        }
        if (producto.getStock() < cantidad) {
            throw new EStockInsuficiente(producto);
        }
        OrdenItem[] items = orden.getItems();
        if (items == null) {
            throw new IllegalStateException("La orden no tiene items");
        }
        for (OrdenItem item : items) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(cantidad);
                return "Se cambió la cantidad correctamente";
            }
        }
        throw new IllegalStateException("El producto '" + nombreProducto + "' no está en la orden");
    }

    //TODO: Métodos de login y logout

    public Usuario getCurrentUser(){
        return currentUser;
    }

    //TODO: generar reportes
    //método que pase por el historial y con el uso de la fecha filtre

    public String[] cargarFicheros() {
        String[] archivosFallidos = new String[0];
        String pathFicheros = "src/com/ficheros/";
        File dir = new File(pathFicheros);

        this.clientes = new Cliente[0];
        this.productos = new Producto[0];
        this.usuarios = new Usuario[0];
        this.ordenes = new Orden[0];

        File[] ficheros = dir.listFiles();
        if (ficheros == null) {
            return archivosFallidos;
        }

        for (File f : ficheros) {
            if (f.isFile()){
                String nombreArchivo = f.getName();
                String extension = getExtension(nombreArchivo);
            try {
                switch (extension) {
                    case "clienteFile":
                        Cliente c = Cliente.leerCliente(f.getPath());
                        clientes = Arrays.copyOf(clientes, clientes.length + 1);
                        clientes[clientes.length - 1] = c;
                        break;
                    case "videojuegoFile":
                        Videojuego v = Videojuego.leerVideojuego(f.getPath());
                        productos = Arrays.copyOf(productos, productos.length + 1);
                        productos[productos.length - 1] = v;
                        break;
                    case "hardwareFile":
                        Hardware h = Hardware.leerHardware(f.getPath());
                        productos = Arrays.copyOf(productos, productos.length + 1);
                        productos[productos.length - 1] = h;
                        break;
                    case "usuarioFile":
                        Usuario u = Usuario.leerUsuario(f.getPath());
                        usuarios = Arrays.copyOf(usuarios, usuarios.length + 1);
                        usuarios[usuarios.length - 1] = u;
                        break;
                    case "ordenFile":
                        Orden o = Orden.leerOrden(f.getPath());
                        ordenes = Arrays.copyOf(ordenes, ordenes.length + 1);
                        ordenes[ordenes.length - 1] = o;
                        break;
                    default:
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                archivosFallidos[archivosFallidos.length - 1] = nombreArchivo + " (" + e.getMessage() + ")";
            }
            }
        }
        return archivosFallidos;
    }

    public String[] guardarFicheros() {
        String[] archivosFallidos = new String[0];
        String pathFicheros = "src/com/ficheros/";
        File dir = new File(pathFicheros);

        File[] ficherosExistentes = dir.listFiles();
        if (ficherosExistentes != null) {
            for (File f : ficherosExistentes) {
                if (f.isFile() && (f.getName().endsWith(".clienteFile") || f.getName().endsWith(".videojuegoFile")
                        || f.getName().endsWith(".hardwareFile") || f.getName().endsWith(".usuarioFile")
                        || f.getName().endsWith(".ordenFile"))) {
                    f.delete();
                }
            }
        }

        int i = 1;
        for (Cliente c : clientes) {
            try {
                c.escribirCliente(pathFicheros + "cliente" + i + ".clienteFile");
            } catch (IOException e) {
                archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                archivosFallidos[archivosFallidos.length - 1] = "cliente" + i + ": " + e.getMessage();
            }
            i++;
        }

        int j = 1;
        for (Producto p : productos) {
            try {
                if (p instanceof Videojuego v) {
                    v.escribirVideojuego(pathFicheros + "videojuego" + j + ".videojuegoFile");
                } else if (p instanceof Hardware h) {
                    h.escribirHardware(pathFicheros + "hardware" + j + ".hardwareFile");
                }
            } catch (IOException e) {
                archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                archivosFallidos[archivosFallidos.length - 1] = "producto" + j + ": " + e.getMessage();
            }
            j++;
        }

        int k = 1;
        for (Usuario u : usuarios) {
            try {
                u.escribirUsuario(pathFicheros + "usuario" + k + ".usuarioFile");
            } catch (IOException e) {
                archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                archivosFallidos[archivosFallidos.length - 1] = "usuario" + k + ": " + e.getMessage();
            }
            k++;
        }

        int ord = 1;
        for (Orden o : ordenes) {
            try {
                o.escribirOrden(pathFicheros + "orden" + ord + ".ordenFile");
            } catch (IOException e) {
                archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                archivosFallidos[archivosFallidos.length - 1] = "orden" + ord + ": " + e.getMessage();
            }
            ord++;
        }
        return archivosFallidos;
    }

    public String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
        return "";
    }
    return filename.substring(filename.lastIndexOf(".") + 1);
}

}