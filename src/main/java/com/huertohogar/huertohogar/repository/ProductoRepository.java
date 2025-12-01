package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByCategoria(String categoria);
    
    List<Producto> findByIsActivo(Boolean isActivo);
    
    List<Producto> findByCategoriaAndIsActivo(String categoria, Boolean isActivo);
    
    @Query("SELECT p FROM Producto p WHERE p.codigo LIKE :prefijo% ORDER BY p.codigo DESC")
    List<Producto> findByCodigoStartingWithOrderByCodigoDesc(@Param("prefijo") String prefijo);
    
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%'))")
    List<Producto> buscar(@Param("buscar") String buscar);
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%'))) " +
           "AND p.isActivo = :isActivo")
    List<Producto> buscarActivos(@Param("buscar") String buscar, @Param("isActivo") Boolean isActivo);
    
    boolean existsByCategoria(String categoria);
}
