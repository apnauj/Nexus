package com.nexus.model.entities;


import com.nexus.exceptions.EFormatoInvalido;

public class LoginService {

    public static Usuario login(String usuarioIngresado, String contrasenaIngresada, Usuario[] usuariosArray) throws EFormatoInvalido {
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
