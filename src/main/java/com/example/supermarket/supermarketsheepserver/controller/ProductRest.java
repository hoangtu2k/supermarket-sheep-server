package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.Product.ProductStatus;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductRest {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductRequest>> getAll() {
        return ResponseEntity.ok(productService.getAllProductsAsDto());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductRequest>> getProductsByStatus(@RequestParam String status) {
        ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(productService.getProductsByStatusAsDto(productStatus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductByIdAsDto(id));
    }

    @PostMapping
    public ResponseEntity<ProductRequest> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.status(201).body(productService.getProductByIdAsDto(product.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRequest> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(productService.getProductByIdAsDto(updatedProduct.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProductRequest> changeProductStatus(@PathVariable Long id, @RequestParam String status) {
        Product updatedProduct = productService.changeProductStatus(id, status);
        return ResponseEntity.ok(productService.getProductByIdAsDto(updatedProduct.getId()));
    }

    @GetMapping("/check-sku")
    public ResponseEntity<Map<String, Boolean>> checkSKU(@RequestParam String code, @RequestParam(required = false) Long excludeId) {
        boolean exists = productService.checkSKUExists(code, excludeId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/check-barcode")
    public ResponseEntity<Map<String, Boolean>> checkBarcode(@RequestParam String barCode, @RequestParam(required = false) Long excludeProductId) {
        boolean exists = productService.checkBarcodeExists(barCode, excludeProductId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}