package com.example.ShopGiayTheThao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserService userService;

    public AdminApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API hiển thị tổng số khách hàng trả về dữ liệu JSON
     * URL: GET /api/admin/stats/customers/count hoặc /api/admin/customers/count
     */
    @GetMapping({ "/stats/customers/count", "/customers/count" })
    public ResponseEntity<Map<String, Object>> getCustomerCount() {
        long count = userService.getTotalCustomers();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalCustomers", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách toàn bộ khách hàng / người dùng
     * URL: GET /api/admin/users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Lấy chi tiết 1 khách hàng theo ID
     * URL: GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Thêm mới khách hàng / người dùng
     * URL: POST /api/admin/users
     */
    @PostMapping("/users")
    public ResponseEntity<User> create(@RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin khách hàng
     * URL: PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Xóa khách hàng
     * URL: DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
