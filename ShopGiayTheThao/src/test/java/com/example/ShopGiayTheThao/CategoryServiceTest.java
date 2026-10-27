package com.example.ShopGiayTheThao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.service.CategoryService;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

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
}
