package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private Long id;
    private String name;
    private String description;
    private Double weight;
    private Integer quantity;
    private Integer status;

    // Product details
    private List<ProductDetailsRequest> productDetails;

    // Images
    private Boolean mainImage;
    private String imageUrl;
    private List<String> notMainImages;

    private Long productId;


}
