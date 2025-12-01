package com.huertohogar.huertohogar.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoResena {
    pendiente("pendiente"),
    aprobado("aprobado"),
    rechazado("rechazado");

    private final String value;

    EstadoResena(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static EstadoResena fromValue(String value) {
        for (EstadoResena estado : EstadoResena.values()) {
            if (estado.value.equals(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de reseña no válido: " + value);
    }
}
