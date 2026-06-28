package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "review_photos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
