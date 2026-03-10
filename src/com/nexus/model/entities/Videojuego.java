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

    private static final long serialVersionUID = -576859584434L;
    private String desarrollador;
    private String genero;
    private boolean multijugador;
    private Date fechaLanzamiento;
    private String plataforma;
    private double tamano;  // GB


    public Videojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String desarrollador, String genero, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano, boolean descuentoActivo) throws EParametroNulo, ECantidadNegativa, EValorNegativo {

        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);
        if (desarrollador == null || desarrollador.isBlank()) throw new EParametroNulo("desarrollador");
        if (genero == null || genero.isBlank()) throw new EParametroNulo("género");
        if (plataforma == null || plataforma.isBlank()) throw new EParametroNulo("plataforma");
        if (tamano < 0) throw new ECantidadNegativa("El peso en GB del videojuego no puede ser negativo");
        this.desarrollador = desarrollador;
        this.genero = genero;
        this.multijugador = multijugador;
        this.fechaLanzamiento = fechaLanzamiento;
        this.plataforma = plataforma;
        this.tamano = tamano;
        if (descuentoActivo) {
            setDescuentoActivo(true);
            asignarDescuento();
        } else {
            desactivarDescuento();
        }
    }

    public String getDesarrollador() {
        return desarrollador;
    }

    public String getGenero() {
        return genero;
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

    public void setDesarrollador(String desarrollador) {
        this.desarrollador = desarrollador;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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


    @Override
    public double asignarDescuento() {
        double pct = stock <= 5 ? 0.05 : (stock <= 15 ? 0.10 : 0.15);
        if (fechaLanzamiento != null) {
            long años = (System.currentTimeMillis() - fechaLanzamiento.getTime()) / (1000L * 60 * 60 * 24 * 365);
            if (años >= 2) pct += 0.05;
        }
        descuento = Math.min(pct, 0.20);
        return descuento;
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
