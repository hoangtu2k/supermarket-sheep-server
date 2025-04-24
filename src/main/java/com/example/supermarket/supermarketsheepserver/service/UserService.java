package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Account;
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

    public List<User> getAllUser() {
        return userRepository.getAllUser();
    }

    public User getUserById(Long id) {
        // Tìm người dùng theo ID
        return userRepository.getById(id);
    }

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
        user.setGender(userRequest.getGender());

        if (userRequest.getImage() != null) {
            user.setImage(userRequest.getImage());
        } else {
            user.setImage(null);
        }
        
        if (userRequest.getAccountId() != null) {
             user.setAccount(Account.builder().id(userRequest.getAccountId()).build());
        } else {
            user.setAccount(null);
        }

        user.setStatus(1);

        return userRepository.save(user);
    }

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
        user.setGender(userRequest.getGender());

        user.setImage(userRequest.getImage());

        // Check the accountId provided in the request
        Long accountId = userRequest.getAccountId();
        if (accountId != null) {
            // Check if any other user already uses this account ID
            List<User> allUsers = userRepository.getAllUser(); // Fetch all users
            boolean accountUsed = allUsers.stream()
                    .anyMatch(existingUser -> existingUser.getAccount() != null &&
                            existingUser.getAccount().getId().equals(accountId) &&
                            !existingUser.getId().equals(id)); // Exclude the current user

            if (accountUsed) {
                user.setAccount(null);
                System.out.println("Đã có user sử dụng tài khoản này");
            } else {
                user.setAccount(Account.builder().id(accountId).build());
            }
        } else {
            user.setAccount(null); // Set account to null if accountId is not provided
        }

        return userRepository.save(user);
    }

    public User deleteUser(Long id) {
        // Kiểm tra xem người dùng có tồn tại không
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User user = optionalUser.get();

        if (user.getAccount().getRole().getName().equals("Admin")) {
            System.out.println("Không thể xóa tài khoản này");
            return userRepository.save(user);
        } else {
            user.setStatus(0);
            return userRepository.save(user);
        }

    }

    private String generateUserCode() {
        return UUID.randomUUID().toString(); // Example using UUID
    }

}
