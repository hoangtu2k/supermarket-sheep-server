package com.example.supermarket.supermarketsheepserver.bean;

import com.example.supermarket.supermarketsheepserver.service.ImportedGoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImportedGoodsService.ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ImportedGoodsService.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}