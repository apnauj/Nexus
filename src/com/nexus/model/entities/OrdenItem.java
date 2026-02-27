package com.nexus.model.entities;

public class OrdenItem {
    private Producto producto;
    private int cantidad;

    public OrdenItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    //Por ahora get cantidad no se usa !!!
    public int getCantidad(){
        return cantidad;
    }

    public void setCantidad(int a){
        this.cantidad = a;
    }

    public double calcularSubtotal(){
        return producto.getPreciobase()*cantidad;
    }
}