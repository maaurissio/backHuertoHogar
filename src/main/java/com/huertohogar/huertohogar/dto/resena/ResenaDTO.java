package com.huertohogar.huertohogar.dto.resena;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaDTO {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String nombreUsuario;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private Boolean verificado;
}
