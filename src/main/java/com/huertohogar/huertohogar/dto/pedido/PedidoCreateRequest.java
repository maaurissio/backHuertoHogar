package com.huertohogar.huertohogar.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateRequest {
    
    @Valid
    private ContactoRequest contacto;
    
    @Valid
    private EnvioRequest envio;
    
    @NotEmpty(message = "Los items son requeridos")
    @Valid
    private List<ItemPedidoRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoRequest {
        @NotBlank(message = "El nombre es requerido")
        private String nombre;
        
        @NotBlank(message = "El apellido es requerido")
        private String apellido;
        
        @NotBlank(message = "El email es requerido")
        @Email(message = "Email inválido")
        private String email;
        
        @NotBlank(message = "El teléfono es requerido")
        private String telefono;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvioRequest {
        @NotBlank(message = "La dirección es requerida")
        private String direccion;
        
        @NotBlank(message = "La ciudad es requerida")
        private String ciudad;
        
        @NotBlank(message = "La región es requerida")
        private String region;
        
        private String codigoPostal;
        private String notas;
    }
}
