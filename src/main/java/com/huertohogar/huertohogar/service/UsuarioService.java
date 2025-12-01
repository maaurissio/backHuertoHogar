package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.usuario.CambiarPasswordRequest;
import com.huertohogar.huertohogar.dto.usuario.UsuarioDTO;
import com.huertohogar.huertohogar.dto.usuario.UsuarioUpdateRequest;
import com.huertohogar.huertohogar.exception.BadRequestException;
import com.huertohogar.huertohogar.exception.ForbiddenException;
import com.huertohogar.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.huertohogar.model.Usuario;
import com.huertohogar.huertohogar.model.enums.Rol;
import com.huertohogar.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO update(Long id, UsuarioUpdateRequest request, Usuario currentUser) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        // Solo admin puede modificar otros usuarios o cambiar rol/estado
        boolean isAdmin = currentUser.getRol() == Rol.administrador;
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdmin && !isSameUser) {
            throw new ForbiddenException("No tienes permisos para modificar este usuario");
        }

        // Actualizar campos básicos
        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email ya en uso");
            }
            usuario.setEmail(request.getEmail());
        }

        // Solo admin puede cambiar rol y estado
        if (isAdmin) {
            if (request.getRol() != null) {
                usuario.setRol(request.getRol());
            }
            if (request.getIsActivo() != null) {
                usuario.setIsActivo(request.getIsActivo());
            }
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", usuario.getId());
        
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public void delete(Long id, String password, Usuario currentUser) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        boolean isAdmin = currentUser.getRol() == Rol.administrador;
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdmin && !isSameUser) {
            throw new ForbiddenException("No tienes permisos para eliminar este usuario");
        }

        // Si es auto-eliminación, verificar contraseña
        if (isSameUser && !isAdmin) {
            if (password == null || !passwordEncoder.matches(password, usuario.getPassword())) {
                throw new BadRequestException("Contraseña incorrecta");
            }
        }

        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: {}", id);
    }

    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request, Usuario currentUser) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        boolean isAdmin = currentUser.getRol() == Rol.administrador;
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdmin && !isSameUser) {
            throw new ForbiddenException("No tienes permisos para cambiar la contraseña de este usuario");
        }

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadRequestException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
        
        log.info("Contraseña cambiada para usuario: {}", id);
    }
}
