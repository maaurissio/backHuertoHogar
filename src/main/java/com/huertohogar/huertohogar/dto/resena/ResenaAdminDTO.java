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
public class ResenaAdminDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long usuarioId;
    private String nombreUsuario;
    private String emailUsuario;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Boolean verificado;
    private String motivoRechazo;
    private String moderadoPor;
    private LocalDateTime fechaModeracion;
}
