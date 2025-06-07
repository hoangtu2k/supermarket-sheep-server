package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.Product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productDetails pd WHERE p.status = :status ORDER BY p.createDate DESC")
    List<Product> findByStatus(ProductStatus status);

    // Check if a product with the given code exists
    boolean existsByCode(String code);

    // Check if a product with the given code exists, excluding a specific product ID
    boolean existsByCodeAndIdNot(String code, Long id);

    // Check if a barcode exists in any product detail
    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM ProductDetails pd WHERE pd.barCode = :barCode")
    boolean existsByProductDetailsBarCode(String barCode);

    // Check if a barcode exists, excluding product details of a specific product ID
    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM ProductDetails pd WHERE pd.barCode = :barCode AND pd.product.id != :excludeProductId")
    boolean existsByProductDetailsBarCodeAndProductIdNot(String barCode, Long excludeProductId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.productDetails pd " +
            "WHERE p.status = :status AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(pd.barCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "p.name IS NULL OR pd.barCode IS NULL)")
    List<Product> findByNameOrBarCodeContainingIgnoreCase(@Param("keyword") String keyword, @Param("status") ProductStatus status);

}