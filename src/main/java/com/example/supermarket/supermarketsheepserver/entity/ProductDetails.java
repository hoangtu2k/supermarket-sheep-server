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
public class ProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code; // Mã hàng riêng của đơn vị
    private String unit; // lon, lốc, thùng
    private Integer conversionRate; // Giá trị quy đổi về lon
    private BigDecimal price;


    @ManyToOne
    @JoinColumn(name = "product_id") // Liên kết với sản phẩm gốc
    private Product product;

}
