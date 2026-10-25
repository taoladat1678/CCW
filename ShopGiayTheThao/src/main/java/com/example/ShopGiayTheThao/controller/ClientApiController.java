package com.example.ShopGiayTheThao.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.UserService;

@RestController
@RequestMapping("/api")
public class ClientApiController {

    private final UserService userService;

    public ClientApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Khách hàng đăng ký tài khoản mới
     * URL: POST /api/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequest request) {
        // Đảm bảo role khi client đăng ký luôn là CUSTOMER
        request.setRole("CUSTOMER");
        request.setStatus(1);
        User newUser = userService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Khách hàng xem thông tin tài khoản cá nhân
     * URL: GET /api/customers/{id}
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Khách hàng cập nhật thông tin tài khoản cá nhân
     * URL: PUT /api/customers/{id}
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody UserRequest request) {
        // Không cho phép client tự đổi role sang ADMIN
        request.setRole(null);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Kiểm tra nhanh email đã được sử dụng hay chưa
     * URL: GET /api/check-email?email=abc@gmail.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
