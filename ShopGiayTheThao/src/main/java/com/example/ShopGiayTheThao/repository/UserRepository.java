package com.example.ShopGiayTheThao.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(u.id, '') LIKE CONCAT('%', :cleanKeyword, '%')) " +
           "AND (:role IS NULL OR UPPER(u.role) = UPPER(:role)) " +
           "AND (:status IS NULL OR u.status = :status)")
    Page<User> filterUsers(
            @Param("keyword") String keyword,
            @Param("cleanKeyword") String cleanKeyword,
            @Param("role") String role,
            @Param("status") Integer status,
            Pageable pageable);


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
    Optional<User> findFirstByOrderByIdAsc();
}
