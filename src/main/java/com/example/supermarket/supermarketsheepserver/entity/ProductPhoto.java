// ProductPhoto.java
package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "product_id")})
@EqualsAndHashCode(exclude = {"product"}) // Exclude bidirectional field
@ToString(exclude = {"product"}) // Prevent toString recursion
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private Boolean mainImage = false;

    @NotNull
    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;
}