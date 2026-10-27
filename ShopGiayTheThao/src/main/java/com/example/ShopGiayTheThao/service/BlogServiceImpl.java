package com.example.ShopGiayTheThao.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShopGiayTheThao.dto.BlogDTO;
import com.example.ShopGiayTheThao.entity.Blog;
import com.example.ShopGiayTheThao.repository.BlogRepository;

@Service
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public Page<BlogDTO> getBlogsPaginated(Pageable pageable) {
        return getBlogsPaginated(pageable, null);
    }

    @Override
    public Page<BlogDTO> getBlogsPaginated(Pageable pageable, String category) {
        if (category == null || category.trim().isEmpty() || "ALL".equalsIgnoreCase(category)) {
            return blogRepository.findByStatus(1, pageable).map(this::toDTO);
        }
        List<BlogDTO> all = blogRepository.findByStatus(1, org.springframework.data.domain.Pageable.unpaged()).getContent().stream()
                .map(this::toDTO)
                .filter(b -> b.getCategoryTag() != null && b.getCategoryTag().equalsIgnoreCase(category.trim()))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), all.size());
        List<BlogDTO> sub = (start <= end) ? all.subList(start, end) : Collections.emptyList();
        return new org.springframework.data.domain.PageImpl<>(sub, pageable, all.size());
    }

    @Override
    public List<BlogDTO> getLatestBlogs() {
        return blogRepository.findTop3ByStatusOrderByCreatedAtDesc(1).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        return blogRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    private BlogDTO toDTO(Blog b) {
        String authorName = (b.getAuthor() != null) ? b.getAuthor().getFullName() : "Nike Editorial Team";
        String tag = extractTag(b.getTitle(), b.getContent());
        return new BlogDTO(
                b.getId(),
                b.getTitle(),
                b.getContent(),
                b.getThumbnail(),
                authorName,
                b.getCreatedAt(),
                tag
        );
    }

    private String extractTag(String title, String content) {
        String combined = ((title != null ? title : "") + " " + (content != null ? content : "")).toLowerCase();
        if (combined.contains("công nghệ") || combined.contains("dynamic air") || combined.contains("nsrl")) return "Công Nghệ";
        if (combined.contains("vận động viên") || combined.contains("kipchoge") || combined.contains("marathon")) return "Vận Động Viên";
        if (combined.contains("streetwear") || combined.contains("phối đồ") || combined.contains("dunk")) return "Phong Cách";
        if (combined.contains("dã ngoại") || combined.contains("acg") || combined.contains("trail")) return "Dã Ngoại";
        if (combined.contains("bảo vệ") || combined.contains("vệ sinh") || combined.contains("chọn size")) return "Cẩm Nang";
        if (combined.contains("di sản") || combined.contains("jordan 1") || combined.contains("lịch sử")) return "Di Sản";
        return "Đổi Mới";
    }
}
