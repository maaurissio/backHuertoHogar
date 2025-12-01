package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.producto.ProductoCreateRequest;
import com.huertohogar.huertohogar.dto.producto.ProductoDTO;
import com.huertohogar.huertohogar.dto.producto.ProductoUpdateRequest;
import com.huertohogar.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.huertohogar.model.Categoria;
import com.huertohogar.huertohogar.model.Producto;
import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import com.huertohogar.huertohogar.repository.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    public List<ProductoDTO> findAll(String categoria, String buscar, Boolean activos) {
        List<Producto> productos;

        if (buscar != null && !buscar.isEmpty()) {
            if (activos != null && activos) {
                productos = productoRepository.buscarActivos(buscar, EstadoActivo.Activo);
            } else {
                productos = productoRepository.buscar(buscar);
            }
        } else if (categoria != null && !categoria.isEmpty()) {
            if (activos != null && activos) {
                productos = productoRepository.findByCategoriaAndIsActivo(categoria, EstadoActivo.Activo);
            } else {
                productos = productoRepository.findByCategoria(categoria);
            }
        } else if (activos != null && activos) {
            productos = productoRepository.findByIsActivo(EstadoActivo.Activo);
        } else {
            productos = productoRepository.findAll();
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
        // Generar código automáticamente
        String codigo = generarCodigo(request.getCategoria());

        Producto producto = Producto.builder()
                .codigo(codigo)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .imagen(request.getImagen())
                .categoria(request.getCategoria())
                .peso(request.getPeso())
                .isActivo(EstadoActivo.Activo)
                .build();

        producto = productoRepository.save(producto);
        log.info("Producto creado: {} - {}", producto.getCodigo(), producto.getNombre());

        return ProductoDTO.fromEntity(producto);
    }

    @Transactional
    public ProductoDTO update(Long id, ProductoUpdateRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }
        if (request.getStock() != null) {
            producto.setStock(request.getStock());
        }
        if (request.getImagen() != null) {
            producto.setImagen(request.getImagen());
        }
        if (request.getCategoria() != null) {
            producto.setCategoria(request.getCategoria());
        }
        if (request.getIsActivo() != null) {
            producto.setIsActivo(request.getIsActivo());
        }
        if (request.getPeso() != null) {
            producto.setPeso(request.getPeso());
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

    private String generarCodigo(String categoriaNombre) {
        // Buscar el código de la categoría
        String prefijo = categoriaRepository.findByValue(categoriaNombre)
                .map(Categoria::getCodigo)
                .orElse("XX");

        // Contar productos existentes con ese prefijo
        long count = productoRepository.countByCodigoStartingWith(prefijo);
        
        // Generar nuevo código
        return String.format("%s%03d", prefijo, count + 1);
    }
}
