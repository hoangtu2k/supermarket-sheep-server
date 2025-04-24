package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.User;
import com.example.supermarket.supermarketsheepserver.request.UserRequest;
import com.example.supermarket.supermarketsheepserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
public class UserRest {

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserRequest>> getAll() {
        // Lấy danh sách User từ service
        List<User> users = userService.getAllUser();

        // Chuyển đổi danh sách User sang danh sách UserReq trực tiếp
        List<UserRequest> userRequests = users.stream()
                .map(user -> {
                    UserRequest userRequest = new UserRequest();
                    userRequest.setId(user.getId());
                    userRequest.setCode(user.getCode());
                    userRequest.setName(user.getName());
                    userRequest.setPhone(user.getPhone());
                    userRequest.setEmail(user.getEmail());
                    userRequest.setDateOfBirth(user.getDateOfBirth());
                    userRequest.setGender(user.getGender());
                    userRequest.setStatus(user.getStatus());
                    userRequest.setImage(user.getImage());

                    // Kiểm tra xem tài khoản và vai trò có tồn tại không
                    if (user.getAccount() != null && user.getAccount().getRole() != null) {
                        userRequest.setRoleName(user.getAccount().getRole().getName());
                        userRequest.setUsername(user.getAccount().getUsername());
                    } else {
                        userRequest.setRoleName(null); // Đặt là null nếu không có vai trò
                        userRequest.setUsername(null);

                    }

                    return userRequest; // Trả về đối tượng UserReq đã tạo
                })
                .collect(Collectors.toList()); // Thu thập kết quả vào danh sách

        if (userRequests.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 nếu không có người dùng
        }
        return ResponseEntity.ok(userRequests); // Trả về 200 và danh sách người dùng
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.getUserById(id); // Giả sử trả về người dùng theo ID

        if (user != null) {
            // Nếu tìm thấy người dùng, trả về HttpStatus 200 (OK) và thông tin người dùng
            return ResponseEntity.ok(user);
        } else {
            // Nếu không tìm thấy, trả về HttpStatus 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        // Kiểm tra tính hợp lệ của userReq
        if (userRequest == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User createdUser = userService.createUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            // Xử lý lỗi phù hợp (có thể ghi log hoặc trả về thông báo cụ thể hơn)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser); // Trả về 200 OK và đối tượng UserReq đã cập nhật
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        User deleteUser = userService.deleteUser(id);
        return ResponseEntity.ok(deleteUser); // Trả về 200 OK và đối tượng UserReq đã cập nhật
    }

}
