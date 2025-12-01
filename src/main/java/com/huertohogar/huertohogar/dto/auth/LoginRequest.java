package com.huertohogar.huertohogar.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "El email o usuario es requerido")
    private String emailOUsuario;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
