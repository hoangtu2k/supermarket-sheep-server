package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import com.example.supermarket.supermarketsheepserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserRest {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserRequest>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersAsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRequest> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserByIdAsDto(id));
    }

    @PostMapping
    public ResponseEntity<UserRequest> createUser(@Valid @RequestBody UserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(201).body(userService.getUserByIdAsDto(user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRequest> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(userService.getUserByIdAsDto(updatedUser.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserRequest> changeUserStatus(@PathVariable Long id, @RequestParam String status) {
        User updatedUser = userService.changeUserStatus(id, status);
        return ResponseEntity.ok(userService.getUserByIdAsDto(updatedUser.getId()));
    }
}