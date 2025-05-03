package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean mainImage;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
