package com.example.supermarket.supermarketsheepserver.request;

import java.math.BigDecimal;

public record BillItemRequest(
        Long productDetailsId, // Changed from productId to productDetailsId
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Long unitId
) {}