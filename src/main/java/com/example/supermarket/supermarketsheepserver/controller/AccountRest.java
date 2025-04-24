package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Account;
import com.example.supermarket.supermarketsheepserver.request.AccountRequest;
import com.example.supermarket.supermarketsheepserver.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/account")
public class AccountRest {

    @Autowired
    private AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<AccountRequest>> getAllAccout() {
    // Lấy danh sách User từ service
        List<Account> accounts = accountService.getAllAccount();

        // Chuyển đổi danh sách User sang danh sách UserReq trực tiếp
        List<AccountRequest> accountRequests = accounts.stream()
                .map(account  -> {
                    AccountRequest accountRequest = new AccountRequest();
                    accountRequest.setId(account.getId());
                    accountRequest.setUsername(account.getUsername());
                    return accountRequest; // Trả về đối tượng UserReq đã tạo
                })
                .collect(Collectors.toList()); // Thu thập kết quả vào danh sách

        if (accountRequests.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 nếu không có người dùng
        }
        return ResponseEntity.ok(accountRequests); // Trả về 200 và danh sách người dùng
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> findById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);

        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            // Nếu không tìm thấy, trả về HttpStatus 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Account> createUser(@RequestBody AccountRequest accountRequest) {
        // Kiểm tra tính hợp lệ của userReq
        if (accountRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Account createdAccount = accountService.createAccount(accountRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            // Xử lý lỗi phù hợp (có thể ghi log hoặc trả về thông báo cụ thể hơn)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
