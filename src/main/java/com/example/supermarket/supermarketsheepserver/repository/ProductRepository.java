package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Truy vấn để lấy tất cả sản phẩm và sắp xếp theo ngày tạo giảm dần
    @Query(value = "SELECT * FROM product ORDER BY create_date DESC", nativeQuery = true)
    List<Product> findAllProductsOrderedByCreateDate();

}
