package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.resena.*;
import com.huertohogar.huertohogar.model.*;
import com.huertohogar.huertohogar.model.enums.EstadoPedido;
import com.huertohogar.huertohogar.model.enums.EstadoResena;
import com.huertohogar.huertohogar.model.enums.MotivoReporte;
import com.huertohogar.huertohogar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ReporteResenaRepository reporteResenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    private static final int MAX_REPORTES_AUTO_PENDIENTE = 3;

    /**
     * Obtener reseñas de un producto (públicas, solo aprobadas)
     */
    public Page<ResenaDTO> obtenerResenasPorProducto(Long productoId, Pageable pageable) {
        return resenaRepository.findByProductoIdAndEstado(productoId, EstadoResena.aprobado, pageable)
                .map(this::convertToResenaDTO);
    }

    /**
     * Obtener resumen de reseñas de un producto
     */
    public ResumenResenasDTO obtenerResumenResenas(Long productoId) {
        Double promedio = resenaRepository.calcularPromedioCalificacion(productoId);
        long total = resenaRepository.countByProductoIdAndEstado(productoId, EstadoResena.aprobado);
        List<Object[]> distribucionRaw = resenaRepository.contarPorPuntuacion(productoId);

        Map<Integer, Integer> distribucion = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribucion.put(i, 0);
        }
        for (Object[] row : distribucionRaw) {
            Integer puntuacion = (Integer) row[0];
            Long count = (Long) row[1];
            distribucion.put(puntuacion, count.intValue());
        }

        return ResumenResenasDTO.builder()
                .productoId(productoId)
                .promedioCalificacion(promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0)
                .totalResenas((int) total)
                .distribucion(distribucion)
                .build();
    }

    /**
     * Obtener reseñas de un usuario
     */
    public Page<ResenaUsuarioDTO> obtenerResenasPorUsuario(Long usuarioId, Pageable pageable) {
        return resenaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToResenaUsuarioDTO);
    }

    /**
     * Crear una reseña
     */
    @Transactional
    public ResenaDTO crearResena(Long productoId, Long usuarioId, CrearResenaDTO dto) {
        // Verificar que el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("PRODUCTO_NO_ENCONTRADO"));

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("USUARIO_NO_ENCONTRADO"));

        // Verificar que no exista ya una reseña de este usuario para este producto
        if (resenaRepository.existsByProductoIdAndUsuarioId(productoId, usuarioId)) {
            throw new RuntimeException("RESENA_DUPLICADA");
        }

        // Verificar si el usuario ha comprado el producto (verificado)
        boolean verificado = verificarCompra(usuarioId, productoId);

        // Si no ha comprado, podemos rechazar o permitir sin verificado
        // Según el spec, debería dar error 403 si no ha comprado
        if (!verificado) {
            throw new RuntimeException("NO_HA_COMPRADO_PRODUCTO");
        }

        Resena resena = Resena.builder()
                .producto(producto)
                .usuario(usuario)
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .verificado(verificado)
                .estado(EstadoResena.aprobado)
                .build();

        Resena saved = resenaRepository.save(resena);
        return convertToResenaDTO(saved);
    }

    /**
     * Actualizar una reseña (solo el propietario)
     */
    @Transactional
    public ResenaDTO actualizarResena(Long productoId, Long resenaId, Long usuarioId, ActualizarResenaDTO dto) {
        Resena resena = resenaRepository.findByIdAndProductoId(resenaId, productoId)
                .orElseThrow(() -> new RuntimeException("RESENA_NO_ENCONTRADA"));

        // Verificar que el usuario es el propietario
        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("SIN_PERMISOS");
        }

        resena.setPuntuacion(dto.getPuntuacion());
        resena.setComentario(dto.getComentario());

        Resena updated = resenaRepository.save(resena);
        return convertToResenaDTO(updated);
    }

    /**
     * Eliminar una reseña (propietario o admin)
     */
    @Transactional
    public void eliminarResena(Long productoId, Long resenaId, Long usuarioId, boolean esAdmin) {
        Resena resena = resenaRepository.findByIdAndProductoId(resenaId, productoId)
                .orElseThrow(() -> new RuntimeException("RESENA_NO_ENCONTRADA"));

        // Verificar permisos
        if (!esAdmin && !resena.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("SIN_PERMISOS");
        }

        // Eliminar reportes asociados
        reporteResenaRepository.deleteByResenaId(resenaId);

        // Eliminar la reseña
        resenaRepository.delete(resena);
    }

    /**
     * Reportar una reseña
     */
    @Transactional
    public Long reportarResena(Long productoId, Long resenaId, Long usuarioId, ReportarResenaDTO dto) {
        Resena resena = resenaRepository.findByIdAndProductoId(resenaId, productoId)
                .orElseThrow(() -> new RuntimeException("RESENA_NO_ENCONTRADA"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("USUARIO_NO_ENCONTRADO"));

        // Verificar que el usuario no haya reportado ya esta reseña
        if (reporteResenaRepository.existsByResenaIdAndUsuarioId(resenaId, usuarioId)) {
            throw new RuntimeException("YA_REPORTADA");
        }

        ReporteResena reporte = ReporteResena.builder()
                .resena(resena)
                .usuario(usuario)
                .motivo(MotivoReporte.fromValue(dto.getMotivo()))
                .descripcion(dto.getDescripcion())
                .build();

        ReporteResena saved = reporteResenaRepository.save(reporte);

        // Si la reseña tiene más de 3 reportes, marcarla como pendiente
        long totalReportes = reporteResenaRepository.countByResenaId(resenaId);
        if (totalReportes >= MAX_REPORTES_AUTO_PENDIENTE && resena.getEstado() == EstadoResena.aprobado) {
            resena.setEstado(EstadoResena.pendiente);
            resenaRepository.save(resena);
        }

        return saved.getId();
    }

    // ==================== ADMIN ====================

    /**
     * Obtener todas las reseñas (admin)
     */
    public Page<ResenaAdminDTO> obtenerTodasResenas(EstadoResena estado, Integer minPuntuacion, 
                                                     Integer maxPuntuacion, Pageable pageable) {
        Page<Resena> resenas;

        if (estado != null && minPuntuacion != null && maxPuntuacion != null) {
            resenas = resenaRepository.findByEstadoAndPuntuacionBetween(estado, minPuntuacion, maxPuntuacion, pageable);
        } else if (estado != null) {
            resenas = resenaRepository.findByEstado(estado, pageable);
        } else if (minPuntuacion != null && maxPuntuacion != null) {
            resenas = resenaRepository.findByPuntuacionBetween(minPuntuacion, maxPuntuacion, pageable);
        } else {
            resenas = resenaRepository.findAll(pageable);
        }

        return resenas.map(this::convertToResenaAdminDTO);
    }

    /**
     * Moderar una reseña (admin)
     */
    @Transactional
    public ModeracionResultadoDTO moderarResena(Long resenaId, String adminEmail, ModerarResenaDTO dto) {
        Resena resena = resenaRepository.findById(resenaId)
                .orElseThrow(() -> new RuntimeException("RESENA_NO_ENCONTRADA"));

        EstadoResena nuevoEstado = EstadoResena.fromValue(dto.getEstado());
        resena.setEstado(nuevoEstado);
        resena.setModeradoPor(adminEmail);
        resena.setFechaModeracion(LocalDateTime.now());

        if (nuevoEstado == EstadoResena.rechazado && dto.getMotivoRechazo() != null) {
            resena.setMotivoRechazo(dto.getMotivoRechazo());
        }

        Resena updated = resenaRepository.save(resena);

        return ModeracionResultadoDTO.builder()
                .id(updated.getId())
                .estado(updated.getEstado().getValue())
                .moderadoPor(updated.getModeradoPor())
                .fechaModeracion(updated.getFechaModeracion())
                .build();
    }

    // ==================== HELPERS ====================

    /**
     * Verificar si un usuario ha comprado un producto
     */
    private boolean verificarCompra(Long usuarioId, Long productoId) {
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPedido.entregado);
        
        for (Pedido pedido : pedidos) {
            for (ItemPedido item : pedido.getItems()) {
                if (item.getProductoId().equals(productoId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ResenaDTO convertToResenaDTO(Resena resena) {
        return ResenaDTO.builder()
                .id(resena.getId())
                .productoId(resena.getProducto().getId())
                .usuarioId(resena.getUsuario().getId())
                .nombreUsuario(resena.getUsuario().getNombre() + " " + resena.getUsuario().getApellido())
                .puntuacion(resena.getPuntuacion())
                .comentario(resena.getComentario())
                .fechaCreacion(resena.getFechaCreacion())
                .verificado(resena.getVerificado())
                .build();
    }

    private ResenaUsuarioDTO convertToResenaUsuarioDTO(Resena resena) {
        return ResenaUsuarioDTO.builder()
                .id(resena.getId())
                .productoId(resena.getProducto().getId())
                .productoNombre(resena.getProducto().getNombre())
                .productoImagen(resena.getProducto().getImagen())
                .puntuacion(resena.getPuntuacion())
                .comentario(resena.getComentario())
                .fechaCreacion(resena.getFechaCreacion())
                .verificado(resena.getVerificado())
                .build();
    }

    private ResenaAdminDTO convertToResenaAdminDTO(Resena resena) {
        return ResenaAdminDTO.builder()
                .id(resena.getId())
                .productoId(resena.getProducto().getId())
                .productoNombre(resena.getProducto().getNombre())
                .usuarioId(resena.getUsuario().getId())
                .nombreUsuario(resena.getUsuario().getNombre() + " " + resena.getUsuario().getApellido())
                .emailUsuario(resena.getUsuario().getEmail())
                .puntuacion(resena.getPuntuacion())
                .comentario(resena.getComentario())
                .fechaCreacion(resena.getFechaCreacion())
                .estado(resena.getEstado().getValue())
                .verificado(resena.getVerificado())
                .motivoRechazo(resena.getMotivoRechazo())
                .moderadoPor(resena.getModeradoPor())
                .fechaModeracion(resena.getFechaModeracion())
                .build();
    }
}
