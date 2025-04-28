package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.Supplier;
import com.example.supermarket.supermarketsheepserver.repository.SupplierRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.request.SupplierRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // Lấy tất cả nhà cung cấp
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // Lấy nhà cung cấp theo ID
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    // Tạo mới nhà cung cấp
    public Supplier createSupplier(SupplierRequest supplierRequest) {
        Supplier supplier = new Supplier();
        if (supplierRequest.getCode() == null) {
            // Generate a new code automatically if it's null
            String generatedCode = generateUserCode();
            supplier.setCode(generatedCode);
        } else {
            // Otherwise, set the code from the request
            supplier.setCode(supplierRequest.getCode());
        }
        supplier.setName(supplierRequest.getName());
        supplier.setName(supplierRequest.getName());
        supplier.setPhone(supplierRequest.getPhone());

        supplier.setStatus(1);

        return supplierRepository.save(supplier);
    }

    // Tạo code ramdom
    private String generateUserCode() {
        return UUID.randomUUID().toString();
    }

}
