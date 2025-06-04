package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Customer;
import com.example.supermarket.supermarketsheepserver.entity.Customer.CustomerStatus;
import com.example.supermarket.supermarketsheepserver.request.CustomerRequest;
import com.example.supermarket.supermarketsheepserver.request.LoginRequest;
import com.example.supermarket.supermarketsheepserver.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerRest {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerRequest>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomersAsDto());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CustomerRequest>> getCustomersByStatus(@RequestParam String status) {
        CustomerStatus customerStatus = CustomerStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(customerService.getCustomersByStatusAsDto(customerStatus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerByIdAsDto(id));
    }

    @PostMapping
    public ResponseEntity<CustomerRequest> createCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(201).body(customerService.getCustomerByIdAsDto(customer.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerRequest> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        Customer updatedCustomer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customerService.getCustomerByIdAsDto(updatedCustomer.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerRequest> changeCustomerStatus(@PathVariable Long id, @RequestParam String status) {
        Customer updatedCustomer = customerService.changeCustomerStatus(id, status);
        return ResponseEntity.ok(customerService.getCustomerByIdAsDto(updatedCustomer.getId()));
    }

}