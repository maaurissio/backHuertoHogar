package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.configuracion.ConfiguracionEnvioDTO;
import com.huertohogar.huertohogar.model.ConfiguracionEnvio;
import com.huertohogar.huertohogar.repository.ConfiguracionEnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionEnvioRepository configuracionEnvioRepository;

    public ConfiguracionEnvioDTO getConfiguracionEnvio() {
        ConfiguracionEnvio config = getOrCreateConfiguracion();
        return ConfiguracionEnvioDTO.fromEntity(config);
    }

    public ConfiguracionEnvio getConfiguracionEntity() {
        return getOrCreateConfiguracion();
    }

    @Transactional
    public ConfiguracionEnvioDTO updateConfiguracionEnvio(ConfiguracionEnvioDTO request) {
        ConfiguracionEnvio config = getOrCreateConfiguracion();

        if (request.getCostoEnvioBase() != null) {
            config.setCostoEnvioBase(request.getCostoEnvioBase());
        }
        if (request.getEnvioGratisDesde() != null) {
            config.setEnvioGratisDesde(request.getEnvioGratisDesde());
        }
        if (request.getEnvioGratisHabilitado() != null) {
            config.setEnvioGratisHabilitado(request.getEnvioGratisHabilitado());
        }

        config = configuracionEnvioRepository.save(config);
        log.info("Configuración de envío actualizada");

        return ConfiguracionEnvioDTO.fromEntity(config);
    }

    private ConfiguracionEnvio getOrCreateConfiguracion() {
        return configuracionEnvioRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    ConfiguracionEnvio config = ConfiguracionEnvio.builder()
                            .costoEnvioBase(BigDecimal.valueOf(5000))
                            .envioGratisDesde(BigDecimal.valueOf(50000))
                            .envioGratisHabilitado(true)
                            .build();
                    return configuracionEnvioRepository.save(config);
                });
    }
}
