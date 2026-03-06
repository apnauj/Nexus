package com.nexus.model.entities;


public class LoginService {

    public static Usuario login(String usuarioIngresado, String contrasenaIngresada, Usuario[] usuariosArray) {
        int i=0;
        boolean sw=false;
        Usuario ur = null;
        while(i<usuariosArray.length && sw==false){
            Usuario u1 = usuariosArray[i];
            if(u1.getUsername()==usuarioIngresado && u1.getPassword()==contrasenaIngresada){
                sw=true;
                ur = u1;
            }
        }
        if(sw){
            System.out.println("Se ha iniciado sesion correctamente");
        }else{
            System.out.println("Credenciales incorrectas");
        }
        return ur;
    }
}
