package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.Customer;
import com.example.supermarket.supermarketsheepserver.entity.Customer.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.status = :status ORDER BY c.createdAt DESC")
    List<Customer> findByStatus(CustomerStatus status);

    @Query("SELECT C FROM Customer C WHERE C.username = :identifier OR C.email = :identifier OR C.phone = :identifier")
    Customer findByIdentifier(@Param("identifier") String identifier);

}