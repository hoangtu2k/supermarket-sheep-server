package com.example.supermarket.supermarketsheepserver.repository;

import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.entity.User.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier OR u.phone = :identifier")
    User findByIdentifier(@Param("identifier") String identifier);

    List<User> findByStatusOrderByCreatedAtDesc(UserStatus status);
}