package com.huertohogar.huertohogar.dto.resena;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenResenasDTO {
    private Long productoId;
    private Double promedioCalificacion;
    private Integer totalResenas;
    private Map<Integer, Integer> distribucion;
}
