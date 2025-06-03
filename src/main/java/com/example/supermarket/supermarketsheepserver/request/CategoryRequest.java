package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for category-related requests, mapping to Category entity.
 */
@Data
@Builder
public class CategoryRequest {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Status is required")
    private String status; // Maps to CategoryStatus enum (e.g., ACTIVE, INACTIVE)
}