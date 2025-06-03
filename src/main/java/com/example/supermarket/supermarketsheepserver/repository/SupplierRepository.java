package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Supplier;
import com.example.supermarket.supermarketsheepserver.entity.Supplier.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByStatusOrderByCreatedAtDesc(SupplierStatus status);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByCode(String code);
}