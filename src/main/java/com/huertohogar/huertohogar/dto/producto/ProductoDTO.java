package com.huertohogar.huertohogar.dto.producto;

import com.huertohogar.huertohogar.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private Long id;
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String categoria;
    private String descripcion;
    private String imagen;
    private String isActivo;

    public static ProductoDTO fromEntity(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria())
                .descripcion(producto.getDescripcion())
                .imagen(producto.getImagen())
                .isActivo(Boolean.TRUE.equals(producto.getIsActivo()) ? "Activo" : "Inactivo")
                .build();
    }
}
