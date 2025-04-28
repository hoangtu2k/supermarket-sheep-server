package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Role;
import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.repository.UserRepository;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        // Validate đầu vào
        validateUserRequest(userRequest, true);

        // Check trùng username
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        // Tạo mới User
        User user = new User();
        String code = (userRequest.getCode() == null) ? generateUserCode() : userRequest.getCode();

        user.setCode(code);
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setStatus(1);

        if (userRequest.getRoleId() != null) {
            user.setRole(Role.builder().id(userRequest.getRoleId()).build());
        }

        return userRepository.save(user);
    }

    // Cập nhật người dùng
    public User updateUser(Long id, UserRequest userRequest) {
        // Tìm user theo id
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));

        // Validate đầu vào (update mode)
        validateUserRequest(userRequest, false);

        // Check trùng username (nếu có đổi username)
        if (!user.getUsername().equals(userRequest.getUsername())
                && userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        // Set lại các field
        String code = (userRequest.getCode() == null) ? generateUserCode() : userRequest.getCode();
        user.setCode(code);
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setDateOfBirth(userRequest.getDateOfBirth());

        // Nếu có nhập password mới, mới cập nhật
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            if (!userRequest.getPassword().equals(userRequest.getRePassword())) {
                throw new IllegalArgumentException("Mật khẩu và Nhập lại mật khẩu không khớp!");
            }
            validatePassword(userRequest.getPassword());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        // Set role
        if (userRequest.getRoleId() != null) {
            user.setRole(Role.builder().id(userRequest.getRoleId()).build());
        }

        return userRepository.save(user);
    }

    // Thay đổi trạng thái người dùng (xóa hoặc khôi phục)
    public User changeUserStatus(Long userId, Integer newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    // Validate toàn bộ UserRequest
    private void validateUserRequest(UserRequest userRequest, boolean isCreate) {
        validateName(userRequest.getName());
        validateUsername(userRequest.getUsername());
        validateEmail(userRequest.getEmail());

        if (isCreate || (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty())) {
            validatePassword(userRequest.getPassword());

            if (!userRequest.getPassword().equals(userRequest.getRePassword())) {
                throw new IllegalArgumentException("Mật khẩu và Nhập lại mật khẩu không khớp!");
            }
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
        }

        username = username.trim();

        // Kiểm tra độ dài tối đa
        if (username.length() > 24) {
            throw new IllegalArgumentException("Tên đăng nhập không được vượt quá 24 ký tự!");
        }

        // Kiểm tra khoảng trắng bên trong username
        if (username.contains(" ")) {
            throw new IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng!");
        }

        // Kiểm tra ký tự hợp lệ: chỉ cho phép chữ, số, _ và .
        String usernameRegex = "^[A-Za-z0-9_.]+$";
        if (!username.matches(usernameRegex)) {
            throw new IllegalArgumentException("Tên đăng nhập chỉ được chứa chữ cái, số, dấu chấm (.) hoặc dấu gạch dưới (_).");
        }
    }

    private void validatePassword(String password) {
        if (password != null && !password.isEmpty() && password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự!");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người dùng không được để trống!");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Tên người dùng không được vượt quá 100 ký tự!");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            // Email trống thì bỏ qua, không validate
            return;
        }

        email = email.trim();

        // Kiểm tra độ dài tối đa
        if (email.length() > 255) {
            throw new IllegalArgumentException("Email không được vượt quá 255 ký tự!");
        }

        // Kiểm tra định dạng email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
    }


    private String generateUserCode() {
        return UUID.randomUUID().toString(); // Example using UUID
    }

}
