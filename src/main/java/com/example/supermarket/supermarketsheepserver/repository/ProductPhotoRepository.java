package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {

    @Query(value = "select p from ProductPhoto p WHERE p.id = :id")
    ProductPhoto getById(@Param("id") Long id);

    @Query(value = "select p from ProductPhoto p")
    List<ProductPhoto> getAllProductPhoto();


    @Query(value = "SELECT e FROM ProductPhoto e WHERE e.product.id = :id")
    List<ProductPhoto> getAllByIdSP(@Param("id") Long id);

    @Query(value = "SELECT e FROM ProductPhoto e WHERE e.product.id = :id and e.mainImage = true")
    List<ProductPhoto> getAllByIdSP1(@Param("id") Long id);

    @Query(value = "SELECT e FROM ProductPhoto e WHERE e.product.id = :id and e.mainImage = false ")
    List<ProductPhoto> getAllByIdSP0(@Param("id") Long id);

    boolean existsByProductIdAndMainImageTrue(Long productId);

    Optional<ProductPhoto> findByProductAndMainImage(Product product, Boolean mainImage);

    @Modifying
    @Query("DELETE FROM ProductPhoto pp WHERE pp.product = :product AND pp.mainImage = :mainImage")
    void deleteByProductAndMainImage(@Param("product") Product product,
                                     @Param("mainImage") Boolean mainImage);

}
