package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Producto;
import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByCategoria(String categoria);
    
    List<Producto> findByIsActivo(EstadoActivo isActivo);
    
    List<Producto> findByCategoriaAndIsActivo(String categoria, EstadoActivo isActivo);
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    List<Producto> buscar(@Param("buscar") String buscar);
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :buscar, '%'))) " +
           "AND p.isActivo = :isActivo")
    List<Producto> buscarActivos(@Param("buscar") String buscar, @Param("isActivo") EstadoActivo isActivo);
    
    Optional<Producto> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.codigo LIKE :prefijo%")
    long countByCodigoStartingWith(@Param("prefijo") String prefijo);
    
    boolean existsByCategoria(String categoria);
}
