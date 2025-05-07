package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select u from Product u WHERE u.id = :id")
    Product getById(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.productPhotos " +
            "LEFT JOIN FETCH p.productDetails " +
            "ORDER BY p.createDate DESC")
    List<Product> findAllWithPhotosAndDetails();


}
