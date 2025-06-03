package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.EntryForm;
import com.example.supermarket.supermarketsheepserver.entity.EntryForm.EntryFormStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryFormRepository extends JpaRepository<EntryForm, Long> {

    @Query("SELECT DISTINCT ef FROM EntryForm ef " +
            "LEFT JOIN FETCH ef.entryDetails " +
            "WHERE ef.status = :status " +
            "ORDER BY ef.createdAt DESC")
    List<EntryForm> findByStatusWithDetails(EntryFormStatus status);
}