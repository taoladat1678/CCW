package com.example.ShopGiayTheThao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
}
