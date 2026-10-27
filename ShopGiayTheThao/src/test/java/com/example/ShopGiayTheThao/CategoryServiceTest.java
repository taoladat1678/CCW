package com.example.ShopGiayTheThao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.CategoryRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.service.CategoryService;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testCategoryCRUDAndFilter() {
        // 1. Tạo mới danh mục
        CategoryRequest addReq = new CategoryRequest();
        addReq.setCode("CAT-TEST-RUN");
        addReq.setName("Danh Mục Chạy Thử Nghiệm");
        addReq.setDescription("Mô tả test");
        addReq.setStatus(1);

        Category created = categoryService.createCategory(addReq);
        assertNotNull(created.getId());
        assertEquals(String.format("CAT-%03d", created.getId()), created.getCode());
        assertEquals("Danh Mục Chạy Thử Nghiệm", created.getName());

        // 2. Lấy chi tiết danh mục
        Category found = categoryService.getCategoryById(created.getId());
        assertEquals(created.getName(), found.getName());

        // 3. Cập nhật danh mục
        CategoryRequest editReq = new CategoryRequest();
        editReq.setId(created.getId());
        editReq.setName("Danh Mục Đã Cập Nhật");
        editReq.setDescription("Mô tả mới");
        editReq.setStatus(0);

        Category updated = categoryService.updateCategory(editReq);
        assertEquals(String.format("CAT-%03d", updated.getId()), updated.getCode());
        assertEquals("Danh Mục Đã Cập Nhật", updated.getName());
        assertEquals(0, updated.getStatus());

        // 4. Lọc và tìm kiếm danh mục
        Page<Category> searchPage = categoryService.getCategoriesForAdmin(0, 5, "Cập Nhật", "hidden");
        assertFalse(searchPage.isEmpty());

        // 5. Xóa danh mục
        categoryService.deleteCategory(created.getId());

        // 6. Test tự sinh mã CAT-XXX tránh trùng lặp khi để trống mã
        CategoryRequest autoReq = new CategoryRequest();
        autoReq.setName("Danh Mục Tự Sinh Mã");
        Category autoCreated = categoryService.createCategory(autoReq);
        assertNotNull(autoCreated.getCode());
        org.junit.jupiter.api.Assertions.assertTrue(autoCreated.getCode().startsWith("CAT-"));
        assertEquals(String.format("CAT-%03d", autoCreated.getId()), autoCreated.getCode());
    }

    @Test
    void testDeleteCategoryWithProductsReassignsRandomly() {
        // Tạo 2 danh mục
        Category catToDelete = categoryRepository.save(new Category("Cat To Delete", "Desc", 1));
        Category catRemaining = categoryRepository.save(new Category("Cat Remaining", "Desc", 1));

        // Tạo 1 thương hiệu để liên kết sản phẩm
        Brand brand = brandRepository.save(new Brand("Brand For Cat Test", null, "Desc"));

        // Tạo sản phẩm thuộc catToDelete
        Product product = new Product("Giày Test Cat Delete", "Desc", BigDecimal.valueOf(1200000), null, null, catToDelete, brand, 1);
        product = productRepository.save(product);

        // Xóa catToDelete: xóa khỏi DB hoàn toàn, chuyển sản phẩm sang danh mục còn lại
        categoryService.deleteCategory(catToDelete.getId());

        // Kiểm tra catToDelete đã bị xóa khỏi DB
        assertTrue(categoryRepository.findById(catToDelete.getId()).isEmpty());

        // Kiểm tra sản phẩm đã được chuyển sang một danh mục hợp lệ khác (không còn là catToDelete)
        Product updatedProduct = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertNotEquals(catToDelete.getId(), updatedProduct.getCategory().getId());
        assertTrue(categoryRepository.existsById(updatedProduct.getCategory().getId()));
    }
}
