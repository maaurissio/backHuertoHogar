package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.pedido.EstadoUpdateRequest;
import com.huertohogar.huertohogar.dto.pedido.MarcarLeidosRequest;
import com.huertohogar.huertohogar.dto.pedido.PedidoCreateRequest;
import com.huertohogar.huertohogar.dto.pedido.PedidoDTO;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String email,
            @AuthenticationPrincipal Usuario currentUser) {
        List<PedidoDTO> pedidos = pedidoService.findAll(estado, email, currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "pedidos", pedidos
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario currentUser) {
        PedidoDTO pedido = pedidoService.findById(id, currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "pedido", pedido
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody PedidoCreateRequest request,
            @AuthenticationPrincipal Usuario currentUser) {
        PedidoDTO pedido = pedidoService.create(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "pedido", pedido,
                "mensaje", "Pedido creado exitosamente"
        ));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> updateEstado(
            @PathVariable String id,
            @Valid @RequestBody EstadoUpdateRequest request) {
        pedidoService.updateEstado(id, request.getEstado());
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado"));
    }

    @PatchMapping("/marcar-leidos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> marcarLeidos(@Valid @RequestBody MarcarLeidosRequest request) {
        pedidoService.marcarComoLeidos(request.getIds());
        return ResponseEntity.ok(ApiResponse.success("Pedidos marcados como leídos"));
    }
}
