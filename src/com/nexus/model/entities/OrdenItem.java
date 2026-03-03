package com.nexus.model.entities;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EStockInsuficiente;
import com.nexus.exceptions.EValorNegativo;

public class OrdenItem {
    private final Producto producto;
    private int cantidad;

    public OrdenItem(Producto producto, int cantidad) throws EProductoNoEncontrado, ECantidadNegativa, EValorNegativo{
        if(StoreController.getProductos().contains(producto)) {
            this.producto = producto;
        }else{
            throw new EProductoNoEncontrado("No se encontro el producto");
        }
        if(cantidad <= 0){
            throw new ECantidadNegativa("Cantidad inválida solo se aceptan cantidades positivas");
        } else if(producto.getStock() < cantidad){
            throw new EStockInsuficiente(producto);
        } else {
            this.cantidad = cantidad;
        }
        producto.setStock(producto.getStock() - cantidad);
    }
    public Producto getProducto() {
        return producto;
    }

    public int getCantidad(){
        return cantidad;
    }

    public void setCantidad(int a) {
        if (a <= 0) {
            throw new ECantidadNegativa("La cantidad debe ser positiva");
        }
        this.cantidad = a;
    }

    public double calcularSubtotal(){
        return producto.calcularPrecio()*cantidad;
    }


}