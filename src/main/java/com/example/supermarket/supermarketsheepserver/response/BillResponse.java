package com.example.supermarket.supermarketsheepserver.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponse(
        Long id,
        String billCode,
        LocalDateTime createdAt,
        String status,
        BigDecimal totalAmount
) {}
