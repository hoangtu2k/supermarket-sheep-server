package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.repository.UserRepository;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username) {
        return userRepository.findByUser(username);
    }

    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAllUsersOrderedByCreateDate();
    }

    // Lấy người dùng theo ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Tạo mới người dùng
    public User createUser(UserRequest userRequest) {
        User user = new User();
        if (userRequest.getCode() == null) {
            // Generate a new code automatically if it's null
            String generatedCode = generateUserCode();
            user.setCode(generatedCode);
        } else {
            // Otherwise, set the code from the request
            user.setCode(userRequest.getCode());
        }
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setDateOfBirth(userRequest.getDateOfBirth());

        user.setStatus(1);

        return userRepository.save(user);
    }

    // Cập nhật người dùng
    public User updateUser( Long id, UserRequest userRequest) {
        // Kiểm tra xem người dùng có tồn tại không
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User user = optionalUser.get();
        if (userRequest.getCode() == null) {
            // Generate a new code automatically if it's null
            String generatedCode = generateUserCode();
            user.setCode(generatedCode);
        } else {
            // Otherwise, set the code from the request
            user.setCode(userRequest.getCode());
        }
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setDateOfBirth(userRequest.getDateOfBirth());

        return userRepository.save(user);
    }

    // Thay đổi trạng thái người dùng (xóa hoặc khôi phục)
    public User changeUserStatus(Long userId, Integer newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    private String generateUserCode() {
        return UUID.randomUUID().toString(); // Example using UUID
    }

}
