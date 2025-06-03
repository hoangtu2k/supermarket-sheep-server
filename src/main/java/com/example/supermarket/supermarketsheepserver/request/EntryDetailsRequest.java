package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for entry details-related requests, mapping to EntryDetails entity.
 */
@Data
@Builder
public class EntryDetailsRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Import price is required")
    private BigDecimal importPrice;

    @NotNull(message = "Payment is required")
    private BigDecimal payment;

    private String status; // Maps to EntryDetailsStatus enum (e.g., ACTIVE, INACTIVE), if applicable
}