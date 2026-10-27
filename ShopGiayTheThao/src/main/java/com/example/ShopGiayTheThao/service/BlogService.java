package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ShopGiayTheThao.dto.BlogDTO;
import com.example.ShopGiayTheThao.dto.BlogStatsResponse;
import com.example.ShopGiayTheThao.entity.Blog;

public interface BlogService {

    long getTotalBlogs();

    BlogStatsResponse getBlogStats();

    List<Blog> getAllBlogs();

    Page<BlogDTO> getBlogsPaginated(Pageable pageable);

    Page<BlogDTO> getBlogsPaginated(Pageable pageable, String category);

    List<BlogDTO> getLatestBlogs();

    BlogDTO getBlogById(Long id);
}
