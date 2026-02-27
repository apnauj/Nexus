package com.nexus.model.entities;

import java.util.UUID;

public abstract class Producto {

	protected UUID id;
	protected String nombre;
	protected String descripcion;
	protected String categoria;
	protected int tiempoGarantia;
	protected double precioBase;
	protected double descuento;
	protected int stock;
	
	
	
	public Producto(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase,int stock) {
		this.id=UUID.randomUUID();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.tiempoGarantia = tiempoGarantia;
		this.precioBase = precioBase;
		this.stock = stock;
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public int getTiempoGarantia() {
		return tiempoGarantia;
	}
	
	public void setTiempoGarantia(int tiempoGarantia) {
		this.tiempoGarantia = tiempoGarantia;
	}
	
	public double getPreciobase() {
		return precioBase;
	}
	
	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}
	
	public double getDescuento() {
		return descuento;
	}
	
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
	public int getStock() {
		return stock;
	}
	
	public void setStock(int stock) {
		this.stock = stock;
	}
	
	public String getCategoria() {
		return categoria;
	}
	
	public void mostrarProducto(Producto[] productos) {
		for(Producto p:productos) {
			System.out.print(p.getNombre()+" || ");
		}
	}
	
	public double calcularPrecio() {
		return precioBase +(precioBase*tiempoGarantia*0.15)-(precioBase*descuento);
	}
	

	 abstract void asignarDescuento(); 

	}