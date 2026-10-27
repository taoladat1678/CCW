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
import com.example.ShopGiayTheThao.dto.BrandStatsResponse;
import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.dto.CategoryStatsResponse;
import com.example.ShopGiayTheThao.dto.CustomerStatsResponse;
import com.example.ShopGiayTheThao.dto.ProductRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.ProductStatsResponse;
import com.example.ShopGiayTheThao.dto.SizeRequest;
import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.BlogService;
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

    public AdminApiController(UserService userService, ProductService productService, BlogService blogService,
            CategoryService categoryService, SizeService sizeService) {
        this.userService = userService;
        this.productService = productService;
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.sizeService = sizeService;
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
     * Lấy danh sách toàn bộ khách hàng / người dùng
     * URL: GET /api/admin/users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
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
     * Cập nhật thông tin khách hàng
     * URL: PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Xóa khách hàng
     * URL: DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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
}
