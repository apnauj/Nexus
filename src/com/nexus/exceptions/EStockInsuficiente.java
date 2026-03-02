package com.nexus.exceptions;

import com.nexus.model.entities.Producto;

public class EStockInsuficiente extends RuntimeException {
    public EStockInsuficiente(Producto p) {
        super("No hay stock suficiente del producto, el stock actual es de: " + p.getStock());
    }
}
