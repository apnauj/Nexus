package com.nexus.exceptions;

import java.util.UUID;

public class EOrdenNoEncontrada extends Exception {
    public EOrdenNoEncontrada(UUID id) {
        super("No se ha podido encontrar la orden con id: " + id);
    }
}
