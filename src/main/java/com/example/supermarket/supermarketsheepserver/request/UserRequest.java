package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for user-related requests, mapping to User entity.
 */
@Data
@Builder
public class UserRequest {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(max = 24, message = "Username cannot exceed 24 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 255, message = "Confirm password must be between 6 and 255 characters")
    private String rePassword;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    private String dateOfBirth; // Parsed to LocalDate in service (e.g., "2025-06-03")

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    private String address;

    @NotBlank(message = "Status is required")
    private String status; // Maps to UserStatus enum (e.g., ACTIVE, INACTIVE, SUSPENDED)

    @NotNull(message = "Role ID is required")
    private Long roleId;

    private String roleName;


}