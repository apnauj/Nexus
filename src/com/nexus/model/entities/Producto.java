package com.nexus.model.entities;

import java.util.UUID;

import com.nexus.exceptions.EValorNegativo;

public abstract class Producto {

	protected final UUID id;
	protected String nombre;
	protected String descripcion;
	protected String categoria;
	protected int tiempoGarantia;
	protected double precioBase;
	protected double descuento;
	protected int stock;
	
	/*
	Constructor de producto, este constructor recibe nombre, descripcion, categoria, tiempoGarantia
	Debe de hacer varias validaciones para asegurarse de que va a realizar la creación de un producto válido
	1. Se asegura que el precio no es menor ni igual a 0
	2. Se asegura de que el stock con el que se va a inicializar no es menor ni igual a 0
	3. Se asergura de que el tiempo de garantia no es menor ni igual a 0
	4. Si todas estas validaciones pasan podemos crear el producto
	*/
	
	public Producto(String nombre, String descripcion, String categoria, int tiempoGarantia, double precioBase,int stock) throws EValorNegativo {
		if(precioBase<=0) {
			throw new EValorNegativo("El precio base no puede ser negativo, usted registro: "+precioBase);	
		}else if(stock<=0){
			throw new EValorNegativo("El stock no puede ser negativo, el stock registrado fue: "+stock);
		}else if (tiempoGarantia <=0) {
			throw new EValorNegativo("El tiempo de garantia no puede ser negativo, el valor registrado del tiempo de garantia es: "+tiempoGarantia);
		
		}else{
			
		this.id=UUID.randomUUID();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.tiempoGarantia = tiempoGarantia;
		this.precioBase = precioBase;
		this.stock = stock;
		}
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

	/*
	Este method sirve para modificar el precioBase
	La única validación que debemos de hacer es el precio base que debe de ser mayor que 0
	*/
	
	public void setPrecioBase(double precioBase)throws EValorNegativo {
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
	
	public void setDescuento(double descuento)throws EValorNegativo {
		if(descuento <=0 ) {
			throw new EValorNegativo("El valor del descuento no puede ser negativo o cero");
		}else {
			this.descuento = descuento;	
		}
	}
	
	public int getStock() {
		return stock;
	}

	/*
	Este method sirve para modificar el stock
	La única validación que debemos de hacer es que el stock que debe de ser mayor que 0
	*/


	public void setStock(int stock) throws EValorNegativo {
		if(descuento <=0) {
			throw new EValorNegativo("El valor del stock no puede ser negativo o cero, el valor ingresado fue: "+stock);
		}else {
			this.stock = stock;	
		}
		
	}
	
	public String getCategoria() {
		return categoria;
	}
	
	public double calcularPrecio() {
		return precioBase +(precioBase*tiempoGarantia*0.15)-(precioBase*descuento);
	}
	

	 abstract void asignarDescuento(); 

	}