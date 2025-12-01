package com.huertohogar.huertohogar.dto.configuracion;

import com.huertohogar.huertohogar.model.ConfiguracionEnvio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionEnvioDTO {
    
    private BigDecimal costoEnvioBase;
    private BigDecimal envioGratisDesde;
    private Boolean envioGratisHabilitado;

    public static ConfiguracionEnvioDTO fromEntity(ConfiguracionEnvio config) {
        return ConfiguracionEnvioDTO.builder()
                .costoEnvioBase(config.getCostoEnvioBase())
                .envioGratisDesde(config.getEnvioGratisDesde())
                .envioGratisHabilitado(config.getEnvioGratisHabilitado())
                .build();
    }
}
