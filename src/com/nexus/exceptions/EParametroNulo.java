package com.nexus.exceptions;

/**
 * Se lanza cuando un parámetro requerido es null.
 */
public class EParametroNulo extends Exception {
    public EParametroNulo(String nombreParametro) {
        super("El parámetro '" + nombreParametro + "' no puede ser null.");
    }

    public EParametroNulo(String nombreParametro, String mensaje) {
        super(nombreParametro + ": " + mensaje);
    }
}
