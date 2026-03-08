package com.nexus.exceptions;

import com.nexus.model.entities.Producto;

public class EStockInsuficiente extends Exception {
    public EStockInsuficiente(Producto p) {
        super("No hay stock suficiente del producto" + p.getNombre()  +  ", el stock actual es de: " + p.getStock());
    }
}
