package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Resena;
import com.huertohogar.huertohogar.model.enums.EstadoResena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Encontrar reseñas por producto (solo aprobadas para vista pública)
    Page<Resena> findByProductoIdAndEstado(Long productoId, EstadoResena estado, Pageable pageable);

    // Encontrar todas las reseñas de un producto
    Page<Resena> findByProductoId(Long productoId, Pageable pageable);

    // Encontrar reseñas por usuario
    Page<Resena> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Verificar si un usuario ya reseñó un producto
    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);

    // Encontrar reseña específica de usuario para producto
    Optional<Resena> findByProductoIdAndUsuarioId(Long productoId, Long usuarioId);

    // Encontrar reseña por ID y producto
    Optional<Resena> findByIdAndProductoId(Long id, Long productoId);

    // Calcular promedio de calificación de un producto (solo aprobadas)
    @Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.producto.id = :productoId AND r.estado = 'aprobado'")
    Double calcularPromedioCalificacion(@Param("productoId") Long productoId);

    // Contar total de reseñas de un producto (solo aprobadas)
    long countByProductoIdAndEstado(Long productoId, EstadoResena estado);

    // Contar reseñas por puntuación para un producto (solo aprobadas)
    @Query("SELECT r.puntuacion, COUNT(r) FROM Resena r WHERE r.producto.id = :productoId AND r.estado = 'aprobado' GROUP BY r.puntuacion")
    List<Object[]> contarPorPuntuacion(@Param("productoId") Long productoId);

    // Para admin: filtrar por estado
    Page<Resena> findByEstado(EstadoResena estado, Pageable pageable);

    // Para admin: filtrar por rango de puntuación
    @Query("SELECT r FROM Resena r WHERE r.puntuacion >= :minPuntuacion AND r.puntuacion <= :maxPuntuacion")
    Page<Resena> findByPuntuacionBetween(@Param("minPuntuacion") Integer minPuntuacion, 
                                          @Param("maxPuntuacion") Integer maxPuntuacion, 
                                          Pageable pageable);

    // Para admin: filtrar por estado y rango de puntuación
    @Query("SELECT r FROM Resena r WHERE r.estado = :estado AND r.puntuacion >= :minPuntuacion AND r.puntuacion <= :maxPuntuacion")
    Page<Resena> findByEstadoAndPuntuacionBetween(@Param("estado") EstadoResena estado,
                                                   @Param("minPuntuacion") Integer minPuntuacion,
                                                   @Param("maxPuntuacion") Integer maxPuntuacion,
                                                   Pageable pageable);

    // Contar reportes de una reseña
    @Query("SELECT COUNT(rr) FROM ReporteResena rr WHERE rr.resena.id = :resenaId")
    long contarReportes(@Param("resenaId") Long resenaId);
}
