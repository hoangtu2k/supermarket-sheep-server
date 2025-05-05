package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDetailsRequest {
    private Long id;
    private String code;
    private String unit;
    private Integer conversionRate;
    private BigDecimal price;
}
