package com.huertohogar.huertohogar.repository;

import com.huertohogar.huertohogar.model.Pedido;
import com.huertohogar.huertohogar.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
    
    List<Pedido> findByUsuarioId(Long usuarioId);
    
    List<Pedido> findByContactoEmail(String email);
    
    List<Pedido> findByEstado(EstadoPedido estado);
    
    List<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.contactoEmail = :email AND p.estado = :estado")
    List<Pedido> findByEmailAndEstado(@Param("email") String email, @Param("estado") EstadoPedido estado);
    
    // Buscar pedidos por usuarioId O por email de contacto (para usuarios autenticados)
    @Query("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId OR p.contactoEmail = :email")
    List<Pedido> findByUsuarioIdOrContactoEmail(@Param("usuarioId") Long usuarioId, @Param("email") String email);
    
    @Query("SELECT p FROM Pedido p WHERE (p.usuario.id = :usuarioId OR p.contactoEmail = :email) AND p.estado = :estado")
    List<Pedido> findByUsuarioIdOrContactoEmailAndEstado(
            @Param("usuarioId") Long usuarioId, 
            @Param("email") String email, 
            @Param("estado") EstadoPedido estado);
    
    // Buscar por email de contacto O email de usuario (para admin)
    @Query("SELECT p FROM Pedido p WHERE p.contactoEmail = :email OR (p.usuario IS NOT NULL AND p.usuario.email = :email)")
    List<Pedido> findByContactoEmailOrUsuarioEmail(@Param("email") String email);
    
    @Query("SELECT p FROM Pedido p WHERE (p.contactoEmail = :email OR (p.usuario IS NOT NULL AND p.usuario.email = :email)) AND p.estado = :estado")
    List<Pedido> findByContactoEmailOrUsuarioEmailAndEstado(
            @Param("email") String email, 
            @Param("estado") EstadoPedido estado);
    
    @Modifying
    @Query("UPDATE Pedido p SET p.leido = true WHERE p.id IN :ids")
    void marcarComoLeidos(@Param("ids") List<String> ids);
    
    List<Pedido> findByLeidoFalse();
    
    long countByLeidoFalse();
}
