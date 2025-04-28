package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import com.example.supermarket.supermarketsheepserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class UserRest {

    @Autowired
    private UserService userService;

    // Lấy danh sách người dùng
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users != null ? users : Collections.emptyList());
    }

    // Lấy 1 nguời dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới nguời dùng
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User savedUser = userService.createUser(userRequest);
        return ResponseEntity.ok(savedUser);
    }

    // Cập nhật nguời dùng
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            User updatedUser = userService.updateUser(id, userRequest);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thay đổi trạng thái nguời dùng
    @PutMapping("/{id}/status")
    public ResponseEntity<User> changeUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User updatedUser = userService.changeUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }



}
