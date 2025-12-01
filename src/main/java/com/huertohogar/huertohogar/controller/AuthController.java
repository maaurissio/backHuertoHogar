package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.auth.*;
import com.huertohogar.huertohogar.dto.usuario.UsuarioDTO;
import com.huertohogar.huertohogar.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioDTO usuario = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "usuario", usuario
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Con JWT stateless, el logout se maneja en el cliente eliminando el token
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente"));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<ApiResponse<Void>> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequest request) {
        authService.recuperarPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Código enviado al correo electrónico"));
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<Map<String, Object>> verificarCodigo(@Valid @RequestBody VerificarCodigoRequest request) {
        boolean valido = authService.verificarCodigo(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "valido", valido
        ));
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        authService.restablecerPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada correctamente"));
    }
}
