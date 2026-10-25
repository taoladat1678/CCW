package com.example.ShopGiayTheThao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Đếm số lượng người dùng theo role (ví dụ: "CUSTOMER")
    long countByRole(String role);

    // Đếm không phân biệt chữ hoa thường
    long countByRoleIgnoreCase(String role);

    // Lấy danh sách người dùng theo role
    List<User> findByRole(String role);

    // Tìm kiếm theo email
    Optional<User> findByEmail(String email);

    // Kiểm tra trùng email
    boolean existsByEmail(String email);
}
