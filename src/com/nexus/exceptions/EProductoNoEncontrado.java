package com.nexus.exceptions;

public class EProductoNoEncontrado extends RuntimeException {
    public EProductoNoEncontrado(String nombre) {
        super("No se ha podido encontrar el producto: " + nombre);
    }
}
