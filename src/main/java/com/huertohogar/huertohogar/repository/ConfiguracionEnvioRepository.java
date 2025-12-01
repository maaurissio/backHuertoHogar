package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.ConfiguracionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionEnvioRepository extends JpaRepository<ConfiguracionEnvio, Long> {
}
