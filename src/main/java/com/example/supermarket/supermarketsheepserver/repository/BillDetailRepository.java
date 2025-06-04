package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.BillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetails, Long> {
}
