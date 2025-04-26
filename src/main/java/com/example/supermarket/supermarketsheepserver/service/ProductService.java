package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.findAllProductsOrderedByCreateDate();
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
        product.setCreateDate(LocalDateTime.now());
        product.setStatus(1);

        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Long id, ProductRequest productRequest) {
        Product product = getProductById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        // Cập nhật các trường của sản phẩm
        product.setCode(productRequest.getCode());
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setWeight(productRequest.getWeight());
        product.setDescription(productRequest.getDescription());
        product.setQuantity(productRequest.getQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        // Lưu sản phẩm đã cập nhật
        return productRepository.save(product);
    }

    // Thay đổi trạng thái sản phẩm (xóa hoặc khôi phục)
    public Product changeProductStatus(Long productId, Integer newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    // Tạo code ramdom
    private String generateUserCode() {
        return UUID.randomUUID().toString();
    }

}
