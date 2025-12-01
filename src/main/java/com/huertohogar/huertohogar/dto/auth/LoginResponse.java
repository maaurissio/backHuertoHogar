package com.huertohogar.huertohogar.dto.auth;

import com.huertohogar.huertohogar.dto.usuario.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private boolean success;
    private String token;
    private UsuarioDTO usuario;
}
