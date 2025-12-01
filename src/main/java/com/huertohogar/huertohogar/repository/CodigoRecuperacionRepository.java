package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.CodigoRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoRecuperacionRepository extends JpaRepository<CodigoRecuperacion, Long> {
    
    Optional<CodigoRecuperacion> findByEmailAndCodigoAndUsadoFalse(String email, String codigo);
    
    void deleteByEmail(String email);
    
    void deleteByUsadoTrue();
}
