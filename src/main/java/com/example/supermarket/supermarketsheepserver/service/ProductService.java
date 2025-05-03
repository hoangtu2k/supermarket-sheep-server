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
        return productRepository.getAllProducts();
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long id) {
        return productRepository.getById(id);
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
        product.setCreateDate(LocalDateTime.now());
        product.setStatus(1);

        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Long id, ProductRequest productRequest) {
        // Kiểm tra xem sản phẩm có tồn tại không
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        Product product = optionalProduct.get();
        // Cập nhật các thuộc tính sản phẩm
        if (productRequest.getCode() == null) {
            String generatedCode = generateUserCode();
            product.setCode(generatedCode);
        }
        else {
            product.setCode(productRequest.getCode());
        }
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setWeight(productRequest.getWeight());
        product.setDescription(productRequest.getDescription());
        product.setQuantity(productRequest.getQuantity());
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
