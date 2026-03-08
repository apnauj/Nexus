package com.nexus.exceptions;

/**
 * Se lanza cuando el usuario o la contraseña son incorrectos durante el login.
 */
public class ECredencialesInvalidas extends Exception {
    public ECredencialesInvalidas() {
        super("Usuario o contraseña incorrectos.");
    }

    public ECredencialesInvalidas(String mensaje) {
        super(mensaje);
    }
}
