package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Customer;
import com.example.supermarket.supermarketsheepserver.entity.Customer.CustomerStatus;
import com.example.supermarket.supermarketsheepserver.repository.CustomerRepository;
import com.example.supermarket.supermarketsheepserver.request.CustomerRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public Customer login(String identifier) {
        return customerRepository.findByIdentifier(identifier);
    }

    public List<CustomerRequest> getAllCustomersAsDto() {
        List<Customer> customers = customerRepository.findByStatus(CustomerStatus.ACTIVE);
        return customers.stream()
                .map(this::mapToCustomerRequest)
                .collect(Collectors.toList());
    }

    public List<CustomerRequest> getCustomersByStatusAsDto(CustomerStatus status) {
        List<Customer> customers = customerRepository.findByStatus(status);
        return customers.stream()
                .map(this::mapToCustomerRequest)
                .collect(Collectors.toList());
    }

    public CustomerRequest getCustomerByIdAsDto(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return mapToCustomerRequest(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer createCustomer(@Valid CustomerRequest request) {
        Customer customer = Customer.builder()
                .code(request.getCode())
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .phone(request.getPhone())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .status(CustomerStatus.ACTIVE)
                .build();

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long customerId, @Valid CustomerRequest request) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        existingCustomer.setCode(request.getCode());
        existingCustomer.setName(request.getName());
        existingCustomer.setUsername(request.getUsername());
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingCustomer.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        }
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setDateOfBirth(request.getDateOfBirth());
        existingCustomer.setAddress(request.getAddress());
        existingCustomer.setStatus(request.getStatus() != null ? CustomerStatus.valueOf(request.getStatus()) : CustomerStatus.ACTIVE);

        return customerRepository.save(existingCustomer);
    }

    @Transactional
    public Customer changeCustomerStatus(Long customerId, String status) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        customer.setStatus(CustomerStatus.valueOf(status));
        return customerRepository.save(customer);
    }

    private CustomerRequest mapToCustomerRequest(Customer customer) {
        return CustomerRequest.builder()
                .id(customer.getId())
                .code(customer.getCode())
                .name(customer.getName())
                .username(customer.getUsername())
                .password(null) // Do not return hashed password
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .dateOfBirth(customer.getDateOfBirth())
                .address(customer.getAddress())
                .status(customer.getStatus().name())
                .build();
    }
}