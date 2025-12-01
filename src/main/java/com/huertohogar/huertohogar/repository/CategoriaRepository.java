package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByValue(String value);
    
    Optional<Categoria> findByCodigo(String codigo);
    
    boolean existsByValue(String value);
    
    boolean existsByCodigo(String codigo);
    
    void deleteByValue(String value);
}
