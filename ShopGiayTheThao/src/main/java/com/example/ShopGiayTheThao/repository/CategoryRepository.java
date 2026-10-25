package com.example.ShopGiayTheThao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Lấy danh sách danh mục theo trạng thái (status = 1: đang hoạt động)
    List<Category> findByStatus(Integer status);
}
