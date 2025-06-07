// Product.java
package com.example.supermarket.supermarketsheepserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"photos", "productDetails"}) // Exclude bidirectional fields
@ToString(exclude = {"photos", "productDetails"}) // Prevent toString recursion
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    private Double weight;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ProductStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "product")
    private Set<ProductCategory> productCategories = new HashSet<>();



    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<ProductPhoto> productPhotos = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<ProductDetails> productDetails = new HashSet<>();



    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}