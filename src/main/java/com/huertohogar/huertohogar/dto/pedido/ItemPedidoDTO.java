package com.huertohogar.huertohogar.dto.pedido;

import com.huertohogar.huertohogar.model.ItemPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal subtotal;

    public static ItemPedidoDTO fromEntity(ItemPedido item) {
        return ItemPedidoDTO.builder()
                .id(item.getProductoId())
                .nombre(item.getNombre())
                .precio(item.getPrecio())
                .cantidad(item.getCantidad())
                .subtotal(item.getSubtotal())
                .build();
    }
}
