package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {

    List<ProductPhoto> findByProductId(Long productId);

    List<ProductPhoto> findByProductIdAndMainImageTrue(Long productId);

    List<ProductPhoto> findByProductIdAndMainImageFalse(Long productId);

    boolean existsByProductIdAndMainImageTrue(Long productId);

    Optional<ProductPhoto> findByProductAndMainImage(Product product, Boolean mainImage);

    @Modifying
    void deleteByProductAndMainImage(@Param("product") Product product, @Param("mainImage") Boolean mainImage);
}