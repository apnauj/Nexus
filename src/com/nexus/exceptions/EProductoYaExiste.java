package com.nexus.exceptions;

public class EProductoYaExiste extends Exception {
    public EProductoYaExiste(String nombre) {
        super("El producto '" + nombre + "' ya existe.");
    }
}
