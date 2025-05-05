package com.example.supermarket.supermarketsheepserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
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

    private String name;

    @Column(name = "description")
    private String description;

    private Double weight;

    private Integer quantity;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(nullable = false)
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<ProductPhoto> productPhotos = new HashSet<ProductPhoto>();

    // Mối quan hệ với ProductDetails (chi tiết sản phẩm theo từng đơn vị)
    @OneToMany(mappedBy = "product")
    private Set<ProductDetails> productDetails = new HashSet<ProductDetails>();


}
