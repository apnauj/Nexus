package com.nexus.exceptions;

/**
 * Se lanza cuando un valor numérico es negativo o cero cuando debe ser positivo.
 */
public class EValorNegativo extends Exception {
    public EValorNegativo(String mensaje) {
        super(mensaje);
    }
}
