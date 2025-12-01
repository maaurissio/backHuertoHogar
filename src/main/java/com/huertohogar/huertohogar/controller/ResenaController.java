package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.resena.*;
import com.huertohogar.huertohogar.model.Usuario;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    /**
     * GET /api/productos/{productoId}/resenas
     * Obtener reseñas de un producto (público)
     */
    @GetMapping("/productos/{productoId}/resenas")
    public ResponseEntity<Map<String, Object>> obtenerResenasProducto(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Mapear nombres de campos
        if (sortField.equals("fecha")) {
            sortField = "fechaCreacion";
        } else if (sortField.equals("puntuacion")) {
            sortField = "puntuacion";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<ResenaDTO> resenas = resenaService.obtenerResenasPorProducto(productoId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", resenas.getContent());
        response.put("totalElements", resenas.getTotalElements());
        response.put("totalPages", resenas.getTotalPages());
        response.put("number", resenas.getNumber());
        response.put("size", resenas.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/productos/{productoId}/resenas/resumen
     * Obtener resumen de reseñas de un producto (público)
     */
    @GetMapping("/productos/{productoId}/resenas/resumen")
    public ResponseEntity<ResumenResenasDTO> obtenerResumenResenas(@PathVariable Long productoId) {
        ResumenResenasDTO resumen = resenaService.obtenerResumenResenas(productoId);
        return ResponseEntity.ok(resumen);
    }

    /**
     * POST /api/productos/{productoId}/resenas
     * Crear una reseña (autenticado, debe haber comprado el producto)
     */
    @PostMapping("/productos/{productoId}/resenas")
    public ResponseEntity<?> crearResena(
            @PathVariable Long productoId,
            @Valid @RequestBody CrearResenaDTO dto,
            @AuthenticationPrincipal Usuario usuario) {

        try {
            ResenaDTO resena = resenaService.crearResena(productoId, usuario.getId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resena);
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    /**
     * PUT /api/productos/{productoId}/resenas/{resenaId}
     * Actualizar una reseña (solo el propietario)
     */
    @PutMapping("/productos/{productoId}/resenas/{resenaId}")
    public ResponseEntity<?> actualizarResena(
            @PathVariable Long productoId,
            @PathVariable Long resenaId,
            @Valid @RequestBody ActualizarResenaDTO dto,
            @AuthenticationPrincipal Usuario usuario) {

        try {
            ResenaDTO resena = resenaService.actualizarResena(productoId, resenaId, usuario.getId(), dto);
            return ResponseEntity.ok(resena);
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    /**
     * DELETE /api/productos/{productoId}/resenas/{resenaId}
     * Eliminar una reseña (propietario o admin)
     */
    @DeleteMapping("/productos/{productoId}/resenas/{resenaId}")
    public ResponseEntity<?> eliminarResena(
            @PathVariable Long productoId,
            @PathVariable Long resenaId,
            @AuthenticationPrincipal Usuario usuario) {

        try {
            boolean esAdmin = usuario.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
            
            resenaService.eliminarResena(productoId, resenaId, usuario.getId(), esAdmin);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    /**
     * POST /api/productos/{productoId}/resenas/{resenaId}/reportar
     * Reportar una reseña (autenticado)
     */
    @PostMapping("/productos/{productoId}/resenas/{resenaId}/reportar")
    public ResponseEntity<?> reportarResena(
            @PathVariable Long productoId,
            @PathVariable Long resenaId,
            @Valid @RequestBody ReportarResenaDTO dto,
            @AuthenticationPrincipal Usuario usuario) {

        try {
            Long reporteId = resenaService.reportarResena(productoId, resenaId, usuario.getId(), dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reporte enviado exitosamente");
            response.put("reporteId", reporteId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    /**
     * GET /api/usuarios/{usuarioId}/resenas
     * Obtener reseñas de un usuario (autenticado)
     */
    @GetMapping("/usuarios/{usuarioId}/resenas")
    public ResponseEntity<?> obtenerResenasUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Usuario usuario) {

        // Verificar que el usuario solo puede ver sus propias reseñas (o es admin)
        boolean esAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        
        if (!esAdmin && !usuario.getId().equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("SIN_PERMISOS"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<ResenaUsuarioDTO> resenas = resenaService.obtenerResenasPorUsuario(usuarioId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", resenas.getContent());
        response.put("totalElements", resenas.getTotalElements());
        response.put("totalPages", resenas.getTotalPages());
        response.put("number", resenas.getNumber());
        response.put("size", resenas.getSize());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ApiResponse> handleError(RuntimeException e) {
        String message = e.getMessage();
        
        return switch (message) {
            case "PRODUCTO_NO_ENCONTRADO", "RESENA_NO_ENCONTRADA", "USUARIO_NO_ENCONTRADO" -> 
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(message));
            case "NO_HA_COMPRADO_PRODUCTO", "SIN_PERMISOS" -> 
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(message));
            case "RESENA_DUPLICADA", "YA_REPORTADA" -> 
                    ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(message));
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
        };
    }
}
