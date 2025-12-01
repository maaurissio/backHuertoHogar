package com.huertohogar.huertohogar.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoActivo {
    Activo("Activo"),
    Inactivo("Inactivo");

    private final String value;

    EstadoActivo(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
