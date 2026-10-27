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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ShopGiayTheThao.dto.BlogDTO;

public interface BlogService {
    Page<BlogDTO> getBlogsPaginated(Pageable pageable);
    Page<BlogDTO> getBlogsPaginated(Pageable pageable, String category);
    List<BlogDTO> getLatestBlogs();
    BlogDTO getBlogById(Long id);

}
