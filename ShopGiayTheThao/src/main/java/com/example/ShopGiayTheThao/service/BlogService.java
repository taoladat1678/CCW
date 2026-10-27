package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ShopGiayTheThao.dto.BlogDTO;

public interface BlogService {
    Page<BlogDTO> getBlogsPaginated(Pageable pageable);
    Page<BlogDTO> getBlogsPaginated(Pageable pageable, String category);
    List<BlogDTO> getLatestBlogs();
    BlogDTO getBlogById(Long id);
}
