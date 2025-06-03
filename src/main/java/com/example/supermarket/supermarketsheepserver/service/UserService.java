package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Role;
import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.entity.User.UserStatus;
import com.example.supermarket.supermarketsheepserver.repository.UserRepository;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User login(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }

    public List<UserRequest> getAllUsersAsDto() {
        List<User> users = userRepository.findByStatusOrderByCreatedAtDesc(UserStatus.ACTIVE);
        return users.stream().map(this::mapToUserRequest).collect(Collectors.toList());
    }

    public UserRequest getUserByIdAsDto(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserRequest(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(@Valid UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        User user = User.builder()
                .code(request.getCode() != null ? request.getCode() : generateUserCode())
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth() != null ? LocalDate.parse(request.getDateOfBirth()) : null)
                .status(UserStatus.valueOf(request.getStatus()))
                .role(Role.builder().id(request.getRoleId()).build())
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, @Valid UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        user.setCode(request.getCode() != null ? request.getCode() : generateUserCode());
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth() != null ? LocalDate.parse(request.getDateOfBirth()) : null);
        user.setStatus(UserStatus.valueOf(request.getStatus()));
        user.setRole(Role.builder().id(request.getRoleId()).build());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getRePassword())) {
                throw new IllegalArgumentException("Password and confirm password do not match!");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User changeUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setStatus(UserStatus.valueOf(status));
        return userRepository.save(user);
    }

    private UserRequest mapToUserRequest(User user) {
        return UserRequest.builder()
                .id(user.getId())
                .code(user.getCode())
                .name(user.getName())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .status(user.getStatus().name())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .build();
    }

    private String generateUserCode() {
        return UUID.randomUUID().toString();
    }
}