package com.nexus.model.entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EValorNegativo;

public abstract class Producto implements Serializable {

	private static final long serialVersionUID = 1L;
	protected final UUID id;
	protected String nombre;
	protected String descripcion;
	protected String categoria;
	protected int tiempoGarantia;
	protected double precioBase;
	protected double descuento;
	protected int stock;
	protected boolean descuentoActivo;
	
	/*
	Constructor de producto, este constructor recibe nombre, descripcion, categoria, tiempoGarantia
	Debe de hacer varias validaciones para asegurarse de que va a realizar la creación de un producto válido
	1. Se asegura que el precio no es menor ni igual a 0
	2. Se asegura de que el stock con el que se va a inicializar no es menor ni igual a 0
	3. Se asergura de que el tiempo de garantia no es menor ni igual a 0
	4. Si todas estas validaciones pasan podemos crear el producto
	*/
	
	public Producto(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase, int stock) throws EParametroNulo, EValorNegativo {
		if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
		if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
		if (precioBase < 0) throw new EValorNegativo("El precio base no puede ser negativo, usted registro: "+precioBase);
		if (stock < 0) throw new EValorNegativo("El stock no puede ser negativo, el stock registrado fue: " + stock);
		if (tiempoGarantia <0) throw new EValorNegativo("El tiempo de garantia no puede ser negativo, el valor registrado del tiempo de garantia es: "+tiempoGarantia);
		if (descripcion == null || descripcion.isBlank()) throw new EParametroNulo("descripción");
			
		this.id=UUID.randomUUID();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.tiempoGarantia = tiempoGarantia;
		this.precioBase = precioBase;
		this.stock = stock;
		this.descuentoActivo = true;
		this.descuento = 0;
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) throws EParametroNulo {
		if (nombre == null || nombre.isBlank()) throw new EParametroNulo("nombre del producto");
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) throws EParametroNulo {
		if (descripcion == null || descripcion.isBlank()) throw new EParametroNulo("descripción");
		this.descripcion = descripcion;
	}
	
	public int getTiempoGarantia() {
		return tiempoGarantia;
	}
	
	public void setTiempoGarantia(int tiempoGarantia) throws EValorNegativo {
		if(tiempoGarantia>0) {
		this.tiempoGarantia = tiempoGarantia;
		}else {
			throw new EValorNegativo("El tiempo de garantia no puede ser menor a 1");
		}
	}
	
	public double getPrecioBase() {
		return precioBase;
	}

	/*
	Este method sirve para modificar el precioBase
	La única validación que debemos de hacer es el precio base que debe de ser mayor que 0
	*/
	
	public void setPrecioBase(double precioBase) throws EValorNegativo {
		if(precioBase <=0) {
			throw new EValorNegativo("El valor del precio base no puede ser negativo o cero, el valor registrado fue: "+precioBase);
		}else {
			this.precioBase = precioBase;	
		}
	}
	
	public double getDescuento() {
		return descuento;
	}

	/*
	Este method sirve para modificar el descuento
	La única validación que debemos de hacer es que el descuento que debe de ser mayor que 0
	*/
	
	public int getStock() {
		return stock;
	}

	/*
	Este method sirve para modificar el stock
	La única validación que debemos de hacer es que el stock no sea negativo
	*/


	public void setStock(int stock) throws EValorNegativo {
		if (stock < 0) {
			throw new EValorNegativo("El valor del stock no puede ser negativo, el valor ingresado fue: " + stock);
		}
		this.stock = stock;
	}
	
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) throws EParametroNulo {
		if (categoria == null || categoria.isBlank()) throw new EParametroNulo("categoría");
		this.categoria=categoria;
	}
	
	/**
	 * Calcula el precio final. Si el descuento está activo, reduce el precio base.
	 * Si está inactivo, devuelve el precio base sin descuento.
	 */
	public double calcularPrecio() {
		if (!descuentoActivo) return precioBase;
		return precioBase * (1 - descuento);
	}

	public boolean isDescuentoActivo() {
		return descuentoActivo;
	}

	public void setDescuentoActivo(boolean activo) {
		this.descuentoActivo = activo;
		if (!activo) this.descuento = 0;
	}

	public void activarDescuento() {
		this.descuentoActivo = true;
	}

	public abstract double asignarDescuento();

	public void desactivarDescuento() {
		descuentoActivo = false;
		descuento = 0;
	}

}