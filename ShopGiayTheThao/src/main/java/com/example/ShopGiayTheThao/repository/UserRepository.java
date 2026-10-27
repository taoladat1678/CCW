package com.example.ShopGiayTheThao.repository;


import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // Đếm số lượng người dùng theo vai trò nếu cần lọc riêng
    long countByRole(String role);

    // Đếm số lượng không phân biệt chữ hoa/thường theo vai trò
    long countByRoleIgnoreCase(String role);

    // Lấy danh sách người dùng theo vai trò
    List<User> findByRole(String role);

    // Tìm kiếm theo email
    Optional<User> findByEmail(String email);

    // Kiểm tra trùng email
    boolean existsByEmail(String email);
    // Lấy 5 người dùng mới nhất theo ngày tạo
    List<User> findTop5ByOrderByCreatedAtDesc();

    // Fallback theo ID giảm dần
    List<User> findTop5ByOrderByIdDesc();

    Optional<User> findByEmail(String email);
    Optional<User> findFirstByOrderByIdAsc();

}
