package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByUsuario(String usuario);
    
    Optional<Usuario> findByEmailOrUsuario(String email, String usuario);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsuario(String usuario);
}
