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
        String catLower = category.trim().toLowerCase();
        List<BlogDTO> all = blogRepository.findByStatus(1, org.springframework.data.domain.Pageable.unpaged()).getContent().stream()
                .map(this::toDTO)
                .filter(b -> {
                    if (b.getCategoryTag() == null) return false;
                    String tagLower = b.getCategoryTag().toLowerCase();
                    return tagLower.equalsIgnoreCase(catLower)
                            || tagLower.contains(catLower)
                            || catLower.contains(tagLower);
                })
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

        String inputAuthorName = (request.getAuthorName() != null && !request.getAuthorName().trim().isEmpty())
                ? request.getAuthorName().trim()
                : null;

        User author = null;
        if (request.getAuthorId() != null) {
            author = userRepository.findById(request.getAuthorId()).orElse(null);
        }
        if (author == null && inputAuthorName != null) {
            author = userRepository.findAll().stream()
                    .filter(u -> inputAuthorName.equalsIgnoreCase(u.getFullName()) || inputAuthorName.equalsIgnoreCase(u.getEmail()))
                    .findFirst().orElse(null);
        }
        if (author == null) {
            // Find an ADMIN user first, or first user in DB to satisfy foreign key
            author = userRepository.findAll().stream()
                    .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
                    .findFirst()
                    .orElseGet(() -> userRepository.findFirstByOrderByIdAsc().orElse(null));
        }

        Blog blog = new Blog();
        blog.setTitle(request.getTitle().trim());
        blog.setAuthor(author);

        String finalAuthorName = (inputAuthorName != null)
                ? inputAuthorName
                : ((author != null && author.getFullName() != null) ? author.getFullName() : "Nike Newsroom");
        blog.setAuthorName(finalAuthorName);

        String rawContent = cleanDisplayContent(request.getContent() != null ? request.getContent().trim() : "");
        StringBuilder sb = new StringBuilder();
        if (request.getCategoryTag() != null && !request.getCategoryTag().trim().isEmpty()) {
            sb.append("[Chuyên mục: ").append(request.getCategoryTag().trim()).append("]\n");
        }
        if (inputAuthorName != null && !inputAuthorName.isEmpty()) {
            sb.append("[Tác giả: ").append(inputAuthorName).append("]\n");
        }
        sb.append(rawContent);
        blog.setContent(sb.toString());

        blog.setThumbnail(request.getThumbnail() != null && !request.getThumbnail().trim().isEmpty()
                ? request.getThumbnail().trim()
                : "https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=700&q=80");
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

        String inputAuthorName = (request.getAuthorName() != null && !request.getAuthorName().trim().isEmpty())
                ? request.getAuthorName().trim()
                : null;

        if (inputAuthorName != null) {
            blog.setAuthorName(inputAuthorName);
            User matchedAuthor = userRepository.findAll().stream()
                    .filter(u -> inputAuthorName.equalsIgnoreCase(u.getFullName()) || inputAuthorName.equalsIgnoreCase(u.getEmail()))
                    .findFirst().orElse(null);
            if (matchedAuthor != null) {
                blog.setAuthor(matchedAuthor);
            }
        } else if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId()).orElse(null);
            if (author != null) {
                blog.setAuthor(author);
                blog.setAuthorName(author.getFullName());
            }
        }

        String targetCategory = (request.getCategoryTag() != null && !request.getCategoryTag().trim().isEmpty())
                ? request.getCategoryTag().trim()
                : null;

        String baseContent = cleanDisplayContent(request.getContent() != null ? request.getContent().trim() : blog.getContent());
        String currentCat = (targetCategory != null) ? targetCategory : extractTag(blog.getTitle(), blog.getContent());
        String currentAuthor = (blog.getAuthorName() != null && !blog.getAuthorName().trim().isEmpty())
                ? blog.getAuthorName().trim()
                : inputAuthorName;

        StringBuilder sb = new StringBuilder();
        if (currentCat != null && !currentCat.trim().isEmpty()) {
            sb.append("[Chuyên mục: ").append(currentCat.trim()).append("]\n");
        }
        if (currentAuthor != null && !currentAuthor.trim().isEmpty()) {
            sb.append("[Tác giả: ").append(currentAuthor.trim()).append("]\n");
        }
        sb.append(baseContent);
        blog.setContent(sb.toString());

        if (request.getThumbnail() != null && !request.getThumbnail().trim().isEmpty()) {
            blog.setThumbnail(request.getThumbnail().trim());
        }
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
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
        String authorName = null;
        if (b.getAuthorName() != null && !b.getAuthorName().trim().isEmpty()) {
            authorName = b.getAuthorName().trim();
        } else if (b.getContent() != null && b.getContent().contains("[Tác giả: ")) {
            authorName = extractAuthorFromContent(b.getContent());
        }

        if (authorName == null || authorName.trim().isEmpty()) {
            authorName = (b.getAuthor() != null && b.getAuthor().getFullName() != null)
                    ? b.getAuthor().getFullName()
                    : "Nike Newsroom";
        }

        String tag = extractTag(b.getTitle(), b.getContent());
        String displayContent = cleanDisplayContent(b.getContent());

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

    private String extractAuthorFromContent(String content) {
        if (content == null) return null;
        int start = content.indexOf("[Tác giả: ");
        if (start >= 0) {
            int end = content.indexOf("]", start);
            if (end > start + 10) {
                return content.substring(start + 10, end).trim();
            }
        }
        return null;
    }

    private String cleanDisplayContent(String rawContent) {
        if (rawContent == null) return "";
        String content = rawContent.trim();
        boolean changed = true;
        while (changed) {
            changed = false;
            if (content.startsWith("[Chuyên mục: ")) {
                int end = content.indexOf("]");
                if (end > 0) {
                    content = content.substring(end + 1).trim();
                    changed = true;
                }
            }
            if (content.startsWith("[Tác giả: ")) {
                int end = content.indexOf("]");
                if (end > 0) {
                    content = content.substring(end + 1).trim();
                    changed = true;
                }
            }
        }
        return content;
    }

    private String extractTag(String title, String content) {
        if (content != null && content.contains("[Chuyên mục: ")) {
            int start = content.indexOf("[Chuyên mục: ");
            int end = content.indexOf("]", start);
            if (end > start + 13) {
                return content.substring(start + 13, end).trim();
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
