package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for account-related requests, mapping to User entity.
 */
@Data
@Builder
public class AccountRequest {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(max = 24, message = "Username cannot exceed 24 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    @NotBlank(message = "Status is required")
    private String status; // Maps to UserStatus enum (e.g., ACTIVE, INACTIVE, SUSPENDED)

    private Long roleId; // Maps to Role entity ID
}