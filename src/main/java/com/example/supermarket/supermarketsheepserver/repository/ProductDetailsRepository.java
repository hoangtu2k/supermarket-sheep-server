package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

    List<ProductDetails> findByProduct(@Param("product") Product product);

    @Modifying
    void deleteByProductAndIdNotIn(@Param("product") Product product, @Param("ids") List<Long> ids);

    @Modifying
    void deleteByProduct(@Param("product") Product product);

    // Sửa phương thức để dùng unitId thay vì Unit enum
    Optional<ProductDetails> findByProductIdAndUnitId(@Param("productId") Long productId, @Param("unitId") Long unitId);
}