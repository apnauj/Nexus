package com.nexus.exceptions;

public class EUsuarioNoEncontrado extends Exception {
    public EUsuarioNoEncontrado(String username) {
        super("El Usuario con nombre de usuario: " + username + " no existe");
    }
}
