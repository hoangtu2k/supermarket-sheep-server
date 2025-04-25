package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @Column(name = "description") // Đổi tên cột ở DB để tránh lỗi reserved keyword
    private String description;

    private BigDecimal price;
    private Integer quantity;

    @Column(name = "image_url") // Tên này khớp với DDL gốc
    private String imageUrl;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

}
