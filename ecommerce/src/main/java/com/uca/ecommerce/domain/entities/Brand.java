package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name="slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name="logo_url", length = 500)
    private String logoUrl;
}
