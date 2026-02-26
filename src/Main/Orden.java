package Main;
// Importa Enums
import Main.enumOrden.Estado;
import Main.enumOrden.MetodoPago;

import java.util.Arrays;
import java.util.UUID;

public class Orden {
    private UUID idPedido;
    private Cliente cliente;
    private String fecha;
    private Estado estado;
    private MetodoPago metodoPago;
    private OrdenItem items[];

    //Constructor
    public Orden(Cliente cliente, String fecha, Estado estado, MetodoPago metodoPago) {
        //Genera automaticamente el id unico
        this.idPedido = UUID.randomUUID();
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public String getFecha(){
        return fecha;
    }

    public Cliente getCliente(){
        return cliente;
    }

    public String addItemOrden(OrdenItem a){
        //Valida si existe la OrdenxItem
        if (a==null){
            return "No existe la Orden X Item";
        }else {
            //Crea un espacio en el arreglo y agrega el producto
            items = Arrays.copyOf(items, items.length + 1);
            items[items.length - 1] = a;
            return "Se agregó correctamente el producto";
        }
    }

    public double calcularTotal(){
        double total = 0;
        for(OrdenItem i:items){
            total+=i.calcularSubtotal();
        }
        return total;
    }
}