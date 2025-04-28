package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Supplier;
import com.example.supermarket.supermarketsheepserver.request.SupplierRequest;
import com.example.supermarket.supermarketsheepserver.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/supplier")
public class SupplierRest {

    @Autowired
    private SupplierService supplierService;

    // Lấy danh sách nhà cung cấp
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers != null ? suppliers : Collections.emptyList());
    }

    // Lấy 1 nhà cung cấp theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        return supplier.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới nhà cung cấp
    @PostMapping
    public ResponseEntity<Supplier> createProduct(@RequestBody SupplierRequest supplierRequest) {
        Supplier savedSupplier = supplierService.createSupplier(supplierRequest);
        return ResponseEntity.ok(savedSupplier);
    }

}
