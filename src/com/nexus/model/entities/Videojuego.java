package com.nexus.model.entities;

import java.util.Date;

public class Videojuego extends Producto {

    private String[] desarrolladores;
    private String[] generos;
    private boolean multijugador;
    private Date fechaLanzamiento;
    private String plataforma;
    private double tamano;  // GB

    public Videojuego(String nombre, String descripcion,String categoria,int tiempoGarantia,double precioBase,int stock,String[] desarrolladores,String[] generos,boolean multijugador,Date fechaLanzamiento,String plataforma,double tamano) {
        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);

        this.desarrolladores = desarrolladores;
        this.generos = generos;
        this.multijugador = multijugador;
        this.fechaLanzamiento = fechaLanzamiento;
        this.plataforma = plataforma;
        this.tamano = tamano;
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

    

    //informacion resumida del juego
    public String obtenerInfoJuego() {
        return nombre + " - " + plataforma + " - " + calcularPrecio();
    }

    //actualiza stock del producto, setStock se hereda de la clase padre
    public void actualizarStock(int nuevoStock) {
        setStock(nuevoStock);
    }

    public boolean verificarDisponibilidad() {
        return stock > 0;
    }
    
    @Override
    void asignarDescuento() {
        long hoy = System.currentTimeMillis();
        long lanzamiento = fechaLanzamiento.getTime();

        long diferencia = hoy - lanzamiento;

        long anos = diferencia / (1000L * 60 * 60 * 24 * 365);

        if (anos >= 2) {
            descuento = 0.20; // 20%
        } else {
            descuento = 0.05; // 5%
        }
    }
}
