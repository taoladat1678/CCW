package com.example.ShopGiayTheThao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.dto.BrandRequest;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.CategoryRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.service.BrandService;

@SpringBootTest
@Transactional
class BrandServiceTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testBrandCRUDPaginationAndSearch() {
        // 1. Thêm mới thương hiệu với ID tự sinh
        BrandRequest addReq = new BrandRequest("BRAND-TEST-XYZ", "https://example.com/logo.png", "Mô tả thương hiệu test");
        Brand created = brandService.createBrand(addReq);

        assertNotNull(created.getId());
        assertEquals("BRAND-TEST-XYZ", created.getName());
        assertEquals("https://example.com/logo.png", created.getLogo());
        assertEquals("Mô tả thương hiệu test", created.getDescription());
        assertEquals("BRD-" + created.getId(), created.getCode());

        // 2. Lấy chi tiết thương hiệu theo ID
        BrandDTO found = brandService.getBrandById(created.getId());
        assertNotNull(found);
        assertEquals("BRAND-TEST-XYZ", found.getName());
        assertEquals("BRD-" + created.getId(), found.getCode());
        assertEquals("Mô tả thương hiệu test", found.getDescription());

        // 3. Cập nhật thương hiệu (chỉ cập nhật name, logo, description)
        BrandRequest editReq = new BrandRequest(created.getId(), "BRAND-TEST-XYZ-EDITED", "https://example.com/newlogo.png", "Mô tả đã sửa");
        Brand updated = brandService.updateBrand(editReq);
        assertEquals("BRAND-TEST-XYZ-EDITED", updated.getName());
        assertEquals("https://example.com/newlogo.png", updated.getLogo());
        assertEquals("Mô tả đã sửa", updated.getDescription());
        assertEquals("BRD-" + updated.getId(), updated.getCode());

        // Kiểm tra: nếu ô nhập ảnh bị trống khi cập nhật, tự động lấy lại link ảnh cũ
        BrandRequest editKeepLogoReq = new BrandRequest(created.getId(), "BRAND-TEST-XYZ-EDITED", "   ", "Mô tả giữ ảnh");
        Brand updatedKeepLogo = brandService.updateBrand(editKeepLogoReq);
        assertEquals("https://example.com/newlogo.png", updatedKeepLogo.getLogo());

        // 4. Tìm kiếm theo tên thương hiệu và phân trang 4 phần tử / trang
        Page<BrandDTO> pageByName = brandService.getBrandsForAdmin(0, 4, "XYZ-EDITED");
        assertFalse(pageByName.isEmpty());
        assertEquals(4, pageByName.getSize());
        assertTrue(pageByName.getContent().stream().anyMatch(b -> b.getId().equals(created.getId())));

        // 5. Tìm kiếm theo định dạng mã "BRD-ID"
        Page<BrandDTO> pageByCode = brandService.getBrandsForAdmin(0, 4, "BRD-" + created.getId());
        assertFalse(pageByCode.isEmpty());
        assertTrue(pageByCode.getContent().stream().anyMatch(b -> b.getId().equals(created.getId())));

        // 6. Xóa thương hiệu
        brandService.deleteBrand(created.getId());

        // 7. Kiểm tra sắp xếp: Mới nhất xếp trước, cũ nhất xếp sau (ID giảm dần)
        Brand bA = brandService.createBrand(new BrandRequest("BRAND-ORD-A", null, "Desc A"));
        Brand bB = brandService.createBrand(new BrandRequest("BRAND-ORD-B", null, "Desc B"));
        Page<BrandDTO> pageRecent = brandService.getBrandsForAdmin(0, 4, "BRAND-ORD-");
        assertEquals(2, pageRecent.getContent().size());
        assertEquals(bB.getId(), pageRecent.getContent().get(0).getId());
        assertEquals(bA.getId(), pageRecent.getContent().get(1).getId());

        brandService.deleteBrand(bA.getId());
        brandService.deleteBrand(bB.getId());

        // 8. Kiểm tra validation tên trùng và tên rỗng
        assertThrows(ResponseStatusException.class, () -> {
            brandService.createBrand(new BrandRequest("", null, null));
        });
    }

    @Test
    void testDeleteBrandWithProductsReassignsRandomly() {
        // Tạo 2 thương hiệu
        Brand brandToDelete = brandService.createBrand(new BrandRequest("BRAND-DELETE-TARGET", null, "Desc"));
        Brand brandRemaining = brandService.createBrand(new BrandRequest("BRAND-REMAINING", null, "Desc"));

        // Tạo 1 danh mục để liên kết sản phẩm
        Category cat = categoryRepository.save(new Category("Cat For Brand Test", "Desc", 1));

        // Tạo sản phẩm thuộc brandToDelete
        Product product = new Product("Giày Test Brand Delete", "Desc", BigDecimal.valueOf(1000000), null, null, cat, brandToDelete, 1);
        product = productRepository.save(product);

        // Xóa brandToDelete: không được quăng lỗi BAD_REQUEST, mà phải chuyển sản phẩm sang thương hiệu khác ngẫu nhiên
        brandService.deleteBrand(brandToDelete.getId());

        // Kiểm tra brandToDelete đã bị xóa hoàn toàn khỏi DB
        assertTrue(brandRepository.findById(brandToDelete.getId()).isEmpty());

        // Kiểm tra sản phẩm đã được chuyển sang một thương hiệu hợp lệ khác (không còn là brandToDelete)
        Product updatedProduct = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertNotEquals(brandToDelete.getId(), updatedProduct.getBrand().getId());
        assertTrue(brandRepository.existsById(updatedProduct.getBrand().getId()));
    }
}
