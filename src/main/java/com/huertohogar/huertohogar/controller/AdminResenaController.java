package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.resena.*;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.model.enums.EstadoResena;
import com.huertohogar.huertohogar.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/resenas")
@RequiredArgsConstructor
public class AdminResenaController {

    private final ResenaService resenaService;

    /**
     * GET /api/admin/resenas
     * Obtener todas las reseñas para moderación (solo admin)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodasResenas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer puntuacionMinima,
            @RequestParam(required = false) Integer puntuacionMaxima) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        
        EstadoResena estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = EstadoResena.fromValue(estado);
            } catch (IllegalArgumentException ignored) {
                // Si el estado no es válido, ignoramos el filtro
            }
        }

        Page<ResenaAdminDTO> resenas = resenaService.obtenerTodasResenas(
                estadoEnum, puntuacionMinima, puntuacionMaxima, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", resenas.getContent());
        response.put("totalElements", resenas.getTotalElements());
        response.put("totalPages", resenas.getTotalPages());
        response.put("number", resenas.getNumber());
        response.put("size", resenas.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/admin/resenas/{resenaId}/moderar
     * Moderar una reseña (aprobar/rechazar)
     */
    @PatchMapping("/{resenaId}/moderar")
    public ResponseEntity<?> moderarResena(
            @PathVariable Long resenaId,
            @Valid @RequestBody ModerarResenaDTO dto,
            @AuthenticationPrincipal Usuario admin) {

        try {
            ModeracionResultadoDTO resultado = resenaService.moderarResena(
                    resenaId, admin.getEmail(), dto);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RESENA_NO_ENCONTRADA")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
