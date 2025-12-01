package com.huertohogar.huertohogar.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioDTO {
    
    private String direccion;
    private String ciudad;
    private String region;
    private String codigoPostal;
    private String notas;
    private BigDecimal costo;
    private Boolean esGratis;
}
