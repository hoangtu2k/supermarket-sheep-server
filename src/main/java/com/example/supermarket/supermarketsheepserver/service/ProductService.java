package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.*;
import com.example.supermarket.supermarketsheepserver.entity.Product.ProductStatus;
import com.example.supermarket.supermarketsheepserver.repository.*;
import com.example.supermarket.supermarketsheepserver.request.ProductDetailsRequest;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductPhotoRepository productPhotoRepository;
    private final UnitRepository unitRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public List<ProductRequest> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                    .filter(Objects::nonNull)
                    .map(this::mapToProductRequest)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return productRepository.findByNameOrBarCodeContainingIgnoreCase(keyword, ProductStatus.ACTIVE).stream()
                .filter(Objects::nonNull)
                .map(this::mapToProductRequest)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    // Existing checkSKUExists and checkBarcodeExists remain unchanged
    public boolean checkSKUExists(String code, Long excludeId) {
        if (excludeId != null) {
            return productRepository.existsByCodeAndIdNot(code, excludeId);
        }
        return productRepository.existsByCode(code);
    }

    public boolean checkBarcodeExists(String barCode, Long excludeProductId) {
        log.debug("Checking barcode: {}, excludeProductId: {}", barCode, excludeProductId);
        if (excludeProductId != null) {
            return productRepository.existsByProductDetailsBarCodeAndProductIdNot(barCode, excludeProductId);
        }
        return productRepository.existsByProductDetailsBarCode(barCode);
    }

    public List<ProductRequest> getAllProductsAsDto() {
        List<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE);
        return products.stream().map(product -> {
            product.getProductPhotos().size();
            product.getProductDetails().size();
            return mapToProductRequest(product);
        }).collect(Collectors.toList());
    }

    public List<ProductRequest> getProductsByStatusAsDto(ProductStatus status) {
        List<Product> products = productRepository.findByStatus(status);
        return products.stream().map(product -> {
            product.getProductPhotos().size();
            product.getProductDetails().size();
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
    public Product createProduct(ProductRequest request) {
        // Validate code
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SKU là bắt buộc");
        }
        if (checkSKUExists(request.getCode(), null)) {
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        }

        Product product = Product.builder()
                .code(request.getCode().trim())
                .name(request.getName())
                .description(request.getDescription())
                .weight(request.getWeight())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .status(ProductStatus.ACTIVE)
                .sizes(request.getSizes() != null ? request.getSizes() : new ArrayList<>())
                .colors(request.getColors() != null ? request.getColors() : new ArrayList<>())
                .materials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>())
                .build();

        Product savedProduct = productRepository.save(product);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
                ProductCategory productCategory = ProductCategory.builder()
                        .product(savedProduct)
                        .category(category)
                        .build();
                productCategoryRepository.save(productCategory);
            }
        }

        if (request.getProductDetails() != null && !request.getProductDetails().isEmpty()) {
            saveProductDetails(request.getProductDetails(), savedProduct);
        }

        if (request.getMainImage() != null || (request.getNotMainImages() != null && !request.getNotMainImages().isEmpty())) {
            saveProductPhotos(request, savedProduct);
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long productId, @Valid ProductRequest request) {
        try {
            // Validate code
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã SKU là bắt buộc");
            }
            if (checkSKUExists(request.getCode(), productId)) {
                throw new IllegalArgumentException("Mã SKU đã tồn tại");
            }

            // Validate barcodes
            if (request.getProductDetails() != null && !request.getProductDetails().isEmpty()) {
                for (ProductDetailsRequest detailReq : request.getProductDetails()) {
                    if (detailReq.getBarCode() != null && !detailReq.getBarCode().trim().isEmpty()) {
                        if (checkBarcodeExists(detailReq.getBarCode(), productId)) {
                            throw new IllegalArgumentException("Mã Barcode '" + detailReq.getBarCode() + "' đã tồn tại");
                        }
                    }
                }
            }

            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

            existingProduct.setCode(request.getCode().trim());
            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setWeight(request.getWeight());
            existingProduct.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
            existingProduct.setStatus(request.getStatus() != null ? Product.ProductStatus.valueOf(request.getStatus()) : Product.ProductStatus.ACTIVE);
            existingProduct.setSizes(request.getSizes() != null ? request.getSizes() : new ArrayList<>());
            existingProduct.setColors(request.getColors() != null ? request.getColors() : new ArrayList<>());
            existingProduct.setMaterials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>());

            productCategoryRepository.deleteByProductId(productId);

            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                for (Long categoryId : request.getCategoryIds()) {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
                    ProductCategory productCategory = ProductCategory.builder()
                            .product(existingProduct)
                            .category(category)
                            .build();
                    productCategoryRepository.save(productCategory);
                }
            }

            updateProductDetails(existingProduct, request.getProductDetails());
            updateProductPhotos(existingProduct, request);

            return productRepository.save(existingProduct);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_barcode")) {
                throw new IllegalArgumentException("Mã Barcode đã tồn tại trong hệ thống");
            }
            throw new RuntimeException("Cập nhật sản phẩm thất bại do lỗi dữ liệu", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cập nhật sản phẩm thất bại", e);
        }
    }

    @Transactional
    public Product changeProductStatus(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setStatus(ProductStatus.valueOf(status));
        return productRepository.save(product);
    }

    private void saveProductDetails(List<ProductDetailsRequest> detailsRequests, Product product) {
        List<ProductDetails> details = detailsRequests.stream()
                .map(detailReq -> {
                    Unit unit = unitRepository.findById(detailReq.getUnitId())
                            .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + detailReq.getUnitId()));
                    return ProductDetails.builder()
                            .product(product)
                            .barCode(detailReq.getBarCode() != null ? detailReq.getBarCode() : generateDetailBarCode(product))
                            .unit(unit)
                            .conversionRate(detailReq.getConversionRate() != null ? detailReq.getConversionRate() : 1)
                            .price(detailReq.getPrice() != null ? detailReq.getPrice() : BigDecimal.ZERO)
                            .build();
                })
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
                // Only update barCode if provided and different
                if (detailReq.getBarCode() != null && !detailReq.getBarCode().trim().isEmpty() &&
                        !detailReq.getBarCode().equals(detail.getBarCode())) {
                    detail.setBarCode(detailReq.getBarCode().trim());
                }
            } else {
                detail = ProductDetails.builder()
                        .product(product)
                        .barCode(detailReq.getBarCode() != null && !detailReq.getBarCode().trim().isEmpty()
                                ? detailReq.getBarCode().trim()
                                : generateDetailBarCode(product))
                        .build();
            }
            Unit unit = unitRepository.findById(detailReq.getUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + detailReq.getUnitId()));
            detail.setUnit(unit);
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

    private ProductRequest mapToProductRequest(Product product) {
        ProductRequest request = ProductRequest.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .weight(product.getWeight())
                .quantity(product.getQuantity())
                .status(product.getStatus().name())
                .sizes(product.getSizes())
                .colors(product.getColors())
                .materials(product.getMaterials())
                .createDate(product.getCreateDate())
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
                            .barCode(detail.getBarCode()) // Sử dụng barCode
                            .unitId(detail.getUnit().getId())
                            .conversionRate(detail.getConversionRate())
                            .price(detail.getPrice())
                            .build())
                    .collect(Collectors.toList());
            request.setProductDetails(details);
        }

        List<Long> categoryIds = productCategoryRepository.findByProductId(product.getId())
                .stream()
                .map(productCategory -> productCategory.getCategory().getId())
                .collect(Collectors.toList());
        request.setCategoryIds(categoryIds);

        return request;
    }

    private String generateDetailBarCode(Product product) { // Thay generateDetailCode thành generateDetailBarCode
        return "PD-" + product.getId() + "-" + System.currentTimeMillis();
    }
}