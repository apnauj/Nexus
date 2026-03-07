package com.nexus.model.entities;

import com.nexus.exceptions.EParametroNulo;

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


    public Videojuego(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, String[] desarrolladores, String[] generos, boolean multijugador, Date fechaLanzamiento, String plataforma, double tamano) throws EParametroNulo {

        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);

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

    

/*
 * Devuelve una cadena con información resumida del videojuego.
 * Incluye el nombre del juego, la plataforma y el precio final
 * calculado usando el método calcularPrecio() heredado de Producto.
 */
    public String obtenerInfoJuego() {
        return nombre + " - " + plataforma + " - " + calcularPrecio();
    }

/* Este método utiliza el método setStock() heredado de la clase
 * Producto para modificar la cantidad disponible en inventario.
 */
    public void actualizarStock(int nuevoStock) {
        setStock(nuevoStock);
    }
    
/*Verifica si el videojuego está disponible en inventario.*/
    public boolean verificarDisponibilidad() {
        return stock > 0;
    }

    /**
 * Calcula automáticamente el descuento del videojuego según su antigüedad 
 * Si el videojuego tiene más de 2 años desde su fecha de lanzamiento se aplica un descuento del 20%
 * Si tiene menos de 2 años se aplica un descuento del 5%.
 */
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

    /**
 * Serializa el objeto Videojuego y lo guarda en un archivo.
 * Esto permite persistir el objeto en disco para poder recuperarlo posteriormente.
 */
    public void escribirVideojuego(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Videojuego)this);
        b.close();
        f.close();
    }

    /**
 * Lee un archivo serializado y reconstruye un objeto Videojuego.
 * @return el objeto Videojuego leído desde el archivo.
 */
    public static Videojuego leerVideojuego(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Videojuego videojuego = (Videojuego) b.readObject();
        f.close();
        b.close();
        return videojuego;
    }
}
