package com.huertohogar.huertohogar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "configuracion_envio")
public class ConfiguracionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoEnvioBase = BigDecimal.valueOf(5000);

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal envioGratisDesde = BigDecimal.valueOf(50000);

    @Column(nullable = false)
    @Builder.Default
    private Boolean envioGratisHabilitado = true;
}
