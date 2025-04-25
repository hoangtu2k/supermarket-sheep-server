package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class EntryDetailsRequest {

    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal importPrice;
    private BigDecimal payment;

}
