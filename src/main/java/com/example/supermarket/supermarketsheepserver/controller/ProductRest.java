package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.request.ProductDetailsRequest;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductRest {

    private final ProductService productService;

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
                    productRequest.setName(product.getName());
                    productRequest.setQuantity(product.getQuantity());
                    productRequest.setDescription(product.getDescription());
                    productRequest.setWeight(product.getWeight());
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
                        productRequest.setImageUrl(null);
                        productRequest.setNotMainImages(new ArrayList<>());
                    }

                    // Add product details information
                    if (product.getProductDetails() != null && !product.getProductDetails().isEmpty()) {
                        List<ProductDetailsRequest> detailRequests = product.getProductDetails().stream()
                                .map(detail -> {
                                    ProductDetailsRequest detailRequest = new ProductDetailsRequest();
                                    detailRequest.setId(detail.getId());
                                    detailRequest.setCode(detail.getCode());
                                    detailRequest.setUnit(detail.getUnit());
                                    detailRequest.setConversionRate(detail.getConversionRate());
                                    detailRequest.setPrice(detail.getPrice());
                                    return detailRequest;
                                })
                                .collect(Collectors.toList());
                        productRequest.setProductDetails(detailRequests);
                    } else {
                        productRequest.setProductDetails(new ArrayList<>());
                    }

                    return productRequest;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productRequests);
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductRequest> getById(@PathVariable Long id) {
        // Tìm sản phẩm theo id
        Product product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển Product thành ProductRequest
        ProductRequest productRequest = new ProductRequest();
        productRequest.setId(product.getId());
        productRequest.setName(product.getName());
        productRequest.setQuantity(product.getQuantity());
        productRequest.setDescription(product.getDescription());
        productRequest.setWeight(product.getWeight());
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
            productRequest.setImageUrl(null);
            productRequest.setNotMainImages(new ArrayList<>());
        }

        // Add product details information
        if (product.getProductDetails() != null && !product.getProductDetails().isEmpty()) {
            List<ProductDetailsRequest> detailRequests = product.getProductDetails().stream()
                    .map(detail -> {
                        ProductDetailsRequest detailRequest = new ProductDetailsRequest();
                        detailRequest.setId(detail.getId());
                        detailRequest.setCode(detail.getCode());
                        detailRequest.setUnit(detail.getUnit());
                        detailRequest.setConversionRate(detail.getConversionRate());
                        detailRequest.setPrice(detail.getPrice());
                        return detailRequest;
                    })
                    .collect(Collectors.toList());
            productRequest.setProductDetails(detailRequests);
        } else {
            productRequest.setProductDetails(new ArrayList<>());
        }

        return ResponseEntity.ok(productRequest);
    }

    // Tạo mới sản phẩm
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.createProduct(request);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // cập nhật sản phẩm
    @Transactional  // Rollback
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    // Thay đổi trạng thái sản phẩm
    @PutMapping("/{id}/status")
    public ResponseEntity<Product> changeProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        Product updatedProduct = productService.changeProductStatus(id, status);
        return ResponseEntity.ok(updatedProduct);
    }

}
