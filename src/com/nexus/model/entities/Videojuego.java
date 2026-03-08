package com.nexus.model.entities;

import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EValorNegativo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Videojuego extends Producto implements Serializable {

    private long serialVersionUID = -576859584434L;
    private String[] desarrolladores;
    private String[] generos;
    private boolean multijugador;
    private Date fechaLanzamiento;
    private String plataforma;
    private double tamano;  // GB


    public Videojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String[] desarrolladores, String[] generos, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano) throws EParametroNulo, ECantidadNegativa, EValorNegativo {

        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);
        if(desarrolladores == null || desarrolladores.length == 0) throw new EParametroNulo("desarrolladores");
        if(generos == null || generos.length == 0) throw new EParametroNulo("generos");
        if(plataforma == null || plataforma.isBlank()) throw new EParametroNulo("plataforma");
        if (tamano < 0) throw new ECantidadNegativa("El peso en GB del videojuego no puede ser negativo");
        this.desarrolladores = desarrolladores;
        this.generos = generos;
        this.multijugador = multijugador;
        this.fechaLanzamiento = fechaLanzamiento;
        this.plataforma = plataforma;
        this.tamano = tamano;
        asignarDescuento();
    }

    public String[] getDesarrolladores() {
        return desarrolladores;
    }

    public String[] getGeneros() {
        return generos;
    }

    public boolean getMultijugador() {
        return multijugador;
    }

    public Date getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public double getTamano() {
        return tamano;
    }

    public void setGeneros(String[] generos) {
        this.generos = generos;
    }
    public void setModoMultijugador(boolean multijugador) {
        this.multijugador = multijugador;
    }

    public void setFechaLanzamiento(Date fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public void setTamano(double tamano) {
        this.tamano = tamano;
    }

    

    public String obtenerInfoJuego() {
        return nombre + " - " + plataforma + " - " + calcularPrecio();
    }

    public boolean verificarDisponibilidad() {
        return stock > 0;
    }


    @Override
    void asignarDescuento() {
        if(fechaLanzamiento != null){
            long hoy = System.currentTimeMillis();
            long lanzamiento = fechaLanzamiento.getTime();

            long diferencia = hoy - lanzamiento;

            long años = diferencia / (1000L * 60 * 60 * 24 * 365);

            if (años >= 2) {
                descuento = 0.20; // 20%
            } else {
                descuento = 0.05; // 5%
            }
        } else {
            descuento = 0.05;
        }

    }


    public void escribirVideojuego(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Videojuego)this);
        b.close();
        f.close();
    }

    
    public static Videojuego leerVideojuego(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Videojuego videojuego = (Videojuego) b.readObject();
        f.close();
        b.close();
        return videojuego;
    }
}
