package com.huertohogar.huertohogar.dto.categoria;

import com.huertohogar.huertohogar.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    private String value;
    private String label;
    private String codigo;

    public static CategoriaDTO fromEntity(Categoria categoria) {
        return CategoriaDTO.builder()
                .value(categoria.getValue())
                .label(categoria.getLabel())
                .codigo(categoria.getCodigo())
                .build();
    }
}
