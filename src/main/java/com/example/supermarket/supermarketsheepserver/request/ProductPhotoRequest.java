package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for product photo-related requests, mapping to ProductPhoto entity.
 */
@Data
@Builder
public class ProductPhotoRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Image URL is required")
    @Size(max = 1000, message = "Image URL cannot exceed 1000 characters")
    private String imageUrl;

    @NotNull(message = "Main image flag is required")
    private Boolean mainImage;
}