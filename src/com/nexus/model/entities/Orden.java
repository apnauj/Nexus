package com.nexus.model.entities;
// Importa Enums
import com.nexus.model.enums.Estado;
import com.nexus.model.enums.MetodoPago;

import java.util.Arrays;
import java.util.UUID;

public class Orden {
    private final UUID idPedido;
    private Cliente cliente;
    private String fecha;
    private Estado estado;
    private MetodoPago metodoPago;
    private OrdenItem items[];

    //Constructor
    public Orden(Cliente cliente, String fecha,MetodoPago metodoPago) {
        //Genera automaticamente el id unico
        this.idPedido = UUID.randomUUID();
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = Estado.Pendiente;
        this.metodoPago = metodoPago;
        this.items = new OrdenItem[0];
    }

    public UUID getIdPedido() {
        return idPedido;
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

    public OrdenItem[] getItems() {
        return items;
    }

    public void setItems(OrdenItem items) {
        this.items = new OrdenItem[]{items};
    }

    public String getFecha(){
        return fecha;
    }

    public Cliente getCliente(){
        return cliente;
    }

    public String addItemOrden(OrdenItem a){
        //Valida que la orden no sea aprobada ni rechazada
        if (this.estado != Estado.Pendiente) {
            throw new IllegalStateException("Solo se pueden agregar items a órdenes en estado Pendiente");
        }
        //Valida si existe la OrdenxItem
        if (a==null){
            return "No existe la Orden X Item";
        } else {
            //Crea un espacio en el arreglo y agrega el producto
            items = Arrays.copyOf(items, items.length + 1);
            items[items.length - 1] = a;
            return "Se agregó correctamente el producto";
        }
    }

    public double calcularTotal(){
        double total = 0;
        for(OrdenItem i:items){
            total+=i.calcularSubtotal();
        }
        return total;
    }
}