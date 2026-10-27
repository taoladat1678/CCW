package com.example.ShopGiayTheThao.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.AddToCartRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.CartItem;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.CartService;
import com.example.ShopGiayTheThao.service.ProductService;
import com.example.ShopGiayTheThao.service.UserService;

@RestController
@RequestMapping("/api")
public class ClientApiController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public ClientApiController(
            UserService userService,
            ProductService productService,
            CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    // ==========================================
    // API SẢN PHẨM, DANH MỤC, THƯƠNG HIỆU, SIZE
    // ==========================================

    /**
     * 1. Lấy danh sách sản phẩm có phân trang, lọc và sắp xếp
     * URL: GET /api/products?page=0&size=9&categoryId=1&brandId=2&sizeId=3&minPrice=1000000&maxPrice=5000000&keyword=nike&sort=price-low
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "sizeId", required = false) Long sizeId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "featured") String sort) {

        Page<ProductResponse> products = productService.getProducts(
                page, size, categoryId, brandId, sizeId, minPrice, maxPrice, keyword, sort
        );
        return ResponseEntity.ok(products);
    }

    /**
     * 2. Lấy danh sách danh mục (Categories)
     * URL: GET /api/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    /**
     * 3. Lấy danh sách thương hiệu (Brands)
     * URL: GET /api/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getBrands() {
        return ResponseEntity.ok(productService.getAllBrands());
    }

    /**
     * 4. Lấy danh sách kích thước (Sizes)
     * URL: GET /api/sizes
     */
    @GetMapping("/sizes")
    public ResponseEntity<List<Size>> getSizes() {
        return ResponseEntity.ok(productService.getAllSizes());
    }

    // ==========================================
    // API GIỎ HÀNG (CART)
    // ==========================================

    /**
     * 5. Thêm sản phẩm vào giỏ hàng (Bắt buộc người dùng phải đăng nhập)
     * URL: POST /api/cart/add
     */
    @PostMapping("/cart/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody AddToCartRequest request,
            jakarta.servlet.http.HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        Long userId = (currentUser != null) ? currentUser.getId() : request.getUserId();

        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("requireLogin", true);
            error.put("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        request.setUserId(userId);
        CartItem item = cartService.addToCart(request);
        long totalCount = cartService.getCartCount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        response.put("cartItemId", item.getId());
        response.put("totalCartCount", totalCount);

        return ResponseEntity.ok(response);
    }

    /**
     * 6. Lấy số lượng món hàng trong giỏ
     * URL: GET /api/cart/count
     */
    @GetMapping("/cart/count")
    public ResponseEntity<Map<String, Object>> getCartCount(
            @RequestParam(value = "userId", required = false) Long userId,
            jakarta.servlet.http.HttpSession session) {
        if (userId == null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                userId = currentUser.getId();
            }
        }
        long count = cartService.getCartCount(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // API NGƯỜI DÙNG (USER / AUTH)
    // ==========================================

    /**
     * Khách hàng đăng ký tài khoản mới
     * URL: POST /api/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequest request) {
        request.setRole("CUSTOMER");
        request.setStatus(1);
        User newUser = userService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Khách hàng xem thông tin tài khoản cá nhân
     * URL: GET /api/customers/{id}
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Khách hàng cập nhật thông tin tài khoản cá nhân
     * URL: PUT /api/customers/{id}
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody UserRequest request) {
        request.setRole(null);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Kiểm tra nhanh email đã được sử dụng hay chưa
     * URL: GET /api/check-email?email=abc@gmail.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam("email") String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
