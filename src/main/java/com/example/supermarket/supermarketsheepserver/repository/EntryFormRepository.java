package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.EntryForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntryFormRepository extends JpaRepository<EntryForm, Long> {

    @Query("SELECT ef FROM EntryForm ef LEFT JOIN FETCH ef.entryDetails WHERE ef.id = :entryFormId")
    Optional<EntryForm> findByIdWithDetails(@Param("entryFormId") String entryFormId);
    
}
