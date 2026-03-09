package com.nexus.model.entities;

import com.nexus.exceptions.EFormatoInvalido;
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

    private static final long serialVersionUID = -67675765756776L;
    private final UUID id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(String username, String password, Rol rol) throws EParametroNulo, EFormatoInvalido {
        if (username == null || username.isBlank()) throw new EParametroNulo("username");
        if (password == null || password.isBlank()) throw new EParametroNulo("password", "La contraseña no puede ser null o vacio");
        if (rol == null) throw new EParametroNulo("rol");
        validarPassword(password);
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    private void validarPassword(String pass) throws EFormatoInvalido {
        /* Explicación del Regex:
           ^                : Inicio de cadena
           (?=.*[0-9])      : Al menos un número
           (?=.*[A-Z])      : Al menos una mayúscula
           .{8,16}          : Entre 8 y 16 caracteres
           $                : Fin de cadena
        */
        String regex = "^(?=.*[0-9])(?=.*[A-Z]).{8,16}$";

        if (!pass.matches(regex)) {
            throw new EFormatoInvalido("La contraseña debe tener entre 8 y 16 caracteres, incluir al menos un número y una letra mayúscula.");
        }
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

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Verifica si la contraseña ingresada coincide con la almacenada.
     * No valida formato; solo compara para uso en login.
     */
    public boolean verificarPassword(String input) {
        return password != null && password.equals(input);
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