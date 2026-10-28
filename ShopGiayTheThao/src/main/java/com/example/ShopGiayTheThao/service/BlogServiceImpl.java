package com.example.ShopGiayTheThao.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.BlogDTO;
import com.example.ShopGiayTheThao.dto.BlogRequest;
import com.example.ShopGiayTheThao.dto.BlogStatsResponse;
import com.example.ShopGiayTheThao.entity.Blog;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.repository.BlogRepository;
import com.example.ShopGiayTheThao.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogServiceImpl(BlogRepository blogRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public long getTotalBlogs() {
        return blogRepository.count();
    }

    @Override
    public BlogStatsResponse getBlogStats() {
        long total = getTotalBlogs();
        return new BlogStatsResponse(total, true);
    }

    @Override
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
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

    @Override
    public Page<BlogDTO> getBlogsForAdmin(int page, int size, String keyword, Integer status, String category) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Integer st = (status != null && (status == 0 || status == 1)) ? status : null;

        Page<Blog> blogPage = blogRepository.filterBlogs(kw, st, pageable);

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category.trim())) {
            String catTrim = category.trim().toLowerCase();
            List<BlogDTO> allFiltered = blogRepository.filterBlogs(kw, st, Pageable.unpaged()).getContent().stream()
                    .map(this::toDTO)
                    .filter(b -> b.getCategoryTag() != null && b.getCategoryTag().toLowerCase().contains(catTrim))
                    .collect(Collectors.toList());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allFiltered.size());
            List<BlogDTO> sub = (start <= end) ? allFiltered.subList(start, end) : Collections.emptyList();
            return new org.springframework.data.domain.PageImpl<>(sub, pageable, allFiltered.size());
        }

        return blogPage.map(this::toDTO);
    }

    @Override
    @Transactional
    public BlogDTO createBlog(BlogRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tiêu đề bài viết không được để trống!");
        }

        User author = null;
        if (request.getAuthorId() != null) {
            author = userRepository.findById(request.getAuthorId()).orElse(null);
        }
        if (author == null && request.getAuthorName() != null && !request.getAuthorName().trim().isEmpty()) {
            String authorName = request.getAuthorName().trim();
            author = userRepository.findAll().stream()
                    .filter(u -> authorName.equalsIgnoreCase(u.getFullName()) || authorName.equalsIgnoreCase(u.getEmail()))
                    .findFirst().orElse(null);
        }
        if (author == null) {
            author = userRepository.findFirstByOrderByIdAsc().orElse(null);
        }

        Blog blog = new Blog();
        blog.setTitle(request.getTitle().trim());

        String rawContent = request.getContent() != null ? request.getContent().trim() : "";
        if (request.getCategoryTag() != null && !request.getCategoryTag().trim().isEmpty()) {
            if (!rawContent.startsWith("[Chuyên mục: ")) {
                rawContent = "[Chuyên mục: " + request.getCategoryTag().trim() + "]\n" + rawContent;
            }
        }
        blog.setContent(rawContent);

        blog.setThumbnail(request.getThumbnail() != null && !request.getThumbnail().trim().isEmpty()
                ? request.getThumbnail().trim()
                : "https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=700&q=80");
        blog.setAuthor(author);
        blog.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        Blog saved = blogRepository.save(blog);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public BlogDTO updateBlog(Long id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết với ID: " + id));

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            blog.setTitle(request.getTitle().trim());
        }

        String targetCategory = (request.getCategoryTag() != null && !request.getCategoryTag().trim().isEmpty())
                ? request.getCategoryTag().trim()
                : null;

        if (request.getContent() != null) {
            String content = request.getContent().trim();
            if (content.startsWith("[Chuyên mục: ")) {
                int endIdx = content.indexOf("]");
                if (endIdx > 0) {
                    content = content.substring(endIdx + 1).trim();
                }
            }
            if (targetCategory != null) {
                content = "[Chuyên mục: " + targetCategory + "]\n" + content;
            }
            blog.setContent(content);
        } else if (targetCategory != null) {
            String content = blog.getContent() != null ? blog.getContent() : "";
            if (content.startsWith("[Chuyên mục: ")) {
                int endIdx = content.indexOf("]");
                if (endIdx > 0) {
                    content = content.substring(endIdx + 1).trim();
                }
            }
            blog.setContent("[Chuyên mục: " + targetCategory + "]\n" + content);
        }

        if (request.getThumbnail() != null && !request.getThumbnail().trim().isEmpty()) {
            blog.setThumbnail(request.getThumbnail().trim());
        }
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
        }

        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId()).orElse(null);
            if (author != null) {
                blog.setAuthor(author);
            }
        }

        Blog updated = blogRepository.save(blog);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết để xóa với ID: " + id);
        }
        blogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BlogDTO toggleBlogStatus(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết với ID: " + id));
        int currentStatus = (blog.getStatus() != null) ? blog.getStatus() : 1;
        blog.setStatus(currentStatus == 1 ? 0 : 1);
        return toDTO(blogRepository.save(blog));
    }

    private BlogDTO toDTO(Blog b) {
        String authorName = (b.getAuthor() != null && b.getAuthor().getFullName() != null) ? b.getAuthor().getFullName() : "Nike Newsroom";
        String tag = extractTag(b.getTitle(), b.getContent());
        String displayContent = b.getContent();
        if (displayContent != null && displayContent.startsWith("[Chuyên mục: ")) {
            int endIdx = displayContent.indexOf("]");
            if (endIdx > 0) {
                displayContent = displayContent.substring(endIdx + 1).trim();
            }
        }
        return new BlogDTO(
                b.getId(),
                b.getTitle(),
                displayContent,
                b.getThumbnail(),
                authorName,
                b.getCreatedAt(),
                tag,
                b.getStatus()
        );
    }

    private String extractTag(String title, String content) {
        if (content != null && content.startsWith("[Chuyên mục: ")) {
            int end = content.indexOf("]");
            if (end > 13) {
                return content.substring(13, end).trim();
            }
        }
        String combined = ((title != null ? title : "") + " " + (content != null ? content : "")).toLowerCase();
        if (combined.contains("công nghệ") || combined.contains("dynamic air") || combined.contains("nsrl")) return "Công Nghệ & Đổi Mới";
        if (combined.contains("vận động viên") || combined.contains("kipchoge") || combined.contains("marathon")) return "Vận Động Viên";
        if (combined.contains("streetwear") || combined.contains("phối đồ") || combined.contains("dunk")) return "Phong Cách Streetwear";
        if (combined.contains("dã ngoại") || combined.contains("acg") || combined.contains("trail")) return "Dã Ngoại & ACG";
        if (combined.contains("bảo vệ") || combined.contains("vệ sinh") || combined.contains("chọn size") || combined.contains("cẩm nang")) return "Cẩm Nang Chọn Giày";
        if (combined.contains("di sản") || combined.contains("jordan 1") || combined.contains("lịch sử")) return "Di Sản & Retro";
        return "Công Nghệ & Đổi Mới";
    }
}
