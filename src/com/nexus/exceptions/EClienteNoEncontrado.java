package com.nexus.exceptions;

import com.nexus.model.entities.TipoDocumento;

public class EClienteNoEncontrado extends RuntimeException {
    public EClienteNoEncontrado(TipoDocumento tipoDoc, String numDoc) {
        super("El cliente con documento: " + tipoDoc + " " + numDoc + " no existe" );
    }
}
