package com.nexus.exceptions;

/**
 * Se lanza cuando se intenta eliminar una entidad que está asociada a órdenes.
 */
public class EHistorialOrden extends Exception {
    public EHistorialOrden(String mensaje) {
        super(mensaje);
    }
}
