package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.configuracion.ConfiguracionEnvioDTO;
import com.huertohogar.huertohogar.service.ConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping("/envio")
    public ResponseEntity<Map<String, Object>> getConfiguracionEnvio() {
        ConfiguracionEnvioDTO configuracion = configuracionService.getConfiguracionEnvio();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "configuracion", configuracion
        ));
    }

    @PutMapping("/envio")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> updateConfiguracionEnvio(@RequestBody ConfiguracionEnvioDTO request) {
        ConfiguracionEnvioDTO configuracion = configuracionService.updateConfiguracionEnvio(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "configuracion", configuracion
        ));
    }
}
