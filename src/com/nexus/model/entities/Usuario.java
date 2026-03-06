package com.nexus.model.entities;

import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.Rol;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class Usuario implements Serializable {
    
    private long serialVersionUID = -67675765756776L;
    private final UUID id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(String username, String password, Rol rol) throws EParametroNulo{
        if (username == null || username.isBlank()) {
            throw new EParametroNulo("username");
        }
        if (password == null) {
            throw new EParametroNulo("password");
        }
        if (rol == null) {
            throw new EParametroNulo("rol");
        }
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

    public void escribirUsuario(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Usuario)this);
        b.close();
        f.close();
    }

    public static Usuario leerUsuario(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Usuario usuario = (Usuario) b.readObject();
        f.close();
        b.close();
        return usuario;
    }
}