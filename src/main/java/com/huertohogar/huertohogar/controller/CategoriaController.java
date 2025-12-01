package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.categoria.CategoriaCreateRequest;
import com.huertohogar.huertohogar.dto.categoria.CategoriaDTO;
import com.huertohogar.huertohogar.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<CategoriaDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "categorias", categorias
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CategoriaCreateRequest request) {
        CategoriaDTO categoria = categoriaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "categoria", categoria
        ));
    }

    @DeleteMapping("/{value}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String value) {
        categoriaService.delete(value);
        return ResponseEntity.ok(ApiResponse.success("Categoría eliminada"));
    }
}
