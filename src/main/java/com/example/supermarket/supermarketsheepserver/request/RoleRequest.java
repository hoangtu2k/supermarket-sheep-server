package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;
}
