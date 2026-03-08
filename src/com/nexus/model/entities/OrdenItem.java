package com.nexus.model.entities;

import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EStockInsuficiente;
import com.nexus.exceptions.EValorNegativo;

import java.io.Serializable;

public class OrdenItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Producto producto;
    private int cantidad;

    public OrdenItem(Producto producto, int cantidad) throws ECantidadNegativa, EParametroNulo, EStockInsuficiente{
        if (producto == null) {
            throw new EParametroNulo("producto");
        }
        this.producto = producto;
        if(cantidad <= 0){
            throw new ECantidadNegativa("Cantidad inválida solo se aceptan cantidades positivas");
        } else if(producto.getStock() < cantidad){
            throw new EStockInsuficiente(producto);
        } else {
            this.cantidad = cantidad;
        }
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad(){
        return cantidad;
    }

    public void setCantidad(int a) throws ECantidadNegativa {
        if (a <= 0) {
            throw new ECantidadNegativa("La cantidad debe ser positiva");
        }
        this.cantidad = a;
    }

    public double calcularSubtotal(){
        return producto.calcularPrecio()*cantidad;
    }

    public boolean stockSuficiente(){
        return producto.getStock() >= cantidad;
    }
}