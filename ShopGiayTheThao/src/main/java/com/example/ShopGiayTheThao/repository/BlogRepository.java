package com.example.ShopGiayTheThao.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByStatus(Integer status, Pageable pageable);
    
    List<Blog> findTop3ByStatusOrderByCreatedAtDesc(Integer status);
}
