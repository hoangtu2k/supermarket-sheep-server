package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        // Convert the list of Product to ProductRequest
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
                                .map(ProductPhoto::getImageUrl)
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

        // ✅ Always return 200 OK with an array (even if empty)
        return ResponseEntity.ok(productRequests);
    }


    // Lấy 1 sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductRequest> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        // Áp dụng Y HỆT cách chuyển đổi từ getAll()
        ProductRequest productRequest = new ProductRequest();
        productRequest.setId(product.getId());
        productRequest.setCode(product.getCode());
        productRequest.setName(product.getName());
        productRequest.setPrice(product.getPrice());
        productRequest.setQuantity(product.getQuantity());
        productRequest.setDescription(product.getDescription());
        productRequest.setStatus(product.getStatus());

        // Xử lý ảnh GIỐNG HỆT getAll()
        if (product.getProductPhotos() != null && !product.getProductPhotos().isEmpty()) {
            ProductPhoto mainPhoto = product.getProductPhotos().stream()
                    .filter(ProductPhoto::getMainImage)
                    .findFirst()
                    .orElse(null);

            productRequest.setImageUrl(mainPhoto != null ? mainPhoto.getImageUrl() : null);

            productRequest.setNotMainImages(
                    product.getProductPhotos().stream()
                            .filter(photo -> !photo.getMainImage())
                            .map(ProductPhoto::getImageUrl)
                            .collect(Collectors.toList())
            );
        } else {
            productRequest.setImageUrl(null);
            productRequest.setNotMainImages(new ArrayList<>());
        }

        return ResponseEntity.ok(productRequest);
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
        try {
            Product updatedProduct = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(updatedProduct); // Trả về 200 OK và đối tượng sản phẩm đã cập nhật
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Trả về 404 Not Found nếu không tìm thấy sản phẩm
        }
    }

    // Thay đổi trạng thái sản phẩm
    @PutMapping("/{id}/status")
    public ResponseEntity<Product> changeProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        Product updatedProduct = productService.changeProductStatus(id, status);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping(value = "/importExel", consumes = "multipart/form-data")
    public ResponseEntity<String> importExel(@RequestParam("file") MultipartFile file) {
        try {
            productService.importExel(file);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("ok");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
