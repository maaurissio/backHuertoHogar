package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.categoria.CategoriaCreateRequest;
import com.huertohogar.huertohogar.dto.categoria.CategoriaDTO;
import com.huertohogar.huertohogar.exception.BadRequestException;
import com.huertohogar.huertohogar.exception.DuplicateResourceException;
import com.huertohogar.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.huertohogar.model.Categoria;
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
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public List<CategoriaDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDTO create(CategoriaCreateRequest request) {
        // Verificar duplicados
        if (categoriaRepository.existsByValue(request.getNombre())) {
            throw new DuplicateResourceException("La categoría ya existe");
        }
        if (categoriaRepository.existsByCodigo(request.getCodigo().toUpperCase())) {
            throw new DuplicateResourceException("El código de categoría ya existe");
        }

        Categoria categoria = Categoria.builder()
                .value(request.getNombre())
                .label(request.getNombre())
                .codigo(request.getCodigo().toUpperCase())
                .esDefault(false)
                .build();

        categoria = categoriaRepository.save(categoria);
        log.info("Categoría creada: {}", categoria.getValue());

        return CategoriaDTO.fromEntity(categoria);
    }

    @Transactional
    public void delete(String value) {
        Categoria categoria = categoriaRepository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + value));

        // Verificar si es categoría por defecto
        if (categoria.getEsDefault()) {
            throw new BadRequestException("No se puede eliminar una categoría por defecto");
        }

        // Verificar si tiene productos
        if (productoRepository.existsByCategoria(value)) {
            throw new BadRequestException("No se puede eliminar una categoría con productos asociados");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoría eliminada: {}", value);
    }
}
