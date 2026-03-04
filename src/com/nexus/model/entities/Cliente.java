package com.nexus.model.entities;

import java.util.Arrays;
import java.util.UUID;

import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.TipoDocumento;

public class Cliente {
    private final UUID id;
    private TipoDocumento tipoDoc;
    private String numDoc;
    private String nombre;
    private String apellido;
    private String email;
    private Orden[] historial;

    public Cliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) throws EParametroNulo {
        if (tipoDoc == null) {
            throw new EParametroNulo("tipoDoc");
        }
        if (numDoc == null || numDoc.isBlank()) {
            throw new EParametroNulo("numDoc", "El número de documento no puede ser null o vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new EParametroNulo("nombre", "El nombre no puede ser null o vacío.");
        }
        this.id = UUID.randomUUID();
        this.tipoDoc = tipoDoc;
        this.numDoc = numDoc;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.historial = new Orden[0];
    }



    public TipoDocumento getTipoDoc(){
        return tipoDoc;
    }
    public void setTipoDoc(TipoDocumento tipoDoc){
        this.tipoDoc = tipoDoc;
    }
    public String getNumDoc(){
        return numDoc;
    }
    public void setNumDoc(String numDoc){
        this.numDoc = numDoc;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Orden[] getHistorial() {
        return historial;
    }
    public void setHistorial(Orden[] historial) {
        this.historial = historial;
    }

    /*
    Este méthod se encarga de añadir una compra al historial
    de un cliente
    */
    
    public void AgregarCompras(Orden o) throws EParametroNulo{
        if (o == null) {
            throw new EParametroNulo("orden");
        }
        historial = Arrays.copyOf(historial, historial.length + 1);
        historial[historial.length - 1] = o;
    }

    public UUID getId() { return id; }


    
}