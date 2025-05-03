package com.example.supermarket.supermarketsheepserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    @Column(name = "description")
    private String description;

    private BigDecimal price;

    private Integer quantity;

    private Double weight;

    private LocalDateTime createDate;

    @Column(nullable = false)
    private Integer status = 1; // Default active

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<ProductPhoto> productPhotos = new HashSet<ProductPhoto>();


}
