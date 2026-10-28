package com.example.ShopGiayTheThao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.BlogStatsResponse;
import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.dto.BrandRequest;
import com.example.ShopGiayTheThao.dto.BrandStatsResponse;
import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.dto.CategoryStatsResponse;
import com.example.ShopGiayTheThao.dto.CustomerStatsResponse;
import com.example.ShopGiayTheThao.dto.ProductRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.ProductStatsResponse;
import com.example.ShopGiayTheThao.dto.SizeRequest;
import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.BlogService;
import com.example.ShopGiayTheThao.service.BrandService;
import com.example.ShopGiayTheThao.service.CategoryService;
import com.example.ShopGiayTheThao.service.ProductService;
import com.example.ShopGiayTheThao.service.SizeService;
import com.example.ShopGiayTheThao.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserService userService;
    private final ProductService productService;
    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SizeService sizeService;
    private final BrandService brandService;

    public AdminApiController(UserService userService, ProductService productService, BlogService blogService,
            CategoryService categoryService, SizeService sizeService, BrandService brandService) {
        this.userService = userService;
        this.productService = productService;
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.sizeService = sizeService;
        this.brandService = brandService;
    }

    @GetMapping({ "/stats/customers/count", "/customers/count" })
    public ResponseEntity<CustomerStatsResponse> getCustomerCount() {
        CustomerStatsResponse response = userService.getCustomerStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping({ "/stats/products/count", "/products/count" })
    public ResponseEntity<ProductStatsResponse> getProductCount() {
        ProductStatsResponse response = productService.getProductStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping({ "/stats/categories/count", "/categories/count" })
    public ResponseEntity<CategoryStatsResponse> getCategoryCount() {
        CategoryStatsResponse response = productService.getCategoryStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping({ "/stats/blogs/count", "/blogs/count" })
    public ResponseEntity<BlogStatsResponse> getBlogCount() {
        BlogStatsResponse response = blogService.getBlogStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping({ "/stats/brands/count", "/brands/count" })
    public ResponseEntity<BrandStatsResponse> getBrandCount() {
        BrandStatsResponse response = productService.getBrandStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getAdminProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(productService.getProductsForAdmin(page, size, keyword, categoryId, brandId, status));
    }

    /**
     * Lấy chi tiết 1 sản phẩm theo ID
     * URL: GET /api/admin/products/{id}
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Thêm mới sản phẩm
     * URL: POST /api/admin/products
     */
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin sản phẩm
     * URL: PUT /api/admin/products/{id}
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * Xóa sản phẩm theo ID
     * URL: DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping({ "/products/recent", "/stats/recent-products" })
    public ResponseEntity<List<ProductResponse>> getRecentProducts() {
        return ResponseEntity.ok(productService.getRecentProducts());
    }

    @GetMapping({ "/users/recent", "/stats/recent-users" })
    public ResponseEntity<List<User>> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUsers());
    }


    /**
     * Lấy danh sách người dùng có phân trang, tìm kiếm, lọc theo vai trò và trạng thái
     * URL: GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAdminUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) Integer status) {
        return ResponseEntity.ok(userService.getUsersForAdmin(page, size, keyword, role, status));
    }

    /**
     * Lấy chi tiết 1 khách hàng theo ID
     * URL: GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Thêm mới khách hàng / người dùng
     * URL: POST /api/admin/users
     */
    @PostMapping("/users")
    public ResponseEntity<User> create(@RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin khách hàng ("sửa thì chỉ sửa ở trạng thái" - hỗ trợ cả cập nhật trạng thái qua body)
     * URL: PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequest request) {
        if (request.getStatus() != null) {
            return ResponseEntity.ok(userService.updateUserStatus(id, request.getStatus()));
        }
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Cập nhật riêng trạng thái người dùng (Chỉ sửa trạng thái: Hoạt động / Khóa)
     * URL: PUT /api/admin/users/{id}/status
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam(name = "status", required = false) Integer statusParam,
            @RequestBody(required = false) Map<String, Object> body) {
        Integer newStatus = statusParam;
        if (newStatus == null && body != null && body.containsKey("status")) {
            Object s = body.get("status");
            if (s instanceof Number) {
                newStatus = ((Number) s).intValue();
            } else if (s != null) {
                try {
                    newStatus = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (newStatus == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Vui lòng cung cấp trạng thái hợp lệ (1: Hoạt động, 0: Tạm khóa)!");
        }
        User updated = userService.updateUserStatus(id, newStatus);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", updated.getStatus());
        res.put("message", updated.getStatus() == 1 ? "Đã kích hoạt tài khoản thành công!" : "Đã khóa tài khoản thành công!");
        res.put("user", updated);
        return ResponseEntity.ok(res);
    }

    /**
     * Khóa / Mở khóa người dùng nhanh (1-click toggle lock)
     * URL: PUT /api/admin/users/{id}/toggle-lock
     */
    @PutMapping("/users/{id}/toggle-lock")
    public ResponseEntity<Map<String, Object>> toggleLockUser(@PathVariable Long id) {
        User updated = userService.toggleLockUser(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", updated.getStatus());
        res.put("message", updated.getStatus() == 1 ? "Đã mở khóa tài khoản thành công!" : "Đã khóa tài khoản thành công!");
        res.put("user", updated);
        return ResponseEntity.ok(res);
    }

    /**
     * Xóa khách hàng
     * URL: DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đã xóa người dùng thành công!");
        return ResponseEntity.ok(res);
    }

    /**
     * Lấy danh sách danh mục có phân trang, tìm kiếm và lọc trạng thái
     * URL: GET /api/admin/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Page<Category>> getAdminCategories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(categoryService.getCategoriesForAdmin(page, size, keyword, status));
    }

    /**
     * Lấy chi tiết danh mục theo ID
     * URL: GET /api/admin/categories/{id}
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Thêm mới danh mục
     * URL: POST /api/admin/categories
     */
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest request) {
        Category created = categoryService.createCategory(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin danh mục
     * URL: PUT /api/admin/categories/{id}
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        request.setId(id);
        Category updated = categoryService.updateCategory(request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa danh mục
     * URL: DELETE /api/admin/categories/{id}
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategoryApi(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đã xóa danh mục thành công với ID: " + id);
        return ResponseEntity.ok(res);
    }

    /**
     * Lấy danh sách kích thước (phân trang 5/trang, tìm kiếm)
     * URL: GET /api/admin/sizes
     */
    @GetMapping("/sizes")
    public ResponseEntity<Page<Size>> getAdminSizes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(sizeService.getSizesForAdmin(page, size, keyword));
    }

    /**
     * Lấy chi tiết 1 kích thước theo ID
     * URL: GET /api/admin/sizes/{id}
     */
    @GetMapping("/sizes/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Long id) {
        return ResponseEntity.ok(sizeService.getSizeById(id));
    }

    /**
     * Thêm mới kích thước
     * URL: POST /api/admin/sizes
     */
    @PostMapping("/sizes")
    public ResponseEntity<Size> createSize(@RequestBody SizeRequest request) {
        Size created = sizeService.createSize(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin kích thước
     * URL: PUT /api/admin/sizes/{id}
     */
    @PutMapping("/sizes/{id}")
    public ResponseEntity<Size> updateSize(@PathVariable Long id, @RequestBody SizeRequest request) {
        request.setId(id);
        Size updated = sizeService.updateSize(request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa kích thước
     * URL: DELETE /api/admin/sizes/{id}
     */
    @DeleteMapping("/sizes/{id}")
    public ResponseEntity<Map<String, Object>> deleteSizeApi(@PathVariable Long id) {
        sizeService.deleteSize(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đã xóa kích thước thành công với ID: " + id);
        return ResponseEntity.ok(res);
    }

    /**
     * Lấy danh sách thương hiệu (phân trang 4/trang, tìm kiếm)
     * URL: GET /api/admin/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<Page<BrandDTO>> getAdminBrands(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(brandService.getBrandsForAdmin(page, size, keyword));
    }

    /**
     * Lấy chi tiết 1 thương hiệu theo ID
     * URL: GET /api/admin/brands/{id}
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    /**
     * Thêm mới thương hiệu
     * URL: POST /api/admin/brands
     */
    @PostMapping("/brands")
    public ResponseEntity<Brand> createBrand(@RequestBody BrandRequest request) {
        Brand created = brandService.createBrand(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin thương hiệu
     * URL: PUT /api/admin/brands/{id}
     */
    @PutMapping("/brands/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        request.setId(id);
        Brand updated = brandService.updateBrand(request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa thương hiệu
     * URL: DELETE /api/admin/brands/{id}
     */
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrandApi(@PathVariable Long id) {
        brandService.deleteBrand(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Đã xóa thương hiệu thành công với ID: " + id);
        return ResponseEntity.ok(res);
    }
}
