package com.huertohogar.huertohogar.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUpdateRequest {
    
    @NotBlank(message = "El estado es requerido")
    private String estado;
}
