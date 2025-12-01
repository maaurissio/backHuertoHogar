package com.huertohogar.huertohogar.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MotivoReporte {
    spam("spam"),
    inapropiado("inapropiado"),
    falso("falso"),
    otro("otro");

    private final String value;

    MotivoReporte(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static MotivoReporte fromValue(String value) {
        for (MotivoReporte motivo : MotivoReporte.values()) {
            if (motivo.value.equals(value)) {
                return motivo;
            }
        }
        throw new IllegalArgumentException("Motivo de reporte no válido: " + value);
    }
}
