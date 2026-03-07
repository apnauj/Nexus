package com.nexus.model.entities;

import com.nexus.exceptions.EParametroNulo;

import java.io.*;
import java.util.Date;

public class Hardware extends Producto implements Serializable {

    private long serialVersionUID = -2343434343434L;
    private float consumo;
    private String fabricante;

    public Hardware(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante) throws EParametroNulo {
        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);

        this.consumo = consumo;
        this.fabricante = fabricante;
        asignarDescuento();
    }
    public float getConsumo() {
        return consumo;
    }
    public String getFabricante() {
        return fabricante;
    }
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }
    public void setConsumo(float consumo) {
        this.consumo = consumo;
    }

    /**
 * Actualiza el stock del hardware en el inventario.
 * Utiliza el método setStock heredado de la clase Producto.
 */
    public void actualizarStock(int nuevoStock) {
        setStock(nuevoStock);
    }

    /**
 * Verifica si el hardware está disponible en inventario.*/
    public boolean verificarDisponibilidad() {
        return stock > 0;
    }

    /**
 * Calcula el descuento del hardware según su consumo energético.
 * Si el consumo es menor a 100 se aplica un descuento del 12%.
 * Si el consumo es mayor o igual se aplica un descuento del 5%.
 */
    @Override
    public void asignarDescuento() {
        if (consumo < 100) { //mas consumo, menor descuento
            descuento = 0.12;  // 12%
        } else {
            descuento = 0.05;  // 5%
        }
    }

    /**
 * Serializa el objeto Hardware y lo guarda en un archivo.
 */
    public void escribirHardware(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Hardware)this);
        b.close();
        f.close();
    }

    /**
 * Lee un archivo serializado y reconstruye un objeto Hardware.
 */
    public static Hardware leerHardware(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Hardware hardware = (Hardware) b.readObject();
        f.close();
        b.close();
        return hardware;
    }
}
