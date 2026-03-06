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
    public void actualizarStock(int nuevoStock) {
        setStock(nuevoStock);
    }
    public boolean verificarDisponibilidad() {
        return stock > 0;
    }

    @Override
    public void asignarDescuento() {
        if (consumo < 100) { //mas consumo, menor descuento
            descuento = 0.12;  // 12%
        } else {
            descuento = 0.05;  // 5%
        }
    }

    public void escribirHardware(String dir) throws IOException {
        FileOutputStream f = new FileOutputStream(dir);
        ObjectOutputStream b = new ObjectOutputStream(f);
        b.writeObject((Hardware)this);
        b.close();
        f.close();
    }

    public static Hardware leerHardware(String dir) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(dir);
        ObjectInputStream b = new ObjectInputStream(f);
        Hardware hardware = (Hardware) b.readObject();
        f.close();
        b.close();
        return hardware;
    }
}
