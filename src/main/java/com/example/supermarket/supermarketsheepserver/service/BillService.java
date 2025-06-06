package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Bill;
import com.example.supermarket.supermarketsheepserver.entity.BillDetails;
import com.example.supermarket.supermarketsheepserver.entity.Customer;
import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductDetails;
import com.example.supermarket.supermarketsheepserver.repository.BillRepository;
import com.example.supermarket.supermarketsheepserver.repository.CustomerRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductDetailsRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.BillItemRequest;
import com.example.supermarket.supermarketsheepserver.request.BillRequest;
import com.example.supermarket.supermarketsheepserver.response.BillResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public BillResponse createBill(BillRequest request) {
        // Map status
        Bill.BillStatus status = Bill.BillStatus.valueOf(request.status().toUpperCase());

        // Create Bill entity
        Bill bill = Bill.builder()
                .billCode(request.billCode())
                .createdAt(request.createdAt())
                .status(status)
                .totalAmount(new BigDecimal(request.totalAmount().doubleValue()))
                .build();

        // Set customer if customerId is provided
        if (request.customerId() != null) {
            Customer customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.customerId()));
            bill.setCustomer(customer);
        }

        // Process items and update product quantities
        List<BillDetails> billDetails = new ArrayList<>();
        for (BillItemRequest item : request.items()) {
            // Find product details by productId and unitId
            ProductDetails productDetails = productDetailsRepository.findByProductIdAndUnitId(
                    item.productId(), item.unitId()
            ).orElseThrow(() -> new IllegalArgumentException(
                    "Product details not found for productId " + item.productId() + " and unitId " + item.unitId()
            ));

            // Get the associated product
            Product product = productDetails.getProduct();

            // Calculate quantity to deduct based on conversionRate
            int quantityToDeduct = item.quantity() * productDetails.getConversionRate();

            // Validate stock
            if (product.getQuantity() < quantityToDeduct) {
                throw new IllegalStateException(
                        "Insufficient stock for product " + product.getName() + ": " +
                                "Available " + product.getQuantity() + ", Required " + quantityToDeduct
                );
            }

            // Update product quantity
            product.setQuantity(product.getQuantity() - quantityToDeduct);
            productRepository.save(product);

            // Create BillDetails
            BillDetails details = BillDetails.builder()
                    .bill(bill)
                    .productDetails(productDetails) // Sử dụng productDetails thay vì product
                    .quantity(item.quantity())
                    .unitPrice(item.unitPrice()) // Lấy từ request hoặc có thể lấy từ productDetails.getPrice()
                    .subtotal(item.subtotal())   // Lấy từ request hoặc tính toán
                    .build();
            billDetails.add(details);
        }

        // Set bill details and calculate total
        bill.setBillDetails(billDetails);
        bill.calculateTotalAmount();

        // Save bill
        Bill savedBill = billRepository.save(bill);

        // Return response
        return new BillResponse(
                savedBill.getId(),
                savedBill.getBillCode(),
                savedBill.getCreatedAt(),
                savedBill.getStatus().name(),
                savedBill.getTotalAmount()
        );
    }
}