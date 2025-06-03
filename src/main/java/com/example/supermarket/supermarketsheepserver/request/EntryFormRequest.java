package com.example.supermarket.supermarketsheepserver.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for entry form-related requests, mapping to EntryForm entity.
 */
@Data
@Builder
public class EntryFormRequest {

    private Long entryFormId;

    @NotNull(message = "Entry date is required")
    private LocalDateTime entryDate;

    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Supplier name is required")
    private String supplierName;

    @NotNull(message = "Total is required")
    private BigDecimal total;

    @NotEmpty(message = "Entry details cannot be empty")
    private List<EntryDetailsRequest> entryDetailsRequests;

    private String status; // Maps to EntryFormStatus enum (e.g., PENDING, COMPLETED, CANCELED)
}