package com.huertohogar.huertohogar.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoDTO {
    
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
