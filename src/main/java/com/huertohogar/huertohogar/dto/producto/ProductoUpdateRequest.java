package com.huertohogar.huertohogar.dto.producto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {
    
    private String codigo;
    
    private String nombre;
    
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    private String categoria;
    
    private String descripcion;
    
    private String imagen;
    
    private String isActivo;  // Acepta "Activo" o "Inactivo"
}
