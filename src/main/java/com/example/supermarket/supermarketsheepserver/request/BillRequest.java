package com.example.supermarket.supermarketsheepserver.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BillRequest(
        String billCode,
        LocalDateTime createdAt,
        String status,
        BigDecimal totalAmount,
        String customerName,
        String customerEmail,
        Long customerId,
        List<BillItemRequest> items
) {}