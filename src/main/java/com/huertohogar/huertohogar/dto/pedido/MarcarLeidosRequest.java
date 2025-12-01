package com.huertohogar.huertohogar.dto.pedido;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarLeidosRequest {
    
    @NotEmpty(message = "Los ids son requeridos")
    private List<String> ids;
}
