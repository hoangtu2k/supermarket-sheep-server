package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailsRequest {
    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code cannot exceed 50 characters")
    private String code;

    @NotNull(message = "Unit ID is required")
    private Long unitId; // Tham chiếu đến ID của Unit

    @NotNull(message = "Conversion rate is required")
    @Min(value = 1, message = "Conversion rate must be at least 1")
    private Integer conversionRate;

    @NotNull(message = "Price is required")
    private BigDecimal price;
}