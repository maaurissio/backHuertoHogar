package com.huertohogar.huertohogar.dto.resena;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportarResenaDTO {

    @NotBlank(message = "El motivo es requerido")
    @Pattern(regexp = "^(spam|inapropiado|falso|otro)$", message = "Motivo debe ser: spam, inapropiado, falso u otro")
    private String motivo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
