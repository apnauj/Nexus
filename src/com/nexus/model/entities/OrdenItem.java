package com.nexus.model.entities;

public class OrdenItem {
    private final Producto producto;
    private int cantidad;

    public OrdenItem(Producto producto, int cantidad) throws EStockInsuficiente, ECantidadNegativa {
        this.producto = producto;
        if(cantidad < 0){
            throw new ECantidadNegativa("Cantidad inválida solo se aceptan cantidades positivas");
        } else if(producto.getStock() < cantidad){
            throw new EStockInsuficiente(producto);
        } else {
            this.cantidad = cantidad;
        }
        producto.setStock(producto.getStock() - cantidad);
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