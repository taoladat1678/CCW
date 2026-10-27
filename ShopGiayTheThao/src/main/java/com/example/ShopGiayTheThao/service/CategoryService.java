package com.example.ShopGiayTheThao.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Lấy danh sách danh mục phân trang, tìm kiếm và lọc trạng thái cho Admin
     */
    @Transactional(readOnly = true)
    public Page<Category> getCategoriesForAdmin(int page, int size, String keyword, String status) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 6;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKw = kw;
        if (cleanKw != null) {
            String lower = cleanKw.toLowerCase();
            if (lower.startsWith("cat-")) {
                cleanKw = cleanKw.substring(4).trim();
            } else if (lower.startsWith("cat")) {
                cleanKw = cleanKw.substring(3).trim();
            }
            // Bỏ các số 0 ở đầu nếu người dùng tìm kiếm dạng CAT-001 hoặc 001
            String unpadded = cleanKw.replaceFirst("^0+", "");
            if (!unpadded.isEmpty()) {
                cleanKw = unpadded;
            }
        }

        Integer st = null;
        if (status != null && !status.trim().isEmpty()) {
            String stLower = status.trim().toLowerCase();
            if ("active".equals(stLower) || "1".equals(stLower)) {
                st = 1;
            } else if ("hidden".equals(stLower) || "inactive".equals(stLower) || "0".equals(stLower)) {
                st = 0;
            }
        }

        return categoryRepository.filterCategories(kw, cleanKw, st, pageable);
    }

    public Page<Category> getCategoriesForAdmin(int page, int size) {
        return getCategoriesForAdmin(page, size, null, null);
    }

    /**
     * Lấy chi tiết danh mục theo ID
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục với ID: " + id));
    }

    /**
     * Thêm mới danh mục
     * Chỉ lưu các trường name, description, status và MySQL tự sinh ID số tăng dần.
     * Không insert vào cột code nào cả.
     */
    @Transactional
    public Category createCategory(CategoryRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống!");
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // Lưu vào database với ID tự sinh
        return categoryRepository.save(category);
    }

    /**
     * Cập nhật danh mục
     * LƯU Ý: Khi chuyển trạng thái sang Tạm ẩn (status = 0), hàm CHỈ CẬP NHẬT cột status trong DB,
     * tuyệt đối KHÔNG xóa bản ghi khỏi database.
     */
    @Transactional
    public Category updateCategory(CategoryRequest request) {
        if (request.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID danh mục không được để trống!");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống!");
        }

        Category category = getCategoryById(request.getId());
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        // Cập nhật trạng thái: 1 = Hiển thị, 0 = Tạm ẩn (chỉ cập nhật status, không xóa dữ liệu)
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        return categoryRepository.save(category);
    }

    /**
     * Xóa danh mục (xử lý an toàn ràng buộc khóa ngoại)
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            // Danh mục đang có sản phẩm liên kết: Chuyển sang tạm ẩn (soft-delete) để không vi phạm ràng buộc FK
            category.setStatus(0);
            categoryRepository.save(category);
        } else {
            categoryRepository.deleteById(id);
        }
    }

    /**
     * Lấy tổng số lượng danh mục
     */
    public long getTotalCategories() {
        return categoryRepository.count();
    }

    /**
     * Lấy toàn bộ danh mục đang hoạt động
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByStatus(1);
    }

    /**
     * Lấy toàn bộ danh mục
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
