package com.nexus.model.entities;

import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EValorNegativo;

import java.io.*;
import java.util.Date;

public class Hardware extends Producto implements Serializable {

    private static final long serialVersionUID = -2343434343434L;
    private float consumo;
    private String fabricante;

    public Hardware (String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock, float consumo, String fabricante, boolean descuentoActivo) throws EParametroNulo, ECantidadNegativa, EValorNegativo {
        super(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock);
        if(consumo<0) throw new ECantidadNegativa("El consumo debe ser un número entero positivo");
        if (fabricante == null || fabricante.isBlank()) throw new EParametroNulo("fabricante");

        this.consumo = consumo;
        this.fabricante = fabricante;
        if (descuentoActivo) {
            setDescuentoActivo(true);
            asignarDescuento();
        } else {
            desactivarDescuento();
        }
    }
    public float getConsumo() {
        return consumo;
    }
    public String getFabricante() {
        return fabricante;
    }
    public void setFabricante(String fabricante) throws EParametroNulo {
        if(fabricante == null || fabricante.isBlank()) throw new EParametroNulo("fabricante");
        this.fabricante = fabricante;
    }
    public void setConsumo(float consumo) throws EValorNegativo {
        if(consumo < 0) throw new EValorNegativo("El consumo debe ser un número entero positivo");
        this.consumo = consumo;
    }

   
    public boolean verificarDisponibilidad() {
        return stock > 0;
    }

    @Override
    public double asignarDescuento() {
        double pct = (stock <= 5) ? 0.05 : ((stock <= 15) ? 0.10 : 0.15);
        if (consumo < 100) pct += 0.03;
        descuento = Math.min(pct, 0.20);
        return descuento;
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
