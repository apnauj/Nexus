package com.nexus.exceptions;

/**
 * Se lanza cuando un valor tiene formato inválido (email, documento, etc.).
 */
public class EFormatoInvalido extends Exception {
    public EFormatoInvalido(String mensaje) {
        super(mensaje);
    }
}
