package com.nexus.exceptions;

public class EUsuarioYaExiste extends Exception {
    public EUsuarioYaExiste(String username) {
        super("El usuario '" + username + "' ya existe.");
    }
}
