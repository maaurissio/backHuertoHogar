package com.huertohogar.huertohogar.controller;

import com.huertohogar.huertohogar.dto.ApiResponse;
import com.huertohogar.huertohogar.dto.producto.ProductoCreateRequest;
import com.huertohogar.huertohogar.dto.producto.ProductoDTO;
import com.huertohogar.huertohogar.dto.producto.ProductoUpdateRequest;
import com.huertohogar.huertohogar.dto.producto.StockUpdateRequest;
import com.huertohogar.huertohogar.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false, defaultValue = "false") Boolean soloActivos) {
        List<ProductoDTO> productos = productoService.findAll(categoria, buscar, soloActivos);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        ProductoDTO producto = productoService.findById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "producto", producto
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductoCreateRequest request) {
        ProductoDTO producto = productoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "producto", producto
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateRequest request) {
        ProductoDTO producto = productoService.update(id, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "producto", producto
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado"));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        ProductoDTO producto = productoService.updateStock(id, request.getStock());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "producto", producto
        ));
    }
}
