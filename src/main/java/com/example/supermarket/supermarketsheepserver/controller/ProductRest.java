package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/products")
public class ProductRest {

    @Autowired
    private ProductService productService;

    // Lấy danh sách sản phẩm
    @GetMapping()
    public ResponseEntity<List<ProductRequest>> getAll() {
        // Retrieve the list of products from the service
        List<Product> products = productService.getAllProducts();

        // Convert the list of Product to ProductReq
        List<ProductRequest> productRequests = products.stream()
                .map(product -> {
                    ProductRequest productRequest = new ProductRequest();
                    productRequest.setId(product.getId());
                    productRequest.setCode(product.getCode());
                    productRequest.setName(product.getName());
                    productRequest.setPrice(product.getPrice());
                    productRequest.setQuantity(product.getQuantity());
                    productRequest.setDescription(product.getDescription());
                    productRequest.setStatus(product.getStatus());

                    // Set main image and image URL
                    if (product.getProductPhotos() != null && !product.getProductPhotos().isEmpty()) {
                        ProductPhoto mainPhoto = product.getProductPhotos().stream()
                                .filter(ProductPhoto::getMainImage)
                                .findFirst()
                                .orElse(null);

                        if (mainPhoto != null) {
                            productRequest.setMainImage(true);
                            productRequest.setImageUrl(mainPhoto.getImageUrl());
                        } else {
                            productRequest.setMainImage(false);
                            productRequest.setImageUrl(null);
                        }

                        // Collect non-main image URLs
                        List<String> notMainImageUrls = product.getProductPhotos().stream()
                                .filter(photo -> !photo.getMainImage())
                                .map(ProductPhoto::getImageUrl) // Assuming getImageUrl() exists
                                .collect(Collectors.toList());

                        productRequest.setNotMainImages(notMainImageUrls);
                    } else {
                        productRequest.setMainImage(false);
                        productRequest.setImageUrl(null); // No photos available
                        productRequest.setNotMainImages(new ArrayList<>()); // Ensure empty list
                    }

                    return productRequest;
                })
                .collect(Collectors.toList());

        // Return response based on the presence of productReqs
        if (productRequests.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no products
        }
        return ResponseEntity.ok(productRequests); // Return 200 and the list of products
    }

    // Lấy 1 sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới sản phẩm
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest) {
        Product savedProduct = productService.createProduct(productRequest);
        return ResponseEntity.ok(savedProduct);
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thay đổi trạng thái sản phẩm
    @PutMapping("/{id}/status")
    public ResponseEntity<Product> changeProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        Product updatedProduct = productService.changeProductStatus(id, status);
        return ResponseEntity.ok(updatedProduct);
    }

}
