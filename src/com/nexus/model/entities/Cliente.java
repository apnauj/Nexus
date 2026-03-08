package com.nexus.model.entities;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.TipoDocumento;

public class Cliente implements Serializable{
    private long serialVersionUID = -78459984354675L;
    private final UUID id;
    private TipoDocumento tipoDoc;
    private String numDoc;
    private String nombre;
    private String apellido;
    private String email;
    private Orden[] historial;

    public Cliente(TipoDocumento tipoDoc, String numDoc, String nombre, String apellido, String email) throws EParametroNulo, EFormatoInvalido {
        if (tipoDoc == null) {
            throw new EParametroNulo("tipoDoc");
        }
        if (numDoc == null || numDoc.isBlank()) {
            throw new EParametroNulo("numDoc", "El número de documento no puede ser null o vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new EParametroNulo("nombre", "El nombre no puede ser null o vacío.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new EParametroNulo("apellido","El apellido no puede ser null o vacio");
        }
        if (email == null || email.isBlank()) {
            throw new EParametroNulo("email","El email no puede ser null o vacio");
        }
        validarEmail(email);
        validarDocumento(numDoc);
        this.id = UUID.randomUUID();
        this.tipoDoc = tipoDoc;
        this.numDoc = numDoc;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.historial = new Orden[0];
    }

    private void validarDocumento(String doc) throws EFormatoInvalido {
        // Verifica que sean solo números y máximo 10 dígitos
        if (!doc.matches("\\d{1,10}")) {
            throw new EFormatoInvalido("El documento debe ser numérico y tener máximo 10 dígitos.");
        }
    }

    private void validarEmail(String email) throws EFormatoInvalido {
        // Verifica que contenga '@' y '.'
        if (!email.contains("@") || !email.contains(".")) {
            throw new EFormatoInvalido("El formato del email es inválido (debe contener @ y .)");
        }
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
    public void setNumDoc(String numDoc) throws EFormatoInvalido {
        validarDocumento(numDoc);
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
    public void setEmail(String email) throws EFormatoInvalido {
        validarEmail(email);
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

    public void escribirCliente(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Cliente)this);
        b.close();
        f.close();
    }

    public static Cliente leerCliente(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Cliente cliente = (Cliente) b.readObject();
        f.close();
        b.close();
        return cliente;
    }

    
}