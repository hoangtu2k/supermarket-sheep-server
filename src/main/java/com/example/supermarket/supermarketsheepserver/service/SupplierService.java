package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Supplier;
import com.example.supermarket.supermarketsheepserver.entity.Supplier.SupplierStatus;
import com.example.supermarket.supermarketsheepserver.repository.SupplierRepository;
import com.example.supermarket.supermarketsheepserver.request.SupplierRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByStatusOrderByCreatedAtDesc(Supplier.SupplierStatus.ACTIVE);
    }

    public List<SupplierRequest> getAllSuppliersAsDto() {
        List<Supplier> suppliers = supplierRepository.findByStatusOrderByCreatedAtDesc(SupplierStatus.ACTIVE);
        return suppliers.stream().map(this::mapToSupplierRequest).collect(Collectors.toList());
    }

    public SupplierRequest getSupplierByIdAsDto(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return mapToSupplierRequest(supplier);
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier createSupplier(@Valid SupplierRequest request) {
        if (supplierRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Supplier code already exists!");
        }

        Supplier supplier = Supplier.builder()
                .code(request.getCode())
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .status(SupplierStatus.ACTIVE)
                .build();

        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, @Valid SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        if (!supplier.getCode().equals(request.getCode()) && supplierRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Supplier code already exists!");
        }

        supplier.setCode(request.getCode());
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setStatus(SupplierStatus.valueOf(request.getStatus()));

        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier changeSupplierStatus(Long supplierId, String status) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));
        supplier.setStatus(SupplierStatus.valueOf(status));
        return supplierRepository.save(supplier);
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