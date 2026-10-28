package com.example.ShopGiayTheThao.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    Page<Blog> findByStatus(Integer status, Pageable pageable);
    
    List<Blog> findTop3ByStatusOrderByCreatedAtDesc(Integer status);

    @Query("SELECT b FROM Blog b WHERE " +
           "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR b.status = :status)")
    Page<Blog> filterBlogs(
            @Param("keyword") String keyword,
            @Param("status") Integer status,
            Pageable pageable);
}
