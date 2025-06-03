package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Supplier;
import com.example.supermarket.supermarketsheepserver.request.SupplierRequest;
import com.example.supermarket.supermarketsheepserver.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/suppliers")
@RequiredArgsConstructor
public class SupplierRest {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierRequest>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<SupplierRequest> supplierRequests = suppliers.stream()
                .map(this::mapToSupplierRequest)
                .collect(Collectors.toList());
        return ResponseEntity.ok(supplierRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierRequest> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> ResponseEntity.ok(mapToSupplierRequest(supplier)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierRequest request) {
        try {
            Supplier savedSupplier = supplierService.createSupplier(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToSupplierRequest(savedSupplier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, request);
            return ResponseEntity.ok(mapToSupplierRequest(updatedSupplier));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeSupplierStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Supplier updatedSupplier = supplierService.changeSupplierStatus(id, status);
            return ResponseEntity.ok(mapToSupplierRequest(updatedSupplier));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private SupplierRequest mapToSupplierRequest(Supplier supplier) {
        return SupplierRequest.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .status(supplier.getStatus().name())
                .build();
    }
}