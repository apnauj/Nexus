package com.nexus.model.entities;

import com.nexus.model.enums.Rol;

import java.util.UUID;

public class Usuario {
    private final UUID id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(String username, String password, Rol rol) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}