package com.nexus.exceptions;

import com.nexus.model.enums.TipoDocumento;

public class EClienteYaExiste extends Exception {
    public EClienteYaExiste(TipoDocumento tipoDoc, String numDoc) {
        super("El cliente con documento " + tipoDoc + " " + numDoc + " ya existe.");
    }
}
