package com.huertohogar.huertohogar.service;

import com.huertohogar.huertohogar.dto.pedido.*;
import com.huertohogar.huertohogar.exception.BadRequestException;
import com.huertohogar.huertohogar.exception.ForbiddenException;
import com.huertohogar.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.huertohogar.model.*;
import com.huertohogar.huertohogar.model.enums.EstadoPedido;
import com.huertohogar.huertohogar.model.enums.Rol;
import com.huertohogar.huertohogar.repository.PedidoRepository;
import com.huertohogar.huertohogar.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ConfiguracionService configuracionService;
    private final EmailService emailService;

    public List<PedidoDTO> findAll(String estado, String email, Usuario currentUser) {
        List<Pedido> pedidos;

        boolean isAdmin = currentUser != null && currentUser.getRol() == Rol.administrador;

        if (isAdmin) {
            if (email != null && !email.isEmpty()) {
                // Admin buscando por email - buscar tanto por contactoEmail como por usuario.email
                if (estado != null && !estado.isEmpty()) {
                    EstadoPedido estadoPedido = EstadoPedido.fromValue(estado);
                    pedidos = pedidoRepository.findByContactoEmailOrUsuarioEmailAndEstado(email, estadoPedido);
                } else {
                    pedidos = pedidoRepository.findByContactoEmailOrUsuarioEmail(email);
                }
            } else if (estado != null && !estado.isEmpty()) {
                EstadoPedido estadoPedido = EstadoPedido.fromValue(estado);
                pedidos = pedidoRepository.findByEstado(estadoPedido);
            } else {
                pedidos = pedidoRepository.findAll();
            }
        } else if (currentUser != null) {
            // Usuario normal - obtener sus propios pedidos por usuarioId O por su email
            if (estado != null && !estado.isEmpty()) {
                EstadoPedido estadoPedido = EstadoPedido.fromValue(estado);
                pedidos = pedidoRepository.findByUsuarioIdOrContactoEmailAndEstado(
                        currentUser.getId(), currentUser.getEmail(), estadoPedido);
            } else {
                pedidos = pedidoRepository.findByUsuarioIdOrContactoEmail(
                        currentUser.getId(), currentUser.getEmail());
            }
        } else {
            pedidos = new ArrayList<>();
        }

        return pedidos.stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PedidoDTO findById(String id, Usuario currentUser) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        // Verificar permisos
        boolean isAdmin = currentUser != null && currentUser.getRol() == Rol.administrador;
        boolean isOwner = currentUser != null && pedido.getUsuario() != null 
                && pedido.getUsuario().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("No tienes permisos para ver este pedido");
        }

        return PedidoDTO.fromEntity(pedido);
    }

    @Transactional
    public PedidoDTO create(PedidoCreateRequest request, Usuario currentUser) {
        // Calcular subtotal y validar productos
        BigDecimal subtotal = BigDecimal.ZERO;
        List<ItemPedido> items = new ArrayList<>();

        for (ItemPedidoRequest itemRequest : request.getItems()) {
            Producto producto = productoRepository.findById(itemRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", itemRequest.getId()));

            if (producto.getStock() < itemRequest.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para: " + producto.getNombre());
            }

            BigDecimal itemSubtotal = producto.getPrecio().multiply(BigDecimal.valueOf(itemRequest.getCantidad()));
            subtotal = subtotal.add(itemSubtotal);

            ItemPedido item = ItemPedido.builder()
                    .productoId(producto.getId())
                    .nombre(producto.getNombre())
                    .precio(producto.getPrecio())
                    .cantidad(itemRequest.getCantidad())
                    .subtotal(itemSubtotal)
                    .build();

            items.add(item);

            // Actualizar stock
            producto.setStock(producto.getStock() - itemRequest.getCantidad());
            productoRepository.save(producto);
        }

        // Calcular costo de envío
        ConfiguracionEnvio config = configuracionService.getConfiguracionEntity();
        BigDecimal costoEnvio;
        boolean esGratis = false;

        if (config.getEnvioGratisHabilitado() && subtotal.compareTo(config.getEnvioGratisDesde()) >= 0) {
            costoEnvio = BigDecimal.ZERO;
            esGratis = true;
        } else {
            costoEnvio = config.getCostoEnvioBase();
        }

        BigDecimal total = subtotal.add(costoEnvio);

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .usuario(currentUser)
                .contactoNombre(request.getContacto().getNombre())
                .contactoApellido(request.getContacto().getApellido())
                .contactoEmail(request.getContacto().getEmail())
                .contactoTelefono(request.getContacto().getTelefono())
                .envioDireccion(request.getEnvio().getDireccion())
                .envioCiudad(request.getEnvio().getCiudad())
                .envioRegion(request.getEnvio().getRegion())
                .envioCodigoPostal(request.getEnvio().getCodigoPostal())
                .envioNotas(request.getEnvio().getNotas())
                .envioCosto(costoEnvio)
                .envioEsGratis(esGratis)
                .subtotal(subtotal)
                .costoEnvio(costoEnvio)
                .total(total)
                .estado(EstadoPedido.confirmado)
                .leido(false)
                .items(new ArrayList<>())
                .build();

        // Agregar items al pedido
        for (ItemPedido item : items) {
            pedido.addItem(item);
        }

        pedido = pedidoRepository.save(pedido);
        log.info("Pedido creado: {}", pedido.getId());

        // Enviar email de confirmación
        try {
            emailService.enviarNotificacionPedido(
                    request.getContacto().getEmail(),
                    request.getContacto().getNombre(),
                    pedido.getId()
            );
        } catch (Exception e) {
            log.error("Error enviando email de confirmación: {}", e.getMessage());
        }

        return PedidoDTO.fromEntity(pedido);
    }

    @Transactional
    public void updateEstado(String id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        EstadoPedido estado = EstadoPedido.fromValue(nuevoEstado);
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);

        log.info("Estado de pedido {} actualizado a: {}", id, nuevoEstado);
    }

    @Transactional
    public void marcarComoLeidos(List<String> ids) {
        pedidoRepository.marcarComoLeidos(ids);
        log.info("Pedidos marcados como leídos: {}", ids.size());
    }
}
