package com.huertohogar.huertohogar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "valor")
    private String value;

    @Column(nullable = false)
    private String label;

    @Column(unique = true, nullable = false, length = 3)
    private String codigo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean esDefault = false;
}
