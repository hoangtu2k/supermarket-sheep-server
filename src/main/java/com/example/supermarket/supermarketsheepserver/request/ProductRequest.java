package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ProductRequest {

    private Long id;
    private Long code;
    private String name;
    private String describe;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private Integer status;
    private Long supplierId;
    private Long categoryId;
    private Long productTypeId;
    private Long productTypename;
    private String supplierName;
    private String categoryName;

}
