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
public class ModeracionResultadoDTO {
    private Long id;
    private String estado;
    private String moderadoPor;
    private LocalDateTime fechaModeracion;
}
