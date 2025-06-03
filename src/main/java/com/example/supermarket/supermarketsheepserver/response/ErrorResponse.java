package com.example.supermarket.supermarketsheepserver.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO for standardized error responses.
 */
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
}
