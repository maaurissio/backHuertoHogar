package com.huertohogar.huertohogar.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCreateRequest {
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @NotBlank(message = "El código es requerido")
    @Size(min = 2, max = 3, message = "El código debe tener 2-3 caracteres")
    private String codigo;
}
