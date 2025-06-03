package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.request.ProductPhotoRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/image")
@RequiredArgsConstructor
public class ProductPhotoRest {

    private final ProductPhotoService productPhotoService;

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody ProductPhotoRequest request) {
        return ResponseEntity.status(201).body(productPhotoService.createProductPhoto(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productPhotoService.deleteProductPhoto(id);
        return ResponseEntity.noContent().build();
    }
}