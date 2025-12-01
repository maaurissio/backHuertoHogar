package com.huertohogar.huertohogar.config;

import com.huertohogar.huertohogar.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas de autenticación
                .requestMatchers("/api/auth/**").permitAll()
                
                // Productos - lectura pública (incluye reseñas GET)
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                
                // Reseñas - crear, editar, eliminar, reportar requiere autenticación
                .requestMatchers(HttpMethod.POST, "/api/productos/*/resenas").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/productos/*/resenas/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*/resenas/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/productos/*/resenas/*/reportar").authenticated()
                
                // Admin reseñas - solo administrador
                .requestMatchers("/api/admin/resenas/**").hasRole("ADMINISTRADOR")
                
                // Categorías - lectura pública
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                
                // Configuración de envío - lectura pública
                .requestMatchers(HttpMethod.GET, "/api/configuracion/envio").permitAll()
                
                // Pedidos - crear pedido es público (para invitados)
                .requestMatchers(HttpMethod.POST, "/api/pedidos").permitAll()
                
                // H2 Console (solo desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Swagger/OpenAPI (si se agrega)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Usuarios - solo admin puede ver todos
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMINISTRADOR")
                
                // Productos, categorías - modificación solo admin (excepto reseñas manejadas arriba)
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/productos/*").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/productos/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*").hasRole("ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMINISTRADOR")
                
                // Configuración de envío - modificación solo admin
                .requestMatchers(HttpMethod.PUT, "/api/configuracion/**").hasRole("ADMINISTRADOR")
                
                // Pedidos - estado y marcar leídos solo admin
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/marcar-leidos").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/estado").hasRole("ADMINISTRADOR")
                
                // Upload - requiere autenticación
                .requestMatchers("/api/upload/**").authenticated()
                
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Para H2 Console
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
