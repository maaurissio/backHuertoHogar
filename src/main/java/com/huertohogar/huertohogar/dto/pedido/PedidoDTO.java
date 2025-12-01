package com.huertohogar.huertohogar.dto.pedido;

import com.huertohogar.huertohogar.model.Pedido;
import com.huertohogar.huertohogar.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    
    private String id;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private Boolean leido;
    private ContactoDTO contacto;
    private EnvioDTO envio;
    private List<ItemPedidoDTO> items;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    public static PedidoDTO fromEntity(Pedido pedido) {
        ContactoDTO contacto = ContactoDTO.builder()
                .nombre(pedido.getContactoNombre())
                .apellido(pedido.getContactoApellido())
                .email(pedido.getContactoEmail())
                .telefono(pedido.getContactoTelefono())
                .build();

        EnvioDTO envio = EnvioDTO.builder()
                .direccion(pedido.getEnvioDireccion())
                .ciudad(pedido.getEnvioCiudad())
                .region(pedido.getEnvioRegion())
                .codigoPostal(pedido.getEnvioCodigoPostal())
                .notas(pedido.getEnvioNotas())
                .costo(pedido.getEnvioCosto())
                .esGratis(pedido.getEnvioEsGratis())
                .build();

        List<ItemPedidoDTO> items = pedido.getItems().stream()
                .map(ItemPedidoDTO::fromEntity)
                .collect(Collectors.toList());

        return PedidoDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getFecha())
                .estado(pedido.getEstado())
                .leido(pedido.getLeido())
                .contacto(contacto)
                .envio(envio)
                .items(items)
                .subtotal(pedido.getSubtotal())
                .costoEnvio(pedido.getCostoEnvio())
                .total(pedido.getTotal())
                .build();
    }
}
