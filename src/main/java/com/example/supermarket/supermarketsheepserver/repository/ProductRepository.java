package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Truy vấn để lấy tất cả sản phẩm và sắp xếp theo ngày tạo giảm dần
    @Query(value = "SELECT * FROM product ORDER BY create_date DESC", nativeQuery = true)
    List<Product> getAllProducts();

    @Query(value = "select u from Product u WHERE u.id = :id")
    Product getById(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.productPhotos " +
            "LEFT JOIN FETCH p.productDetails " +
            "ORDER BY p.createDate DESC")
    List<Product> findAllWithPhotosAndDetails();


}
