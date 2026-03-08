package com.nexus.model.entities;


import com.nexus.exceptions.ECredencialesInvalidas;
import com.nexus.exceptions.EParametroNulo;

public class LoginService {

    public static Usuario login(String usuarioIngresado, String contrasenaIngresada, Usuario[] usuariosArray)
            throws ECredencialesInvalidas, EParametroNulo {
        if (usuarioIngresado == null || usuarioIngresado.isBlank()) {
            throw new EParametroNulo("usuarioIngresado");
        }
        if (contrasenaIngresada == null || contrasenaIngresada.isBlank()) {
            throw new EParametroNulo("contrasenaIngresada");
        }

        for (Usuario u : usuariosArray) {
            if (u.getUsername().equalsIgnoreCase(usuarioIngresado) && u.verificarPassword(contrasenaIngresada)) {
                return u;
            }
        }
        throw new ECredencialesInvalidas();
    }
}
