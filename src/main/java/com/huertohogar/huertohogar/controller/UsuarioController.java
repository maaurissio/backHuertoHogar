package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.usuario.CambiarPasswordRequest;
import com.huertohogar.huertohogar.dto.usuario.DeleteUsuarioRequest;
import com.huertohogar.huertohogar.dto.usuario.UsuarioDTO;
import com.huertohogar.huertohogar.dto.usuario.UsuarioUpdateRequest;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> getAll() {
        List<UsuarioDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "usuarios", usuarios
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario currentUser) {
        UsuarioDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "usuario", usuario
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            @AuthenticationPrincipal Usuario currentUser) {
        UsuarioDTO usuario = usuarioService.update(id, request, currentUser);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "usuario", usuario
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestBody(required = false) DeleteUsuarioRequest request,
            @AuthenticationPrincipal Usuario currentUser) {
        String password = request != null ? request.getPassword() : null;
        usuarioService.delete(id, password, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado correctamente"));
    }

    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request,
            @AuthenticationPrincipal Usuario currentUser) {
        usuarioService.cambiarPassword(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada"));
    }
}
