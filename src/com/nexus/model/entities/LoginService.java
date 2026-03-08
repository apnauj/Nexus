package com.nexus.model.entities;


import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;

public class LoginService {

    public static Usuario login(String usuarioIngresado, String contrasenaIngresada, Usuario[] usuariosArray) throws EFormatoInvalido, EParametroNulo {
        if(usuarioIngresado == null || usuarioIngresado.isBlank()) throw new EParametroNulo("usuarioIngresado");
        if(contrasenaIngresada == null || contrasenaIngresada.isBlank()) throw new EParametroNulo("contrasenaIngresada");

        int i=0;
        boolean sw=false;
        Usuario ur = null;
        while(i<usuariosArray.length && !sw){
            Usuario u1 = usuariosArray[i];
            if(u1.getUsername().equals(usuarioIngresado) && u1.getPassword().equals(contrasenaIngresada)){
                sw=true;
                ur = u1;
            }
            i++;
        }
        if(sw){
            System.out.println("Se ha iniciado sesion correctamente");
        }else{
            System.out.println("Credenciales incorrectas");
        }
        return ur;
    }
}
