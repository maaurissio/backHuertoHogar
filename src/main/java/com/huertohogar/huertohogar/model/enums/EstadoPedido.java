package com.huertohogar.huertohogar.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoPedido {
    confirmado("confirmado"),
    en_preparacion("en-preparacion"),
    enviado("enviado"),
    entregado("entregado"),
    cancelado("cancelado");

    private final String value;

    EstadoPedido(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static EstadoPedido fromValue(String value) {
        for (EstadoPedido estado : EstadoPedido.values()) {
            if (estado.value.equals(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + value);
    }
}
