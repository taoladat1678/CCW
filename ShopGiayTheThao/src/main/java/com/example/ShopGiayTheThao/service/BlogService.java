package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ShopGiayTheThao.dto.BlogStatsResponse;
import com.example.ShopGiayTheThao.entity.Blog;
import com.example.ShopGiayTheThao.repository.BlogRepository;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public long getTotalBlogs() {
        return blogRepository.count();
    }

    public BlogStatsResponse getBlogStats() {
        long total = getTotalBlogs();
        return new BlogStatsResponse(total, true);
    }

    /**
     * Lấy toàn bộ danh sách bài viết blog
     */
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
}
