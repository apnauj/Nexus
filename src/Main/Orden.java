package Main;

import java.util.UUID;

public class Orden {
    private UUID idPedido;
    private Cliente cliente;
    private String fecha;
    private Enum estado;
    private Enum metodoPago;
    private OrdenItem items[];
}