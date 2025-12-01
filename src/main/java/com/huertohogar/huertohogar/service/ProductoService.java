package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.producto.ProductoCreateRequest;
import com.huertohogar.huertohogar.dto.producto.ProductoDTO;
import com.huertohogar.huertohogar.dto.producto.ProductoUpdateRequest;
import com.huertohogar.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.huertohogar.model.Producto;
import com.huertohogar.huertohogar.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoDTO> findAll(String categoria, String buscar, Boolean soloActivos) {
        List<Producto> productos;

        if (buscar != null && !buscar.isEmpty()) {
            if (Boolean.TRUE.equals(soloActivos)) {
                productos = productoRepository.buscarActivos(buscar, true);
            } else {
                productos = productoRepository.buscar(buscar);
            }
        } else if (categoria != null && !categoria.isEmpty()) {
            if (Boolean.TRUE.equals(soloActivos)) {
                productos = productoRepository.findByCategoriaAndIsActivo(categoria, true);
            } else {
                productos = productoRepository.findByCategoria(categoria);
            }
        } else {
            if (Boolean.TRUE.equals(soloActivos)) {
                productos = productoRepository.findByIsActivo(true);
            } else {
                productos = productoRepository.findAll();
            }
        }

        return productos.stream()
                .map(ProductoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return ProductoDTO.fromEntity(producto);
    }

    @Transactional
    public ProductoDTO create(ProductoCreateRequest request) {
        // Generar código automáticamente basado en la categoría
        String codigo = generarCodigo(request.getCategoria());
        
        Producto producto = Producto.builder()
                .codigo(codigo)
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(request.getCategoria())
                .descripcion(request.getDescripcion())
                .imagen(request.getImagen())
                .build();

        producto = productoRepository.save(producto);
        log.info("Producto creado: {} - {}", producto.getId(), producto.getNombre());

        return ProductoDTO.fromEntity(producto);
    }

    @Transactional
    public ProductoDTO update(Long id, ProductoUpdateRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (request.getCodigo() != null) {
            producto.setCodigo(request.getCodigo());
        }
        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }
        if (request.getStock() != null) {
            producto.setStock(request.getStock());
        }
        if (request.getCategoria() != null) {
            producto.setCategoria(request.getCategoria());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getImagen() != null) {
            producto.setImagen(request.getImagen());
        }
        if (request.getIsActivo() != null) {
            // Convertir "Activo"/"Inactivo" a Boolean
            boolean activo = "Activo".equalsIgnoreCase(request.getIsActivo()) 
                          || "true".equalsIgnoreCase(request.getIsActivo())
                          || "1".equals(request.getIsActivo());
            producto.setIsActivo(activo);
        }

        producto = productoRepository.save(producto);
        log.info("Producto actualizado: {}", producto.getId());

        return ProductoDTO.fromEntity(producto);
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        
        productoRepository.delete(producto);
        log.info("Producto eliminado: {}", id);
    }

    @Transactional
    public ProductoDTO updateStock(Long id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        producto.setStock(nuevoStock);
        producto = productoRepository.save(producto);
        
        log.info("Stock actualizado para producto {}: {}", id, nuevoStock);
        return ProductoDTO.fromEntity(producto);
    }

    /**
     * Genera un código automático basado en la categoría.
     * Ejemplo: "Frutas Frescas" -> "FR001", "FR002", etc.
     *          "Verduras" -> "VE001", "VE002", etc.
     *          "Hierbas" -> "HI001", etc.
     */
    private String generarCodigo(String categoria) {
        String prefijo = obtenerPrefijo(categoria);
        
        // Buscar el último código con este prefijo
        List<Producto> productos = productoRepository.findByCodigoStartingWithOrderByCodigoDesc(prefijo);
        
        int siguienteNumero = 1;
        if (!productos.isEmpty()) {
            String ultimoCodigo = productos.get(0).getCodigo();
            // Extraer el número del código (ej: "FR005" -> 5)
            String numeroStr = ultimoCodigo.substring(prefijo.length());
            try {
                siguienteNumero = Integer.parseInt(numeroStr) + 1;
            } catch (NumberFormatException e) {
                siguienteNumero = 1;
            }
        }
        
        // Formatear con 3 dígitos (001, 002, etc.)
        return String.format("%s%03d", prefijo, siguienteNumero);
    }

    /**
     * Obtiene el prefijo de 2 letras basado en la categoría.
     */
    private String obtenerPrefijo(String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            return "PR"; // Producto genérico
        }
        
        String categoriaLower = categoria.toLowerCase().trim();
        
        // Mapeo de categorías a prefijos
        if (categoriaLower.contains("fruta")) {
            return "FR";
        } else if (categoriaLower.contains("verdura")) {
            return "VE";
        } else if (categoriaLower.contains("hierba") || categoriaLower.contains("aromatica")) {
            return "HI";
        } else if (categoriaLower.contains("semilla")) {
            return "SE";
        } else if (categoriaLower.contains("fertilizante") || categoriaLower.contains("abono")) {
            return "FE";
        } else if (categoriaLower.contains("herramienta")) {
            return "HE";
        } else if (categoriaLower.contains("maceta") || categoriaLower.contains("contenedor")) {
            return "MA";
        } else if (categoriaLower.contains("riego")) {
            return "RI";
        } else if (categoriaLower.contains("planta")) {
            return "PL";
        } else {
            // Usar las primeras 2 letras de la categoría
            String sinEspacios = categoria.replaceAll("\\s+", "").toUpperCase();
            return sinEspacios.length() >= 2 ? sinEspacios.substring(0, 2) : "PR";
        }
    }
}
