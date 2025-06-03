package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.repository.ProductPhotoRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductPhotoRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;
    private final ProductRepository productRepository;

    public List<ProductPhoto> getAllProductPhotos() {
        return productPhotoRepository.findAll();
    }

    public ProductPhoto getProductPhotoById(Long id) {
        return productPhotoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product photo not found with id: " + id));
    }

    @Transactional
    public ProductPhoto createProductPhoto(@Valid ProductPhotoRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.getProductId()));

        if (request.getMainImage()) {
            productPhotoRepository.deleteByProductAndMainImage(product, true);
        }

        ProductPhoto productPhoto = ProductPhoto.builder()
                .imageUrl(request.getImageUrl())
                .mainImage(request.getMainImage())
                .product(product)
                .build();

        return productPhotoRepository.save(productPhoto);
    }

    @Transactional
    public void deleteProductPhoto(Long id) {
        ProductPhoto photo = productPhotoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product photo not found with id: " + id));
        productPhotoRepository.delete(photo);
    }
}