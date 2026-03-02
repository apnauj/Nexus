package com.nexus.model.entities;

import com.nexus.model.enums.TipoDocumento;

import java.util.UUID;

public class Cliente {
    private final UUID id;
    private TipoDocumento tipoDoc;
    private String numDoc;
    private String nombre;
    private String apellido;
    private String email;
    private Orden[] historial;

    public Cliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) {
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
    public UUID getId() { return id; }

    
}