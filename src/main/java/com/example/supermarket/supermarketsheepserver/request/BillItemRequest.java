package com.example.supermarket.supermarketsheepserver.request;

import java.math.BigDecimal;

public record BillItemRequest(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Long unitId
) {}
