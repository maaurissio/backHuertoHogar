package com.huertohogar.huertohogar.dto.resena;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerarResenaDTO {

    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "^(pendiente|aprobado|rechazado)$", message = "Estado debe ser: pendiente, aprobado o rechazado")
    private String estado;

    private String motivoRechazo;
}
