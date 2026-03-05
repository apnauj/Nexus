package com.nexus.model.entities;

import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.enums.Estado;
import com.nexus.model.enums.MetodoPago;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class Orden implements Serializable {
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
        if (cliente == null) {
            throw new EParametroNulo("cliente");
        }
        if (fecha == null || fecha.isBlank()) {
            throw new EParametroNulo("fecha");
        }
        if (metodoPago == null) {
            throw new EParametroNulo("metodoPago");
        }
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

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public double calcularTotal(){
        double total = 0;
        for(OrdenItem i:items){
            total+=i.calcularSubtotal();
            setTotal(total);
        }
        return total;
    }

    public UUID getIdPedido() {
    	return this.idPedido;
    }
    
    public OrdenItem[] getItems() {
    	return this.items;
    }

    public String  addItemOrden(OrdenItem a){
        //Valida que la orden no sea aprobada ni rechazada
        if (this.estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden agregar items a órdenes en estado Pendiente");
        }
        //Valida si existe la OrdenxItem
        if (a == null){
            return "No existe la Orden X Item";
        } else {
            //Crea un espacio en el arreglo y agrega el producto
            items = Arrays.copyOf(items, items.length + 1);
            items[items.length - 1] = a;
            return "Se agregó correctamente el producto";
        }
    }

    public OrdenItem removeItemAt(int index) {
        if (this.estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden quitar items de órdenes en estado Pendiente");
        }
        if (index < 0 || index >= items.length) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
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

    public void setValorPagado(double valorPagado) {
        this.valorPagado = valorPagado;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double total, double valorPagado) {
        this.cambio = total-valorPagado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void cambioEstado(){
        if(total<=valorPagado){
            setEstado(Estado.APROBADO);
        }else{
            setEstado(Estado.RECHAZADO);
        }
    }

    public String decrementarStock(OrdenItem items[]) throws EValorNegativo {
        String text = "";
        if(estado == Estado.APROBADO) {
            for (OrdenItem i : items) {
                Producto p = i.getProducto();
                if (i.stockSuficiente(i.getProducto())) {
                    p.setStock(p.getStock() - i.getCantidad());
                } else {
                    throw new EValorNegativo("No hay suficientes productos quedan: " + p.getStock() + p.getNombre());
                }
                text= "El proceso se ha ejecutado correctamente";
            }
        }else{
            text = "La orden no ha sido aprobada";
        }
        return text;
    }

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
    //TODO: modificar la cantidad exclusivamente de un OrdenItem
}