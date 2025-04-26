package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy sản phẩm theo ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Tạo mới sản phẩm
    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        if (productRequest.getCode() == null) {
            // Generate a new code automatically if it's null
            String generatedCode = generateUserCode();
            product.setCode(generatedCode);
        } else {
            // Otherwise, set the code from the request
            product.setCode(productRequest.getCode());
        }
        product.setName(productRequest.getName());
        product.setWeight(productRequest.getWeight());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStatus(1);

        return productRepository.save(product);
    }

    private String generateUserCode() {
        return UUID.randomUUID().toString(); // Example using UUID
    }

}
