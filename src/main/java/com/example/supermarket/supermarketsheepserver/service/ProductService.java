package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductDetails;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.repository.ProductDetailsRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductPhotoRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductDetailsRequest;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductPhotoRepository productPhotoRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllWithPhotosAndDetails(); // Custom query to fetch all needed data
    }

    // Thêm sản phẩm
    public Product createProduct(ProductRequest productRequest) {
        // Validate required fields
        if (productRequest.getName() == null || productRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        // Create and save the product
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .weight(productRequest.getWeight())
                .quantity(productRequest.getQuantity() != null ? productRequest.getQuantity() : 0)
                .status(1) // Default active status
                .createDate(new Date())
                .build();

        Product savedProduct = productRepository.save(product);

        // Save product details if provided
        if (productRequest.getProductDetails() != null && !productRequest.getProductDetails().isEmpty()) {
            saveProductDetails(productRequest.getProductDetails(), savedProduct);
        }

        // Save product photos if provided
        if (productRequest.getMainImage() != null ||
                (productRequest.getNotMainImages() != null && !productRequest.getNotMainImages().isEmpty())) {
            saveProductPhotos(productRequest, savedProduct);
        }

        return savedProduct;
    }

    private void saveProductDetails(List<ProductDetailsRequest> detailsRequests, Product product) {
        List<ProductDetails> details = detailsRequests.stream()
                .map(detailReq -> {
                    String code = detailReq.getCode() != null ?
                            detailReq.getCode() : generateDetailCode(product);

                    return ProductDetails.builder()
                            .product(product)
                            .code(code)
                            .unit(detailReq.getUnit() != null ? detailReq.getUnit() : "unit")
                            .conversionRate(detailReq.getConversionRate() != null ?
                                    detailReq.getConversionRate() : 1)
                            .price(detailReq.getPrice() != null ?
                                    detailReq.getPrice() : BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());

        productDetailsRepository.saveAll(details);
    }

    private void saveProductPhotos(ProductRequest productRequest, Product product) {
        List<ProductPhoto> photos = new ArrayList<>();

        // Add main image if exists
        if (productRequest.getMainImage() && productRequest.getImageUrl() != null) {
            photos.add(ProductPhoto.builder()
                    .product(product)
                    .imageUrl(productRequest.getImageUrl())
                    .mainImage(true)
                    .build());
        }

        // Add additional images if exist
        if (productRequest.getNotMainImages() != null) {
            productRequest.getNotMainImages().forEach(url -> {
                photos.add(ProductPhoto.builder()
                        .product(product)
                        .imageUrl(url)
                        .mainImage(false)
                        .build());
            });
        }

        if (!photos.isEmpty()) {
            productPhotoRepository.saveAll(photos);
        }
    }

    // Cập nhật sản phẩm
    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) {
        // 1. Tìm sản phẩm hiện có
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        // 2. Cập nhật thông tin cơ bản
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setWeight(productRequest.getWeight());
        existingProduct.setQuantity(productRequest.getQuantity() != null ? productRequest.getQuantity() : 0);

        if (productRequest.getStatus() != null) {
            existingProduct.setStatus(productRequest.getStatus());
        }

        // 3. Cập nhật chi tiết sản phẩm
        updateProductDetails(existingProduct, productRequest.getProductDetails());

        // 4. Cập nhật hình ảnh
        updateProductPhotos(existingProduct, productRequest);

        // 5. Lưu và trả về sản phẩm đã cập nhật
        return productRepository.save(existingProduct);
    }

    private void updateProductDetails(Product product, List<ProductDetailsRequest> detailsRequests) {
        if (detailsRequests == null) {
            return;
        }

        // Lấy danh sách ID của các chi tiết mới
        List<Long> newDetailIds = detailsRequests.stream()
                .map(ProductDetailsRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Xóa các chi tiết cũ không có trong request mới
        if (!newDetailIds.isEmpty()) {
            productDetailsRepository.deleteByProductAndIdNotIn(product, newDetailIds);
        } else {
            productDetailsRepository.deleteByProduct(product);
        }

        // Cập nhật hoặc thêm mới chi tiết
        List<ProductDetails> updatedDetails = new ArrayList<>();

        for (ProductDetailsRequest detailReq : detailsRequests) {
            if (detailReq.getId() != null) {
                // Cập nhật chi tiết hiện có
                ProductDetails existingDetail = productDetailsRepository.findById(detailReq.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Product detail not found: " + detailReq.getId()));

                existingDetail.setUnit(detailReq.getUnit() != null ? detailReq.getUnit() : "unit");
                existingDetail.setConversionRate(detailReq.getConversionRate() != null ? detailReq.getConversionRate() : 1);
                existingDetail.setPrice(detailReq.getPrice() != null ? detailReq.getPrice() : BigDecimal.ZERO);
                updatedDetails.add(existingDetail);
            } else {
                // Thêm chi tiết mới
                ProductDetails newDetail = ProductDetails.builder()
                        .product(product)
                        .code(detailReq.getCode() != null ? detailReq.getCode() : generateDetailCode(product))
                        .unit(detailReq.getUnit() != null ? detailReq.getUnit() : "unit")
                        .conversionRate(detailReq.getConversionRate() != null ? detailReq.getConversionRate() : 1)
                        .price(detailReq.getPrice() != null ? detailReq.getPrice() : BigDecimal.ZERO)
                        .build();
                updatedDetails.add(newDetail);
            }
        }

        productDetailsRepository.saveAll(updatedDetails);
    }

    private void updateProductPhotos(Product product, ProductRequest productRequest) {
        if (productRequest.getMainImage() == null && productRequest.getNotMainImages() == null) {
            return;
        }

        // Xử lý ảnh chính
        if (productRequest.getMainImage() != null) {
            if (productRequest.getMainImage() && productRequest.getImageUrl() != null) {
                Optional<ProductPhoto> existingMainPhoto = productPhotoRepository.findByProductAndMainImage(product, true);

                if (existingMainPhoto.isPresent()) {
                    ProductPhoto mainPhoto = existingMainPhoto.get();
                    if (!mainPhoto.getImageUrl().equals(productRequest.getImageUrl())) {
                        mainPhoto.setImageUrl(productRequest.getImageUrl());
                        productPhotoRepository.save(mainPhoto);
                    }
                } else {
                    productPhotoRepository.save(ProductPhoto.builder()
                            .product(product)
                            .imageUrl(productRequest.getImageUrl())
                            .mainImage(true)
                            .build());
                }
            } else if (!productRequest.getMainImage()) {
                productPhotoRepository.deleteByProductAndMainImage(product, true);
            }
        }

        // Xử lý ảnh phụ
        if (productRequest.getNotMainImages() != null) {
            productPhotoRepository.deleteByProductAndMainImage(product, false);

            if (!productRequest.getNotMainImages().isEmpty()) {
                List<ProductPhoto> additionalPhotos = productRequest.getNotMainImages().stream()
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

    // Thay đổi trạng thái sản phẩm (xóa hoặc khôi phục)
    public Product changeProductStatus(Long productId, Integer newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    // import
//    public void importExel(MultipartFile file) throws IOException {
//        try (InputStream inputStream = file.getInputStream();
//             Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
//            for (Row row : sheet) {
//                if (row.getRowNum() > 0) { // Skip header
//                    String code = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : generateCode();
//                    String name = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
//                    String url = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
//                    Double price = row.getCell(3) != null ? row.getCell(3).getNumericCellValue() : null;
//                    Double qty = row.getCell(4) != null ? row.getCell(4).getNumericCellValue() : null;
//                    Double weight = row.getCell(5) != null ? row.getCell(5).getNumericCellValue() : null;
//                    String description = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
//
//                    if (name != null && url != null && price != null && qty != null && weight != null) {
//                        // Check if product with the same code already exists
//                        Optional<Product> existingProductOpt = productRepository.findFirstByCode(code);
//
//                        Product product;
//                        if (existingProductOpt.isPresent()) {
//                            // Product exists, update quantity
//                            product = existingProductOpt.get();
//                            product.setQuantity(product.getQuantity() + qty.intValue());  // Add new quantity
//                            product.setPrice(BigDecimal.valueOf(price));  // Update price if needed (optional)
//                            product.setWeight(weight);  // Update weight if needed (optional)
//                            product.setDescription(description);  // Update description if needed (optional)
//                            product.setUpdateDate(new Date());  // Set updated date
//                        } else {
//                            // Product does not exist, create new product
//                            product = new Product();
//                            product.setCode(code);
//                            product.setName(name);
//                            product.setPrice(BigDecimal.valueOf(price));
//                            product.setQuantity(qty.intValue());
//                            product.setWeight(weight);
//                            product.setDescription(description);
//                            product.setStatus(1);
//                            product.setCreateDate(new Date());
//                        }
//
//                        // Save the product (whether it's new or updated)
//                        productRepository.save(product);
//
//                        // Save product photo
//                        ProductPhoto productPhoto = new ProductPhoto();
//                        productPhoto.setMainImage(true);
//                        productPhoto.setImageUrl(url);
//                        productPhoto.setProduct(product);
//                        productPhotoRepository.save(productPhoto);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new IOException("Failed to import Excel file: " + e.getMessage());
//        }
//    }


    private String generateDetailCode(Product product) {
        return "PD-" + product.getId() + "-" + System.currentTimeMillis();
    }

}
