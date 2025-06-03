package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductDetails;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.entity.Product.ProductStatus;
import com.example.supermarket.supermarketsheepserver.repository.ProductDetailsRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductPhotoRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductDetailsRequest;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductPhotoRepository productPhotoRepository;

    public List<ProductRequest> getAllProductsAsDto() {
        List<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE);
        return products.stream().map(product -> {
            // Explicitly load photos and details if needed
            product.getProductPhotos().size(); // Trigger lazy loading
            product.getProductDetails().size(); // Trigger lazy loading
            return mapToProductRequest(product);
        }).collect(Collectors.toList());
    }

    public List<ProductRequest> getProductsByStatusAsDto(ProductStatus status) {
        List<Product> products = productRepository.findByStatus(status);
        return products.stream().map(product -> {
            product.getProductPhotos().size(); // Trigger lazy loading
            product.getProductDetails().size(); // Trigger lazy loading
            return mapToProductRequest(product);
        }).collect(Collectors.toList());
    }

    public ProductRequest getProductByIdAsDto(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToProductRequest(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(@Valid ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .weight(request.getWeight())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .status(ProductStatus.ACTIVE)
                .build();

        Product savedProduct = productRepository.save(product);

        if (request.getProductDetails() != null && !request.getProductDetails().isEmpty()) {
            saveProductDetails(request.getProductDetails(), savedProduct);
        }

        if (request.getMainImage() != null || (request.getNotMainImages() != null && !request.getNotMainImages().isEmpty())) {
            saveProductPhotos(request, savedProduct);
        }

        return savedProduct;
    }

    private void saveProductDetails(List<ProductDetailsRequest> detailsRequests, Product product) {
        List<ProductDetails> details = detailsRequests.stream()
                .map(detailReq -> ProductDetails.builder()
                        .product(product)
                        .code(detailReq.getCode() != null ? detailReq.getCode() : generateDetailCode(product))
                        .unit(detailReq.getUnit() != null ? ProductDetails.Unit.valueOf(detailReq.getUnit()) : ProductDetails.Unit.CAN)
                        .conversionRate(detailReq.getConversionRate() != null ? detailReq.getConversionRate() : 1)
                        .price(detailReq.getPrice() != null ? detailReq.getPrice() : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        productDetailsRepository.saveAll(details);
    }

    private void saveProductPhotos(ProductRequest request, Product product) {
        List<ProductPhoto> photos = new ArrayList<>();

        if (request.getMainImage() && request.getImageUrl() != null) {
            productPhotoRepository.deleteByProductAndMainImage(product, true);
            photos.add(ProductPhoto.builder()
                    .product(product)
                    .imageUrl(request.getImageUrl())
                    .mainImage(true)
                    .build());
        }

        if (request.getNotMainImages() != null) {
            productPhotoRepository.deleteByProductAndMainImage(product, false);
            request.getNotMainImages().forEach(url -> photos.add(ProductPhoto.builder()
                    .product(product)
                    .imageUrl(url)
                    .mainImage(false)
                    .build()));
        }

        if (!photos.isEmpty()) {
            productPhotoRepository.saveAll(photos);
        }
    }

    @Transactional
    public Product updateProduct(Long productId, @Valid ProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setWeight(request.getWeight());
        existingProduct.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        existingProduct.setStatus(request.getStatus() != null ? ProductStatus.valueOf(request.getStatus()) : ProductStatus.ACTIVE);

        updateProductDetails(existingProduct, request.getProductDetails());
        updateProductPhotos(existingProduct, request);

        return productRepository.save(existingProduct);
    }

    private void updateProductDetails(Product product, List<ProductDetailsRequest> detailsRequests) {
        if (detailsRequests == null || detailsRequests.isEmpty()) {
            productDetailsRepository.deleteByProduct(product);
            return;
        }

        List<Long> newDetailIds = detailsRequests.stream()
                .map(ProductDetailsRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!newDetailIds.isEmpty()) {
            productDetailsRepository.deleteByProductAndIdNotIn(product, newDetailIds);
        } else {
            productDetailsRepository.deleteByProduct(product);
        }

        List<ProductDetails> updatedDetails = new ArrayList<>();
        for (ProductDetailsRequest detailReq : detailsRequests) {
            ProductDetails detail;
            if (detailReq.getId() != null) {
                detail = productDetailsRepository.findById(detailReq.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Product detail not found: " + detailReq.getId()));
            } else {
                detail = ProductDetails.builder()
                        .product(product)
                        .code(detailReq.getCode() != null ? detailReq.getCode() : generateDetailCode(product))
                        .build();
            }
            // Updated setUnit logic
            detail.setUnit(detailReq.getUnit() != null ? ProductDetails.Unit.valueOf(detailReq.getUnit().toUpperCase()) : ProductDetails.Unit.CAN);
            detail.setConversionRate(detailReq.getConversionRate() != null ? detailReq.getConversionRate() : 1);
            detail.setPrice(detailReq.getPrice() != null ? detailReq.getPrice() : BigDecimal.ZERO);
            updatedDetails.add(detail);
        }

        productDetailsRepository.saveAll(updatedDetails);
    }


    private void updateProductPhotos(Product product, ProductRequest request) {
        if (request.getMainImage() == null && request.getNotMainImages() == null) {
            return;
        }

        if (request.getMainImage() != null) {
            if (request.getMainImage() && request.getImageUrl() != null) {
                productPhotoRepository.findByProductAndMainImage(product, true).ifPresentOrElse(
                        mainPhoto -> {
                            if (!mainPhoto.getImageUrl().equals(request.getImageUrl())) {
                                mainPhoto.setImageUrl(request.getImageUrl());
                                productPhotoRepository.save(mainPhoto);
                            }
                        },
                        () -> productPhotoRepository.save(ProductPhoto.builder()
                                .product(product)
                                .imageUrl(request.getImageUrl())
                                .mainImage(true)
                                .build())
                );
            } else if (!request.getMainImage()) {
                productPhotoRepository.deleteByProductAndMainImage(product, true);
            }
        }

        if (request.getNotMainImages() != null) {
            productPhotoRepository.deleteByProductAndMainImage(product, false);
            if (!request.getNotMainImages().isEmpty()) {
                List<ProductPhoto> additionalPhotos = request.getNotMainImages().stream()
                        .map(url -> ProductPhoto.builder()
                                .product(product)
                                .imageUrl(url)
                                .mainImage(false)
                                .build())
                        .collect(Collectors.toList());
                productPhotoRepository.saveAll(additionalPhotos);
            }
        }
    }

    @Transactional
    public Product changeProductStatus(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setStatus(ProductStatus.valueOf(status));
        return productRepository.save(product);
    }

    private ProductRequest mapToProductRequest(Product product) {
        ProductRequest request = ProductRequest.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .weight(product.getWeight())
                .quantity(product.getQuantity())
                .status(product.getStatus().name())
                .build();

        if (product.getProductPhotos() != null) {
            product.getProductPhotos().stream()
                    .filter(ProductPhoto::getMainImage)
                    .findFirst()
                    .ifPresent(photo -> {
                        request.setMainImage(true);
                        request.setImageUrl(photo.getImageUrl());
                    });

            List<String> notMainImages = product.getProductPhotos().stream()
                    .filter(photo -> !photo.getMainImage())
                    .map(ProductPhoto::getImageUrl)
                    .collect(Collectors.toList());
            request.setNotMainImages(notMainImages);
        }

        if (product.getProductDetails() != null) {
            List<ProductDetailsRequest> details = product.getProductDetails().stream()
                    .map(detail -> ProductDetailsRequest.builder()
                            .id(detail.getId())
                            .code(detail.getCode())
                            .unit(detail.getUnit().name())
                            .conversionRate(detail.getConversionRate())
                            .price(detail.getPrice())
                            .build())
                    .collect(Collectors.toList());
            request.setProductDetails(details);
        }

        return request;
    }

    private String generateDetailCode(Product product) {
        return "PD-" + product.getId() + "-" + System.currentTimeMillis();
    }
}