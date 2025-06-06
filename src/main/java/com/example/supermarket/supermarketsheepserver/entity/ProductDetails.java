// ProductDetails.java
package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"product"})
@ToString(exclude = {"product"})
public class ProductDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    @NotNull
    private Unit unit;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    private Integer conversionRate;

    @NotNull
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;
}