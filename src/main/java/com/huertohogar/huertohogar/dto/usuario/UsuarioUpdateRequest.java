package com.huertohogar.huertohogar.dto.usuario;

import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import com.huertohogar.huertohogar.model.enums.Rol;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {
    
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    
    @Email(message = "Email inválido")
    private String email;
    
    private Rol rol;
    private EstadoActivo isActivo;
}
