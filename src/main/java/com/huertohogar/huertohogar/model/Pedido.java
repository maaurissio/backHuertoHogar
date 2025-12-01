package com.huertohogar.huertohogar.model;

import com.huertohogar.huertohogar.model.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    private String id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.confirmado;

    @Column(nullable = false)
    @Builder.Default
    private Boolean leido = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Contacto
    @Column(nullable = false)
    private String contactoNombre;

    @Column(nullable = false)
    private String contactoApellido;

    @Column(nullable = false)
    private String contactoEmail;

    @Column(nullable = false)
    private String contactoTelefono;

    // Envío
    @Column(nullable = false)
    private String envioDireccion;

    @Column(nullable = false)
    private String envioCiudad;

    @Column(nullable = false)
    private String envioRegion;

    private String envioCodigoPostal;

    @Column(columnDefinition = "TEXT")
    private String envioNotas;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal envioCosto;

    @Column(nullable = false)
    @Builder.Default
    private Boolean envioEsGratis = false;

    // Totales
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = generarIdPedido();
        }
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    private String generarIdPedido() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return String.format("ORD-%d-%03d", timestamp, random);
    }

    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }

    public void removeItem(ItemPedido item) {
        items.remove(item);
        item.setPedido(null);
    }
}
