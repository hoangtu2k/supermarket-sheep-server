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
@EqualsAndHashCode(exclude = {"product"}) // Exclude bidirectional field
@ToString(exclude = {"product"}) // Prevent toString recursion
public class ProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String code;

    public enum Unit {
        CAN, PACK, CASE
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, columnDefinition = "ENUM('CAN', 'PACK', 'CASE') DEFAULT 'CAN'")
    private Unit unit = Unit.CAN; // Default to CAN

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