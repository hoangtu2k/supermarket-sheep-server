package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for product-related requests, mapping to Product entity.
 */
@Data
@Builder
public class ProductRequest {

    private Long id;

    private String code;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Double weight;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private LocalDateTime createDate; // Thêm trường này

    private String status; // Maps to ProductStatus enum (e.g., ACTIVE, INACTIVE, DISCONTINUED)

    @NotEmpty(message = "Product details cannot be empty")
    private List<ProductDetailsRequest> productDetails;

    private Boolean mainImage;

    @Size(max = 1000, message = "Image URL cannot exceed 1000 characters")
    private String imageUrl;

    private List<String> notMainImages;

    private List<Long> categoryIds; // Thêm danh sách ID danh mục

    private List<String> sizes;
    private List<String> colors; // Thêm mới
    private List<String> materials; // Thêm mới

}