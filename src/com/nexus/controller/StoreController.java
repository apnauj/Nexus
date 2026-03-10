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
    private static StoreController instance;
    private Orden[] ordenes;
    private Producto[] productos;
    private Cliente[] clientes;
    private Usuario[] usuarios;
    private Usuario currentUser;

    //Constructor
    private StoreController() {
        // Inicializamos los arreglos vacíos (tamaño 0) para evitar NullPointerException
        this.ordenes = new Orden[0];
        this.productos = new Producto[0];
        this.clientes = new Cliente[0];
        this.usuarios = new Usuario[0];
        this.currentUser = null;
    }

    /**
     * Obtiene la única instancia del controlador.
     * Carga los ficheros y crea el admin por defecto si no hay usuarios.
     * synchronized lo único que hace es asegurarse de que solo se pueda obtener una instancia al tiempo
     * de manera que sea imposible que dos métodos lleguen al mismo tiempo y se creen 2 instancias
     */
    public static synchronized StoreController getInstance() {
        //Si no se ha creado se crea el storeController y se inicializa
        if (instance == null) {
            instance = new StoreController();
            instance.inicializar();
        }
        //Si no, devolvemos el storeController actual
        return instance;
    }

    /**
    *Inicialización de los ficheros, verificación de que haya un usuario Admin
    */
    private void inicializar() {
        //Carga los ficheros desde el directorio
        cargarFicheros();
        //Se asegura de que haya admin, si no es así lo crea
        asegurarAdminExiste();
    }

    /**
     * Garantiza que exista un usuario admin para poder acceder al sistema.
     * Se crea si no hay usuarios o si ninguno tiene username "admin".
     */
    private void asegurarAdminExiste() {
        //Si no hay usuarios crea el admin por defecto
        if (usuarios.length == 0) {
            crearAdminPorDefecto();
            return;
        }
        //Si no hay ningún usuario que se llame admin crea el admin por defecto
        int i = 0;
        while (i < usuarios.length && !"admin".equalsIgnoreCase(usuarios[i].getUsername())) {
            i++;
        }

        if (i == usuarios.length) crearAdminPorDefecto();

    }

    /**
     *  Crea el admin por defecto */
    private void crearAdminPorDefecto() {
        try {
            addUsuario("admin", "Admin123", Rol.ADMIN);
        } catch (EUsuarioYaExiste | EParametroNulo | EFormatoInvalido e) {
            // No debería ocurrir si verificamos antes
        }
    }

    //Sets
    public void setCurrentUser(Usuario user) {
        this.currentUser = user;
    }

    //Gets
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
    public Usuario getCurrentUser(){
        return currentUser;
    }

    //Logout
    public void logout() {
        this.currentUser = null;
    }

    // --- Métodos de add (Añadir) ---
    /**
     * Crear una nueva orden, se necesita por parte del Usuario el tipo de documento del cliente, numero de documento del cliente y como va a pagar
     * Se busca un cliente en el arreglo de clientes. Este Metodo puede devolver una excepción: EClienteNoEncontrado (Si no se encuentra el cliente)
     * Se pone la fecha en formato dd/MM/yyyy que es con el que se manejaran todas las fechas del sistema
     * Para esto se llama a la fecha actual y se formatea con el formato que se pasa
     * Ahora, ya teniendo el cliente podemos crear una nueva orden con los parametros de su constructor. La orden tiene un estado de PENDIENTE por defecto.
     * Añadimos la orden al atrreglo de ordenes
     * Añadimos la orden al arreglo de ordenes del cliente
     * Devolvemos la el UUID en caso de haber completado el flujo exitosamente (para así reutilizar este UUID en otras operaciones de la interfaz mientras estamos trabjando con ella), de lo contrario se habría lanzado una excepción
    */
    public UUID addOrden(TipoDocumento tipoDoc, String numDoc, MetodoPago metodoPago) throws EClienteNoEncontrado, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipo de documento");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("número de documento");
        if (metodoPago == null) throw new EParametroNulo("método de pago");

        Cliente cliente = searchCliente(tipoDoc, numDoc);
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fecha = (LocalDate.now()).format(formateador);
        Orden o = new Orden(cliente, fecha, metodoPago);
        ordenes = Arrays.copyOf(ordenes, ordenes.length + 1);
        ordenes[ordenes.length - 1] = o;

        cliente.agregarCompras(o);
        return o.getIdPedido();
    }

    /**
     * Añadir un item a una orden, recibimos el UUID, que deberíamos de tener en una variable temporal producida por la creación de la orden
     * También el nombre del producto para así buscarlo en nuestro arreglo de productos
     * Verificamos que la orden este en estado pendiente, pues de no ser así no podemos añadir productos
     * Capturamos las posibles excepciones que puede producir añadir un item con
    */
    public void addItemToOrden(UUID idOrden, String producto, int cantidad) throws EOrdenNoEncontrada, EProductoNoEncontrado, EStockInsuficiente, EParametroNulo, ECantidadNegativa, EEstadoOrdenInvalido, EValorNegativo {
        if (idOrden == null) throw new EParametroNulo("ID de la orden");
        if (producto == null || producto.isBlank()) throw new EParametroNulo("producto");
        if(cantidad <= 0) throw new EValorNegativo("La cantidad a añadir no puede ser 0 ni negativa");
        Orden o = searchOrden(idOrden);
        Producto p = searchProducto(producto);
        //Validación de que el producto no este ya en la orden, pues en ese caso lo que se debe hacer es modificar la cantidad
        int i = 0;
        while(i < o.getItems().length){
            if(o.getItems()[i].getProducto().getId().equals(p.getId())){
                throw new EProductoRepeteido("El producto que intenta agregar ya está en la oren, por favor modifique su cantidad");
            }
            i++;
        }
        if (o.getEstado() != Estado.PENDIENTE) {
            throw new EEstadoOrdenInvalido("Solo se pueden agregar items a órdenes pendientes");
        }
        OrdenItem oi = new OrdenItem(p, cantidad);
        o.addItemOrden(oi);
        o.calcularTotal();
    }

    /**
     * Añadir un cliente con sus respectivas validaciones
     */
    public void addCliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) throws EClienteYaExiste, EParametroNulo, EFormatoInvalido {
        if (tipoDoc == null) throw new EParametroNulo("tipo de documento");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("número de documento");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (apellido == null || apellido.isBlank()) throw new EParametroNulo("apellido");
        if (email == null || email.isBlank()) throw new EParametroNulo("email");
        if (existeCliente(tipoDoc, numDoc)) throw new EClienteYaExiste(tipoDoc, numDoc);

        String emailTrimmed = email.trim();
        if (emailTrimmed.contains(" ") || emailTrimmed.contains("\t")) throw new EFormatoInvalido("El email no puede contener espacios.");
        /**
         * Validación de formato de email mediante una expresión regular (Regex).
         * * Explicación del patrón "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$":
         * * 1. ^          : Indica el inicio de la cadena. Asegura que no haya texto previo.
         * 2. [^\\s@]+   : (Local-part) Coincide con uno o más caracteres que NO sean espacios (\s) ni arrobas (@).
         * 3. @          : Obliga la presencia del símbolo arroba literal.
         * 4. [^\\s@]+   : (Domain) Coincide con uno o más caracteres que NO sean espacios ni arrobas.
         * 5. \\.        : Obliga la presencia de un punto literal (se escapa con \\ porque el punto en regex significa "cualquier carácter").
         * 6. [^\\s@]+   : (TLD/Ext) Coincide con uno o más caracteres finales (la extensión) que no sean espacios ni arrobas.
         * 7. $          : Indica el fin de la cadena. Asegura que no haya texto extra después del dominio.
         */
        if (!emailTrimmed.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new EFormatoInvalido("El formato del email es inválido. Debe ser: nombre@dominio.ext (ej: usuario@correo.com)");
        Cliente nuevoCliente = new Cliente(tipoDoc, numDoc, nombre, apellido, emailTrimmed);
        this.clientes = Arrays.copyOf(this.clientes, this.clientes.length + 1);
        this.clientes[this.clientes.length - 1] = nuevoCliente;
    }

    /**
     * Añadir un usuario con sus respectivas validaciones
     */
    public void addUsuario(String username, String password, Rol rol) throws EUsuarioYaExiste, EParametroNulo, EFormatoInvalido {
        if (username == null || username.isBlank()) throw new EParametroNulo("nombre de usuario");
        if (password == null) throw new EParametroNulo("contraseña");
        if (rol == null) throw new EParametroNulo("rol");
        if (existeUsuario(username)) throw new EUsuarioYaExiste(username);

        Usuario nuevoUsuario = new Usuario(username, password, rol);
        this.usuarios = Arrays.copyOf(this.usuarios, this.usuarios.length + 1);
        this.usuarios[this.usuarios.length - 1] = nuevoUsuario;
    }

    /**
     * Añade un producto tipo Hardware con sus respectivas validaciones.
     * El descuento se asigna automáticamente según stock y consumo.
     *
     * @return el producto creado (para obtener el descuento aplicado)
     */
    public Producto addHardware(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante, boolean descuentoActivo) throws EProductoYaExiste, EParametroNulo, ECantidadNegativa, EValorNegativo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
        if (precioBase < 0) throw new EValorNegativo("El precio base no puede ser negativo, usted registro: "+precioBase);
        if (stock < 0) throw new EValorNegativo("El stock no puede ser negativo, el stock registrado fue: " + stock);
        if (tiempoGarantia <0) throw new EValorNegativo("El tiempo de garantia no puede ser negativo, el valor registrado del tiempo de garantia es: "+tiempoGarantia);
        if (descripcion == null || descripcion.isBlank()) throw new EParametroNulo("descripción");
        if(consumo<0) throw new ECantidadNegativa("El consumo debe ser un número entero positivo");
        if (fabricante == null || fabricante.isBlank()) throw new EParametroNulo("fabricante");
        if (existeProducto(nombre)) throw new EProductoYaExiste(nombre);

        Hardware nuevoHardware = new Hardware(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, consumo, fabricante, descuentoActivo);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoHardware;
        return nuevoHardware;
    }

    /**
     * Añade un producto tipo Videojuego con sus respectivas validaciones.
     * El descuento se asigna automáticamente según stock y antigüedad.
     *
     * devuelve el producto creado (para obtener el descuento aplicado)
     */
    public Producto addVideojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String desarrollador, String genero, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano, boolean descuentoActivo) throws EProductoYaExiste, EParametroNulo, ECantidadNegativa, EValorNegativo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
        if (precioBase < 0) throw new EValorNegativo("El precio base no puede ser negativo, usted registro: "+precioBase);
        if (stock < 0) throw new EValorNegativo("El stock no puede ser negativo, el stock registrado fue: " + stock);
        if (tiempoGarantia <0) throw new EValorNegativo("El tiempo de garantia no puede ser negativo, el valor registrado del tiempo de garantia es: "+tiempoGarantia);
        if (descripcion == null || descripcion.isBlank()) throw new EParametroNulo("descripción");
        if (desarrollador == null || desarrollador.isBlank()) throw new EParametroNulo("desarrollador");
        if (genero == null || genero.isBlank()) throw new EParametroNulo("genero");
        if (plataforma == null || plataforma.isBlank()) throw new EParametroNulo("plataforma");
        if (tamano < 0) throw new ECantidadNegativa("El peso en GB del videojuego no puede ser negativo");
        if (existeProducto(nombre)) throw new EProductoYaExiste(nombre);

        Videojuego nuevoVideojuego = new Videojuego(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, desarrollador, genero, multijugador, fechaLanzamiento, plataforma, tamano, descuentoActivo);
        this.productos = Arrays.copyOf(this.productos, this.productos.length + 1);
        this.productos[this.productos.length - 1] = nuevoVideojuego;
        return nuevoVideojuego;
    }

    // --- Métodos auxiliares de existencia ---
    //Verifica si existe un cliente
    public boolean existeCliente(TipoDocumento tipoDoc, String numDoc) {
        if (tipoDoc == null && (numDoc == null || numDoc.isBlank())) return false;
        int i = 0;
        while (i < this.clientes.length) {
            if(this.clientes[i].getNumDoc().equals(numDoc) && this.clientes[i].getTipoDoc().equals(tipoDoc)) {
                return true;
            }
            i++;
        }
        return false;
    }
    //Verifica si existe un usuario
    public boolean existeUsuario(String username) {
        if(username == null || username.isBlank()) return false;
        int i = 0;
        while (i < this.usuarios.length) {
            if(this.usuarios[i].getUsername().equals(username)) {
                return true;
            }
            i++;
        }
        return false;
    }
    //Verifica si existe un producto
    public boolean existeProducto(String nombre) {
        if (nombre == null || nombre.isBlank()) return false;
        int i = 0;
        while (i < this.productos.length) {
            String n = this.productos[i].getNombre();
            if (n != null && n.equalsIgnoreCase(nombre)) {
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
        if (id == null) throw new EParametroNulo("ID de la orden");
        int i = 0;
        while (i < ordenes.length) {
            if (ordenes[i].getIdPedido().equals(id)) {
                return ordenes[i];
            }
            i++;
        }
        throw new EOrdenNoEncontrada(id);
    }

    /**
    * Busca un producto haciendo las respectivas validaciones
    * */
    public Producto searchProducto(String nombre) throws EProductoNoEncontrado {
        int i = 0;
        while (i < productos.length) {
            String n = productos[i].getNombre();
            if (n != null && n.equalsIgnoreCase(nombre)) {
                return productos[i];
            }
            i++;
        }
        throw new EProductoNoEncontrado(nombre);
    }

    /**
     * Busca un cliente con sus respectivas validaciones
     */
    public Cliente searchCliente(TipoDocumento tipoDoc, String numDoc) throws EClienteNoEncontrado, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipo de documento");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("número de documento");
        int i = 0;
        while (i < clientes.length) {
            if (clientes[i].getNumDoc().equals(numDoc) && clientes[i].getTipoDoc().equals(tipoDoc)) {
                return clientes[i];
            }
            i++;
        }
        throw new EClienteNoEncontrado(tipoDoc, numDoc);
    }

    /**
     * Busca un usuario con sus respectivas validaciones
     */
    public Usuario searchUsuario(String username) throws EUsuarioNoEncontrado, EParametroNulo {
        if (username == null || username.isBlank()) throw new EParametroNulo("nombre de usuario");
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

    /**
     * Elimina un producto con sus respectivas validaciones
     */
    public void deleteProducto(String nombre) throws EProductoNoEncontrado, EHistorialOrden, EParametroNulo {
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        Producto p = searchProducto(nombre);

        int i = 0;
        while (i < ordenes.length) {

            OrdenItem[] items = ordenes[i].getItems();

            //Orden --> items --> productos

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

    /**
     * Elimina un cliente con sus respectivas validaciones
     */
    public void deleteCliente(TipoDocumento tipoDoc, String numDoc) throws EClienteNoEncontrado, EHistorialOrden, EParametroNulo {
        if (tipoDoc == null) throw new EParametroNulo("tipo de documento");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("número de documento");

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

    /**
     * Elimina un usuario con sus respectivas validaciones
     */
    public void deleteUsuario(String username) throws EUsuarioNoEncontrado, EParametroNulo {
        if (username == null || username.isBlank()) {
            throw new EParametroNulo("nombre de usuario");
        }
        //Verificar que el usuario que se va a borrar exista
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
 */
    public void removeItemOrden(UUID idOrden, String nombre) throws EOrdenNoEncontrada, EProductoNoEncontrado, EParametroNulo, EEstadoOrdenInvalido {
        if (idOrden == null) throw new EParametroNulo("ID de la orden");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        Orden orden = searchOrden(idOrden);
        Producto producto = searchProducto(nombre);
        if (orden.getEstado() != Estado.PENDIENTE) {
            throw new EEstadoOrdenInvalido("Solo se pueden quitar items de órdenes pendientes");
        }
        OrdenItem[] items = orden.getItems();
        if (items == null) {
            throw new EEstadoOrdenInvalido("La orden no tiene items.");
        }
        int index = -1;
        int i = 0;
        while (i < items.length && index == -1) {
            if (items[i] != null && items[i].getProducto() != null && items[i].getProducto().getId().equals(producto.getId())) {
                index = i;
            }
            i++;
        }
        if (index == -1) {
            throw new EProductoNoEncontrado("El producto '" + nombre + "' no está en la orden.");
        }
        orden.removeItemAt(index);
        orden.calcularTotal();
    }

    /**
     * Verifica el pago de una orden y cambia su estado.
     * Si el pago es válido: la orden pasa al estado APROBADO y se decrementa el stock.
     * Si el pago es rechazado: la orden pasa a estado RECHAZADO.
     */
    public void verificarPago(UUID idOrden, double valorPagado) throws EOrdenNoEncontrada, EParametroNulo, EValorNegativo, EEstadoOrdenInvalido, EStockInsuficiente {
        if (idOrden == null) throw new EParametroNulo("ID de la orden");
        if (valorPagado < 0) throw new EValorNegativo("El valor pagado no puede ser negativo");
        Orden orden = searchOrden(idOrden);
        if (orden.getEstado() != Estado.PENDIENTE) throw new EEstadoOrdenInvalido("La orden ya fue verificada (estado: " + orden.getEstado() + ")");
        if (orden.getItems() == null || orden.getItems().length == 0) throw new EEstadoOrdenInvalido("No se puede verificar pago de una orden sin items");

        orden.calcularTotal();
        orden.setValorPagado(valorPagado);
        orden.cambioEstado();
        if (orden.getEstado() == Estado.APROBADO) {
            decrementarStockEnProductos(orden);
        }
    }

    /**
     * Decrementa el stock de cada producto de la orden aprobada.
     * El producto en la orden es la misma referencia que el del inventario, por lo que
     * al modificarlo se actualiza el stock real. Recalcula el descuento tras el cambio.
     */
    private void decrementarStockEnProductos(Orden orden) throws EValorNegativo, EStockInsuficiente {
        OrdenItem[] items = orden.getItems();
        if (items == null) return;
        for (OrdenItem item : items) {
            Producto p = item.getProducto();
            if (p.getStock() < item.getCantidad()) {
                throw new EStockInsuficiente(p);
            }
            p.setStock(p.getStock() - item.getCantidad());
            if (p.isDescuentoActivo()) {
                p.asignarDescuento();
            }
        }
    }

    /**
     * Modifica la cantidad de un producto en una orden. La orden debe estar en estado PENDIENTE.
     */
    public void modificarCantidadItem(UUID idOrden, String nombreProducto, int cantidad) throws EOrdenNoEncontrada, EProductoNoEncontrado, EEstadoOrdenInvalido, ECantidadNegativa, EStockInsuficiente, EParametroNulo {
        if (idOrden == null) throw new EParametroNulo("ID de la orden");
        if (nombreProducto == null || nombreProducto.isBlank()) throw new EParametroNulo("nombre del producto");
        Orden orden = searchOrden(idOrden);
        if (orden.getEstado() != Estado.PENDIENTE) {
            throw new EEstadoOrdenInvalido("Solo se puede modificar cantidad en órdenes pendientes");
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
            throw new EProductoNoEncontrado("La orden no tiene items");
        }

        int i = 0;
        while (i < orden.getItems().length && !items[i].getProducto().getId().equals(producto.getId())) {
            i++;
        }
        if(i == orden.getItems().length){
            throw new EProductoNoEncontrado("El producto '" + nombreProducto + "' no está en la orden");
        } else {
            items[i].setCantidad(cantidad);
        }
    }

    // --- Metodos de modificacion (Update) ---

    /**
     * Actualiza los datos de un cliente existente.
     * Requiere todos los campos (mismas validaciones que el constructor de Cliente).
     */
    public void actualizarCliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email)
            throws EClienteNoEncontrado, EParametroNulo, EFormatoInvalido {

        if (tipoDoc == null) throw new EParametroNulo("tipo de documento");
        if (numDoc == null || numDoc.isBlank()) throw new EParametroNulo("número de documento");
        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre");
        if (apellido == null || apellido.isBlank()) throw new EParametroNulo("apellido");
        if (email == null || email.isBlank()) throw new EParametroNulo("email");
        if (!numDoc.trim().matches("\\d{1,10}")) throw new EFormatoInvalido("El documento debe ser numérico y tener máximo 10 dígitos.");
        String emailTrimmed = email.trim();
        if (emailTrimmed.contains(" ") || emailTrimmed.contains("\t")) throw new EFormatoInvalido("El email no puede contener espacios.");
        if (!emailTrimmed.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new EFormatoInvalido("El formato del email es inválido. Debe ser: nombre@dominio.ext (ej: usuario@correo.com)");

        Cliente c = searchCliente(tipoDoc, numDoc.trim());
        c.setNombre(nombre.trim());
        c.setApellido(apellido.trim());
        c.setEmail(emailTrimmed);
    }
    	
    /**
     * Actualiza un producto tipo Hardware.
     * Aplica las mismas validaciones que el constructor de Hardware.
     */
    public void actualizarHardware(String nombre, String descripcion, String categoria,
            int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante, boolean descuentoActivo)
            throws EProductoNoEncontrado, EParametroNulo, ECantidadNegativa, EValorNegativo {

        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
        if (tiempoGarantia < 1) throw new EValorNegativo("El tiempo de garantía debe ser al menos 1 mes. Valor ingresado: " + tiempoGarantia);
        if (precioBase <= 0) throw new EValorNegativo("El precio base debe ser mayor que 0. Valor ingresado: " + precioBase);
        if (stock < 0) throw new EValorNegativo("El stock no puede ser negativo. Valor ingresado: " + stock);
        if (consumo <= 0) throw new ECantidadNegativa("El consumo debe ser un número entero positivo. Valor ingresado: " + consumo);
        if (fabricante == null || fabricante.isBlank()) throw new EParametroNulo("fabricante");

        Producto p = searchProducto(nombre);
        if (!(p instanceof Hardware hardware)) {
            throw new EProductoNoEncontrado("El producto '" + nombre + "' no es un Hardware.");
        }
        hardware.setDescripcion(descripcion != null ? descripcion : "");
        hardware.setCategoria(categoria);
        hardware.setPrecioBase(precioBase);
        hardware.setStock(stock);
        hardware.setTiempoGarantia(tiempoGarantia);
        hardware.setConsumo(consumo);
        hardware.setFabricante(fabricante);
        if (descuentoActivo) {
            hardware.asignarDescuento();
        }
    }

    /**
     * Actualiza un producto tipo Videojuego.
     * Aplica las mismas validaciones que el constructor de Videojuego.
     */
    public void actualizarVideojuego(String nombre, String descripcion, String categoria,
            int tiempoGarantia, double precioBase, int stock, String desarrollador, String genero,
            boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano, boolean descuentoActivo)
            throws EProductoNoEncontrado, EParametroNulo, ECantidadNegativa, EValorNegativo {

        if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
        if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
        if (tiempoGarantia < 1) throw new EValorNegativo("El tiempo de garantía debe ser al menos 1 mes. Valor ingresado: " + tiempoGarantia);
        if (precioBase <= 0) throw new EValorNegativo("El precio base debe ser mayor que 0. Valor ingresado: " + precioBase);
        if (stock < 0) throw new EValorNegativo("El stock no puede ser negativo. Valor ingresado: " + stock);
        if (desarrollador == null || desarrollador.isBlank()) throw new EParametroNulo("desarrollador");
        if (genero == null || genero.isBlank()) throw new EParametroNulo("género");
        if (plataforma == null || plataforma.isBlank()) throw new EParametroNulo("plataforma");
        if (tamano < 0) throw new ECantidadNegativa("El tamaño en GB no puede ser negativo. Valor ingresado: " + tamano);

        Producto p = searchProducto(nombre);
        if (!(p instanceof Videojuego videojuego)) {
            throw new EProductoNoEncontrado("El producto '" + nombre + "' no es un Videojuego.");
        }
        videojuego.setDescripcion(descripcion != null ? descripcion : "");
        videojuego.setCategoria(categoria);
        videojuego.setPrecioBase(precioBase);
        videojuego.setStock(stock);
        videojuego.setTiempoGarantia(tiempoGarantia);
        videojuego.setDesarrollador(desarrollador);
        videojuego.setGenero(genero);
        videojuego.setModoMultijugador(multijugador);
        videojuego.setFechaLanzamiento(fechaLanzamiento);
        videojuego.setPlataforma(plataforma);
        videojuego.setTamano(tamano);
        if (descuentoActivo) {
            videojuego.asignarDescuento();
        }
    }

    // --- Métodos para cargar y guardar los ficheros ---
    public String[] cargarFicheros() {
        String[] archivosFallidos = new String[0];
        String pathFicheros = "src/com/ficheros/";
        File dir = new File(pathFicheros);
        if (!dir.exists()) {
            dir.mkdirs();
        }

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
                String nombreArchivo = f.getName(); // --> "cliente1.clienteFile"
                String extension = getExtension(nombreArchivo);// --> "clienteFile"
            try {
                switch (extension) {
                    case "clienteFile":
                        Cliente c = Cliente.leerCliente(f.getPath());
                        clientes = Arrays.copyOf(clientes, clientes.length + 1);
                        clientes[clientes.length - 1] = c;
                        break;
                    case "videojuegoFile":
                        Videojuego v = Videojuego.leerVideojuego(f.getPath());
                        if (v.getNombre() != null && !v.getNombre().isBlank()) {
                            productos = Arrays.copyOf(productos, productos.length + 1);
                            productos[productos.length - 1] = v;
                        } else {
                            archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                            archivosFallidos[archivosFallidos.length - 1] = nombreArchivo + " (producto corrupto: nombre nulo)";
                        }
                        break;
                    case "hardwareFile":
                        Hardware h = Hardware.leerHardware(f.getPath());
                        if (h.getNombre() != null && !h.getNombre().isBlank()) {
                            productos = Arrays.copyOf(productos, productos.length + 1);
                            productos[productos.length - 1] = h;
                        } else {
                            archivosFallidos = Arrays.copyOf(archivosFallidos, archivosFallidos.length + 1);
                            archivosFallidos[archivosFallidos.length - 1] = nombreArchivo + " (producto corrupto: nombre nulo)";
                        }
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
        if (!dir.exists()) {
            dir.mkdirs();
        }

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
    return filename.substring(filename.lastIndexOf(".") + 1); //cliente1(.)clienteFile
}

    // --- Métodos auxiliares para organizar información a ser presentada en la interfaz ---
    public Orden[] getOrdenesPorFechas(LocalDate inicio, LocalDate fin) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Orden[] ans = new Orden[0];
        for (Orden o : ordenes) {
            LocalDate fechaOrden = LocalDate.parse(o.getFecha(), dtf);
            if(!fechaOrden.isBefore(inicio) && !fechaOrden.isAfter(fin)) {
                ans = Arrays.copyOf(ans, ans.length + 1);
                ans[ans.length - 1] = o;
            }
        }
        return ans;
    }
    public Orden[] getOrdenesPendientes(){
        Orden[] ans = new Orden[0];
        for(Orden o : ordenes){
            if(o != null && o.getEstado() != null){
                if(o.getEstado() == Estado.PENDIENTE){
                    ans = Arrays.copyOf(ans, ans.length + 1);
                    ans[ans.length - 1] = o;
                }
            }
        }
        return ans;
    }
    public Producto[] getProductosConStockDisponible(){
        Producto[] ans = new Producto[0];
        for(Producto p : productos){
            if(p != null){
                if (p.getStock() > 0){
                    ans = Arrays.copyOf(ans, ans.length + 1);
                    ans[ans.length - 1] = p;
                }
            }
        }
        return ans;
    }
    public String[] getNombresProductos() {
        Producto[] prods = getProductos();
        String[] nombres = new String[0];
        for (Producto prod : prods) {
            if (prod != null) {
                String n = prod.getNombre();
                if (n != null && !n.isBlank()) {
                    nombres = Arrays.copyOf(nombres, nombres.length + 1);
                    nombres[nombres.length - 1] = n;
                }
            }
        }
        return nombres;
    }
    public String[] getOpcionesProductosDisponibles() {
        Producto[] conStock = getProductosConStockDisponible();
        String[] nombres = new String[0];
        for (Producto producto : conStock) {
            String n = producto != null ? producto.getNombre() : null;
            if (n != null && !n.isBlank()) {
                nombres = Arrays.copyOf(nombres, nombres.length + 1);
                nombres[nombres.length - 1] = n;
            }
        }
        return nombres;
    }
    //Esto sirve para crear una lista en la que se tiene Nombre del cliente - fecha de la orden
    //Esto se hace para los métodos de agregar un item a una orden, eliminar un item de una orden
    //O modificar la cantidad de un item de una orden
    public String[] getOpcionesOrdenesPendientes() {
        Orden[] pendientes = getOrdenesPendientes();
        String[] opciones = new String[pendientes.length];
        for (int i = 0; i < pendientes.length; i++) {
            Orden o = pendientes[i];
            Cliente c = o != null ? o.getCliente() : null;
            String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "?";
            String fecha = (o != null && o.getFecha() != null) ? o.getFecha() : "";
            opciones[i] = clienteStr + " - " + fecha;
        }
        return opciones;
    }

    //Este método sirve para obtener un arreglo de los productos que hay en la orden
    //Se usa para modificar la cantidad de un producto en la orden o para eliminar un producto en la orden
    public String[] getOpcionesProductosEnOrden(UUID idOrden) throws EParametroNulo, EOrdenNoEncontrada {
        if (idOrden == null) throw new EParametroNulo("ID de la orden");
        Orden orden = searchOrden(idOrden);
        OrdenItem[] items = orden.getItems();
        if (items == null) return new String[0];

        String[] nombres = new String[0];
        for (OrdenItem item : items) {
            if (item != null && item.getProducto() != null) {
                String n = item.getProducto().getNombre();
                if (n != null && !n.isBlank()) {
                    nombres = Arrays.copyOf(nombres, nombres.length + 1);
                    nombres[nombres.length - 1] = n;
                }
            }
        }
        return nombres;
    }

}