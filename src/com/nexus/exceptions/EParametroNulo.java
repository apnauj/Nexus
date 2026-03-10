package com.nexus.exceptions;

public class EParametroNulo extends Exception {
    public EParametroNulo(String nombreDescriptivo) {
        super("El campo '" + nombreDescriptivo + "' no puede ser nulo o vacío.");
    }

    public EParametroNulo(String nombreDescriptivo, String mensaje) {
        super(mensaje);
    }
}
