package com.huertohogar.huertohogar.dto.producto;

import com.huertohogar.huertohogar.model.enums.EstadoActivo;
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
    
    private String nombre;
    private String descripcion;
    
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    private String imagen;
    private String categoria;
    private EstadoActivo isActivo;
    private String peso;
}
