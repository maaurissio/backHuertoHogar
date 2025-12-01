package com.huertohogar.huertohogar.dto.usuario;

import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import com.huertohogar.huertohogar.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private Long id;
    private String email;
    private String usuario;
    private String nombre;
    private String apellido;
    private Rol rol;
    private EstadoActivo isActivo;
    private String telefono;
    private String direccion;
    private String avatar;
    private LocalDateTime fechaRegistro;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .usuario(usuario.getUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol())
                .isActivo(usuario.getIsActivo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .avatar(usuario.getAvatar())
                .fechaRegistro(usuario.getFechaRegistro())
                .build();
    }
}
