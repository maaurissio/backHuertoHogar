package com.huertohogar.huertohogar.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Rol {
    administrador("administrador"),
    cliente("cliente");

    private final String value;

    Rol(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
