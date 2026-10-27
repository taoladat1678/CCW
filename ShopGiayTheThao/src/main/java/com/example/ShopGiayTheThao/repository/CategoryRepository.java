package com.example.ShopGiayTheThao.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Lấy danh sách danh mục theo trạng thái (status = 1: đang hoạt động)
    List<Category> findByStatus(Integer status);
    // Lọc và tìm kiếm danh mục có phân trang cho Admin theo tên hoặc ID số
    @Query("SELECT c FROM Category c WHERE " +
           "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(c.id, '') LIKE CONCAT('%', :cleanKeyword, '%')) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Category> filterCategories(
            @Param("keyword") String keyword,
            @Param("cleanKeyword") String cleanKeyword,
            @Param("status") Integer status,
            Pageable pageable);
}
