package com.huertohogar.huertohogar.config;

import com.huertohogar.huertohogar.model.Categoria;
import com.huertohogar.huertohogar.model.ConfiguracionEnvio;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import com.huertohogar.huertohogar.model.enums.Rol;
import com.huertohogar.huertohogar.repository.CategoriaRepository;
import com.huertohogar.huertohogar.repository.ConfiguracionEnvioRepository;
import com.huertohogar.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ConfiguracionEnvioRepository configuracionEnvioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initCategorias();
        initAdmin();
        initConfiguracionEnvio();
    }

    private void initCategorias() {
        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = List.of(
                    Categoria.builder()
                            .value("Frutas Frescas")
                            .label("Frutas Frescas")
                            .codigo("FR")
                            .esDefault(true)
                            .build(),
                    Categoria.builder()
                            .value("Verduras Orgánicas")
                            .label("Verduras Orgánicas")
                            .codigo("VR")
                            .esDefault(true)
                            .build(),
                    Categoria.builder()
                            .value("Productos Orgánicos")
                            .label("Productos Orgánicos")
                            .codigo("PO")
                            .esDefault(true)
                            .build(),
                    Categoria.builder()
                            .value("Productos Lácteos")
                            .label("Productos Lácteos")
                            .codigo("LO")
                            .esDefault(true)
                            .build()
            );

            categoriaRepository.saveAll(categorias);
            log.info("Categorías iniciales creadas: {}", categorias.size());
        }
    }

    private void initAdmin() {
        if (!usuarioRepository.existsByEmail("admin@huertohogar.com")) {
            Usuario admin = Usuario.builder()
                    .email("admin@huertohogar.com")
                    .usuario("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombre("Administrador")
                    .apellido("Sistema")
                    .rol(Rol.administrador)
                    .isActivo(EstadoActivo.Activo)
                    .telefono("+56912345678")
                    .direccion("Calle Principal 123")
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador creado: admin@huertohogar.com / admin123");
        }
    }

    private void initConfiguracionEnvio() {
        if (configuracionEnvioRepository.count() == 0) {
            ConfiguracionEnvio config = ConfiguracionEnvio.builder()
                    .costoEnvioBase(BigDecimal.valueOf(5000))
                    .envioGratisDesde(BigDecimal.valueOf(50000))
                    .envioGratisHabilitado(true)
                    .build();

            configuracionEnvioRepository.save(config);
            log.info("Configuración de envío inicial creada");
        }
    }
}
