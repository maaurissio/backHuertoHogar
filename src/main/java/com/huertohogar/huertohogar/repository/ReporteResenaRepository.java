package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.ReporteResena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteResenaRepository extends JpaRepository<ReporteResena, Long> {

    // Verificar si un usuario ya reportó una reseña
    boolean existsByResenaIdAndUsuarioId(Long resenaId, Long usuarioId);

    // Contar reportes de una reseña
    long countByResenaId(Long resenaId);

    // Eliminar reportes de una reseña (cuando se elimina la reseña)
    void deleteByResenaId(Long resenaId);
}
