package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.auth.*;
import com.huertohogar.huertohogar.dto.usuario.UsuarioDTO;
import com.huertohogar.huertohogar.exception.BadRequestException;
import com.huertohogar.huertohogar.exception.DuplicateResourceException;
import com.huertohogar.huertohogar.exception.UnauthorizedException;
import com.huertohogar.huertohogar.model.CodigoRecuperacion;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.model.enums.EstadoActivo;
import com.huertohogar.huertohogar.model.enums.Rol;
import com.huertohogar.huertohogar.repository.CodigoRecuperacionRepository;
import com.huertohogar.huertohogar.repository.UsuarioRepository;
import com.huertohogar.huertohogar.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        // Buscar usuario por email o username
        Usuario usuario = usuarioRepository.findByEmailOrUsuario(
                request.getEmailOUsuario(), 
                request.getEmailOUsuario()
        ).orElseThrow(() -> new UnauthorizedException("Cuenta no encontrada", "CUENTA_INEXISTENTE"));

        // Verificar si la cuenta está activa
        if (usuario.getIsActivo() == EstadoActivo.Inactivo) {
            throw new UnauthorizedException("Cuenta inactiva", "CUENTA_INACTIVA");
        }

        // Autenticar
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuario.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Credenciales incorrectas", "CREDENCIALES_INCORRECTAS");
        }

        // Generar token
        String token = jwtService.generateToken(usuario);

        return LoginResponse.builder()
                .success(true)
                .token(token)
                .usuario(UsuarioDTO.fromEntity(usuario))
                .build();
    }

    @Transactional
    public UsuarioDTO register(RegisterRequest request) {
        // Verificar email duplicado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado", "EMAIL_DUPLICADO");
        }

        // Verificar usuario duplicado
        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new DuplicateResourceException("El nombre de usuario ya existe", "USUARIO_DUPLICADO");
        }

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .usuario(request.getUsuario())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .rol(Rol.cliente)
                .isActivo(EstadoActivo.Activo)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario registrado: {}", usuario.getEmail());

        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public void recuperarPassword(RecuperarPasswordRequest request) {
        // Verificar que el email existe
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email no encontrado"));

        // Eliminar códigos anteriores
        codigoRecuperacionRepository.deleteByEmail(request.getEmail());

        // Generar código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // Guardar código
        CodigoRecuperacion codigoRecuperacion = CodigoRecuperacion.builder()
                .email(request.getEmail())
                .codigo(codigo)
                .expiracion(LocalDateTime.now().plusMinutes(15))
                .usado(false)
                .build();

        codigoRecuperacionRepository.save(codigoRecuperacion);

        // Enviar email
        try {
            emailService.enviarCodigoRecuperacion(request.getEmail(), usuario.getNombre(), codigo);
        } catch (Exception e) {
            log.error("Error enviando email de recuperación: {}", e.getMessage());
            // No lanzar excepción para no revelar si el email existe
        }

        log.info("Código de recuperación generado para: {}", request.getEmail());
    }

    public boolean verificarCodigo(VerificarCodigoRequest request) {
        return codigoRecuperacionRepository
                .findByEmailAndCodigoAndUsadoFalse(request.getEmail(), request.getCodigo())
                .map(codigo -> !codigo.isExpirado())
                .orElse(false);
    }

    @Transactional
    public void restablecerPassword(RestablecerPasswordRequest request) {
        // Verificar código
        CodigoRecuperacion codigoRecuperacion = codigoRecuperacionRepository
                .findByEmailAndCodigoAndUsadoFalse(request.getEmail(), request.getCodigo())
                .orElseThrow(() -> new BadRequestException("Código inválido o expirado"));

        if (codigoRecuperacion.isExpirado()) {
            throw new BadRequestException("Código expirado");
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        // Marcar código como usado
        codigoRecuperacion.setUsado(true);
        codigoRecuperacionRepository.save(codigoRecuperacion);

        log.info("Contraseña restablecida para: {}", request.getEmail());
    }
}
