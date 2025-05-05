package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

    @Modifying
    @Query("DELETE FROM ProductDetails pd WHERE pd.product = :product AND pd.id NOT IN :ids")
    void deleteByProductAndIdNotIn(@Param("product") Product product,
                                   @Param("ids") List<Long> ids);

    @Modifying
    @Query("DELETE FROM ProductDetails pd WHERE pd.product = :product")
    void deleteByProduct(@Param("product") Product product);

}
