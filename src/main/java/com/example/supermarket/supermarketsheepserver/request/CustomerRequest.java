package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for customer-related requests, mapping to Customer entity.
 */
@Data
@Builder
public class CustomerRequest {

    private Long id;

    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    private String address;

    private String status; // Maps to CustomerStatus enum (e.g., ACTIVE, INACTIVE)
}