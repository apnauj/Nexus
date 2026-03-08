package com.nexus.model.entities;

import com.nexus.exceptions.*;
import com.nexus.model.enums.Estado;
import com.nexus.model.enums.MetodoPago;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class Orden implements Serializable {
    
    private long serialVersionUID = -97089709609L;
    private final UUID idPedido;
    private Cliente cliente;
    private String fecha;
    private Estado estado;
    private MetodoPago metodoPago;
    private double valorPagado;
    private double cambio;
    private double total;
    private OrdenItem items[];

    public Orden(Cliente cliente, String fecha, MetodoPago metodoPago) throws EParametroNulo {
        if (cliente == null) throw new EParametroNulo("cliente");
        if (fecha == null || fecha.isBlank()) throw new EParametroNulo("fecha");
        if (metodoPago == null) throw new EParametroNulo("metodoPago");

        this.idPedido = UUID.randomUUID();
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = Estado.PENDIENTE;
        this.metodoPago = metodoPago;
        this.valorPagado = 0;
        this.cambio = 0;
        this.total = 0;
        this.items = new OrdenItem[0];
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getFecha(){
        return fecha;
    }

    public Cliente getCliente(){
        return cliente;
    }

    public UUID getIdPedido() {
    	return this.idPedido;
    }
    
    public OrdenItem[] getItems() {
    	return this.items;
    }

   /**
 * Agrega un nuevo item a la orden.
 * Este método valida que:
 * La orden esté en estado PENDIENTE.
 * El item recibido no sea nulo
 * Luego agrega el item al arreglo de items de la orden.
 * Lanza la excepcion EParametroNulo
 */
    public void  addItemOrden(OrdenItem a) throws EParametroNulo, EEstadoOrdenInvalido{
        //Valida que la orden no sea aprobada ni rechazada
        if (this.estado != Estado.PENDIENTE) {
            throw new EEstadoOrdenInvalido("Solo se pueden agregar items a órdenes en estado Pendiente");
        }
        //Valida OrdenxItem es nulo
        if (a == null){
            throw new EParametroNulo("No se permiten detalles nulos");
        } else {
            //Crea un espacio en el arreglo y agrega el producto
            if (items == null) items = new OrdenItem[0];
            items = Arrays.copyOf(items, items.length + 1);
            items[items.length - 1] = a;
            System.out.println("Se agregó correctamente el producto");
        }
    }

    /**
 * Elimina un item de la orden usando su índice.
 * El método valida:
 * Que la orden esté en estado PENDIENTE.
 * Que el índice esté dentro del rango válido.
 * Luego crea un nuevo arreglo sin el item eliminado.
 */
    public OrdenItem removeItemAt(int index) throws EEstadoOrdenInvalido, EIndiceInvalido{
        //Valida si no está aprobado o rechazado
        if (this.estado != Estado.PENDIENTE) {
            throw new EEstadoOrdenInvalido("Solo se pueden quitar items de órdenes en estado Pendiente");
        }
        //Valida si el parametro pasado no es negativo o mayor a la cantidad del arreglo
        //Revisar
        if (index < 0 || index >= items.length) {
            throw new EIndiceInvalido("Índice inválido: " + index);
        }
        OrdenItem removed = items[index];
        OrdenItem[] newItems = new OrdenItem[items.length - 1];
        int j = 0;
        for (int i = 0; i < items.length; i++) {
            if (i != index) {
                newItems[j] = items[i];
                j++;
            }
        }
        items = newItems;
        return removed;

    }

    public double getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(double valorPagado) throws EValorNegativo {
        if(valorPagado < 0) throw new EValorNegativo("El valor pagado no puede ser negativo");
        this.valorPagado = valorPagado;
    }

    public double getCambio() {
        return cambio;
    }

    //Traer total
    public double getTotal() {
        return total;
    }

    /**
 * Calcula el valor total de la orden.
 * El método recorre todos los items de la orden
 * y suma el subtotal de cada uno.
 * Cada subtotal corresponde a:
 * precio del producto * cantidad
 */
    public double calcularTotal(){
        if (items == null) { setTotal(0); return 0; }
        double total = 0;
        //Recorre el arreglo
        for(OrdenItem i:items){
            total+=i.calcularSubtotal();
        }
        setTotal(total);
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Cambia el estado de compra si el valorPagado es mayor o igual al total.
    // Nota: El decremento de stock se realiza en StoreController sobre el arreglo real de productos.
    public void cambioEstado() throws EValorNegativo {
        if (total <= valorPagado) {
            setEstado(Estado.APROBADO);
            this.cambio = valorPagado - total;
        } else {
            setEstado(Estado.RECHAZADO);
            this.cambio = valorPagado;
        }
    }

    //Serialización
    public void escribirOrden(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Orden)this);
        b.close();
        f.close();
    }

    public static Orden leerOrden(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Orden orden = (Orden) b.readObject();
        f.close();
        b.close();
        return orden;
    }
}


