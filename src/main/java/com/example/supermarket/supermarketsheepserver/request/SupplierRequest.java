package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for supplier-related requests, mapping to Supplier entity.
 */
@Data
@Builder
public class SupplierRequest {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    private String address;

    private String status; // Maps to SupplierStatus enum (e.g., ACTIVE, INACTIVE)
}