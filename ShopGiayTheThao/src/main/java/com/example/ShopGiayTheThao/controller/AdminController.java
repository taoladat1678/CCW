package com.example.ShopGiayTheThao.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ShopGiayTheThao.dto.BlogDTO;
import com.example.ShopGiayTheThao.dto.BlogRequest;
import com.example.ShopGiayTheThao.dto.BrandDTO;
import com.example.ShopGiayTheThao.dto.BrandRequest;
import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.dto.CustomerStatsResponse;
import com.example.ShopGiayTheThao.dto.ProductRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SizeService sizeService;
    private final BrandService brandService;

    public AdminController(UserService userService, ProductService productService, BlogService blogService,
            CategoryService categoryService, SizeService sizeService, BrandService brandService) {
        this.userService = userService;
        this.productService = productService;
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.sizeService = sizeService;
        this.brandService = brandService;
    }

    @GetMapping({ "", "/", "/dashboard", "/dashboard.html" })
    public String dashboard(Model model) {
        long totalCustomers = userService.getTotalCustomers();
        long totalProducts = productService.getTotalProducts();
        long totalCategories = productService.getTotalCategories();
        long totalBlogs = blogService.getTotalBlogs();
        long totalBrands = productService.getTotalBrands();

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalBlogs", totalBlogs);
        model.addAttribute("totalBrands", totalBrands);
        model.addAttribute("recentProducts", productService.getRecentProducts());
        model.addAttribute("recentUsers", userService.getRecentUsers());
        return "admin/dashboard";
    }

    @GetMapping({ "/products", "/product.html" })
    public String products(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {
        Page<ProductResponse> productPage = productService.getProductsForAdmin(page, size, keyword, categoryId, brandId,
                status);
        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("pageSize", size);

        // Giữ lại các giá trị tìm kiếm & lọc trên giao diện và phân trang
        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("status", (status != null && !status.isBlank()) ? status.trim() : "");

        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        model.addAttribute("sizes", productService.getAllSizes());
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                ProductResponse p = productService.getProductById(id);
                if ("view".equalsIgnoreCase(action)) {
                    model.addAttribute("viewProduct", p);
                } else if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editProduct", p);
                } else if ("stock".equalsIgnoreCase(action) || "quick_stock".equalsIgnoreCase(action)) {
                    model.addAttribute("quickStockProduct", p);
                }
            } catch (Exception ignored) {
            }
        }
        return "admin/product";
    }

    private String buildRedirectUrl(int page, int size, String success, String keyword, Long categoryId, Long brandId,
            String status) {
        StringBuilder sb = new StringBuilder("/admin/products?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (categoryId != null) {
            sb.append("&categoryId=").append(categoryId);
        }
        if (brandId != null) {
            sb.append("&brandId=").append(brandId);
        }
        if (status != null && !status.isBlank()) {
            sb.append("&status=").append(java.net.URLEncoder.encode(status, java.nio.charset.StandardCharsets.UTF_8));
        }
        return "redirect:" + sb.toString();
    }

    @PostMapping("/products/add")
    public String addProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryIdFilter", required = false) Long categoryIdFilter,
            @RequestParam(name = "brandIdFilter", required = false) Long brandIdFilter,
            @RequestParam(name = "statusFilter", required = false) String statusFilter) {
        productService.createProduct(request);
        return buildRedirectUrl(page, size, "add", keyword, categoryIdFilter, brandIdFilter, statusFilter);
    }

    @PostMapping("/products/edit")
    public String editProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryIdFilter", required = false) Long categoryIdFilter,
            @RequestParam(name = "brandIdFilter", required = false) Long brandIdFilter,
            @RequestParam(name = "statusFilter", required = false) String statusFilter) {
        String success = null;
        if (request.getId() != null) {
            try {
                com.example.ShopGiayTheThao.dto.ProductResponse existing = productService
                        .getProductById(request.getId());
                boolean hasChanged = false;
                if (existing != null) {
                    String newName = request.getName() != null ? request.getName().trim() : "";
                    String oldName = existing.getName() != null ? existing.getName().trim() : "";
                    if (!newName.equals(oldName))
                        hasChanged = true;

                    String newThumb = request.getThumbnail() != null ? request.getThumbnail().trim() : "";
                    String oldThumb = existing.getThumbnail() != null ? existing.getThumbnail().trim() : "";
                    if (!newThumb.isEmpty() && !newThumb.equals(oldThumb))
                        hasChanged = true;

                    if (request.getPrice() != null && !request.getPrice().equals(existing.getPrice()))
                        hasChanged = true;
                    if (request.getSalePrice() != null && !request.getSalePrice().equals(existing.getSalePrice()))
                        hasChanged = true;
                    if (request.getStatus() != null && !request.getStatus().equals(existing.getStatus()))
                        hasChanged = true;
                }
                productService.updateProduct(request.getId(), request);
                success = hasChanged ? "edit" : null;
            } catch (Exception ignored) {
            }
        }
        return buildRedirectUrl(page, size, success, keyword, categoryIdFilter, brandIdFilter, statusFilter);
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProductPost(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "status", required = false) String status) {
        productService.deleteProduct(id);
        return buildRedirectUrl(page, size, "delete", keyword, categoryId, brandId, status);
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProductGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "status", required = false) String status) {
        productService.deleteProduct(id);
        return buildRedirectUrl(page, size, "delete", keyword, categoryId, brandId, status);
    }

    @GetMapping({ "/categories", "/cate.html" })
    public String categories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {

        String effectiveStatus = (statusFilter != null && !statusFilter.isBlank()) ? statusFilter : status;
        Page<Category> categoryPage = categoryService.getCategoriesForAdmin(page, size, keyword, effectiveStatus);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("status",
                (effectiveStatus != null && !effectiveStatus.isBlank()) ? effectiveStatus.trim() : "");
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                Category c = categoryService.getCategoryById(id);
                if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editCategory", c);
                }
            } catch (Exception ignored) {
            }
        }

        return "admin/cate";
    }

    @GetMapping("/categories/add")
    public String addCategoryGet() {
        return "redirect:/admin/categories?action=add";
    }

    @GetMapping("/categories/edit")
    public String editCategoryGet() {
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(
            @ModelAttribute CategoryRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "status", required = false) String statusFallback) {
        String filter = (statusFilter != null && !statusFilter.isBlank()) ? statusFilter : statusFallback;
        try {
            categoryService.createCategory(request);
            return buildCategoryRedirectUrl(page, size, "add", keyword, filter);
        } catch (Exception e) {
            return buildCategoryRedirectUrl(page, size, "error", keyword, filter);
        }
    }

    @PostMapping("/categories/edit")
    public String editCategory(
            @ModelAttribute CategoryRequest request,
            @RequestParam(name = "id", required = false) Long idParam,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "status", required = false) String statusFallback) {
        if (request.getId() == null && idParam != null) {
            request.setId(idParam);
        }
        String filter = (statusFilter != null && !statusFilter.isBlank()) ? statusFilter : statusFallback;
        try {
            Category existing = categoryService.getCategoryById(request.getId());
            boolean hasChanged = false;
            if (existing != null) {
                String newName = request.getName() != null ? request.getName().trim() : "";
                String oldName = existing.getName() != null ? existing.getName().trim() : "";
                if (!newName.equals(oldName))
                    hasChanged = true;

                String newDesc = request.getDescription() != null ? request.getDescription().trim() : "";
                String oldDesc = existing.getDescription() != null ? existing.getDescription().trim() : "";
                if (!newDesc.equals(oldDesc))
                    hasChanged = true;

                if (request.getStatus() != null && !request.getStatus().equals(existing.getStatus()))
                    hasChanged = true;
            }
            categoryService.updateCategory(request);
            String success = hasChanged ? "edit" : null;
            return buildCategoryRedirectUrl(page, size, success, keyword, filter);
        } catch (Exception e) {
            return buildCategoryRedirectUrl(page, size, "error", keyword, filter);
        }
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "status", required = false) String statusFallback) {
        String filter = (statusFilter != null && !statusFilter.isBlank()) ? statusFilter : statusFallback;
        try {
            categoryService.deleteCategory(id);
            return buildCategoryRedirectUrl(page, size, "delete", keyword, filter);
        } catch (Exception e) {
            return buildCategoryRedirectUrl(page, size, "error", keyword, filter);
        }
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategoryGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "status", required = false) String statusFallback) {
        String filter = (statusFilter != null && !statusFilter.isBlank()) ? statusFilter : statusFallback;
        try {
            categoryService.deleteCategory(id);
            return buildCategoryRedirectUrl(page, size, "delete", keyword, filter);
        } catch (Exception e) {
            return buildCategoryRedirectUrl(page, size, "error", keyword, filter);
        }
    }

    private String buildCategoryRedirectUrl(int page, int size, String success, String keyword, String status) {
        StringBuilder sb = new StringBuilder("/admin/categories?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (status != null && !status.isBlank()) {
            sb.append("&status=").append(java.net.URLEncoder.encode(status, java.nio.charset.StandardCharsets.UTF_8));
        }
        return "redirect:" + sb.toString();
    }

    @GetMapping({ "/sizes", "/size.html" })
    public String sizes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {
        Page<Size> sizePage = sizeService.getSizesForAdmin(page, size, keyword);
        model.addAttribute("sizePage", sizePage);
        model.addAttribute("sizes", sizePage.getContent());
        model.addAttribute("currentPage", sizePage.getNumber());
        model.addAttribute("totalPages", sizePage.getTotalPages());
        model.addAttribute("totalElements", sizePage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                Size s = sizeService.getSizeById(id);
                if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editSize", s);
                }
            } catch (Exception ignored) {
            }
        }

        return "admin/size";
    }

    @GetMapping("/sizes/add")
    public String addSizeGet() {
        return "redirect:/admin/sizes?action=add";
    }

    @GetMapping("/sizes/edit")
    public String editSizeGet() {
        return "redirect:/admin/sizes";
    }

    @PostMapping("/sizes/add")
    public String addSize(
            @ModelAttribute SizeRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            sizeService.createSize(request);
            return buildSizeRedirectUrl(page, size, "add", keyword, null);
        } catch (Exception e) {
            return buildSizeRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @PostMapping("/sizes/edit")
    public String editSize(
            @ModelAttribute SizeRequest request,
            @RequestParam(name = "id", required = false) Long idParam,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        if (request.getId() == null && idParam != null) {
            request.setId(idParam);
        }
        try {
            Size existing = sizeService.getSizeById(request.getId());
            boolean hasChanged = false;
            if (existing != null) {
                String newName = request.getName() != null ? request.getName().trim() : "";
                String oldName = existing.getName() != null ? existing.getName().trim() : "";
                if (!newName.equals(oldName))
                    hasChanged = true;
            }
            sizeService.updateSize(request);
            String success = hasChanged ? "edit" : null;
            return buildSizeRedirectUrl(page, size, success, keyword, null);
        } catch (Exception e) {
            return buildSizeRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @PostMapping("/sizes/delete/{id}")
    public String deleteSize(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            sizeService.deleteSize(id);
            return buildSizeRedirectUrl(page, size, "delete", keyword, null);
        } catch (Exception e) {
            return buildSizeRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @GetMapping("/sizes/delete/{id}")
    public String deleteSizeGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            sizeService.deleteSize(id);
            return buildSizeRedirectUrl(page, size, "delete", keyword, null);
        } catch (Exception e) {
            return buildSizeRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    private String buildSizeRedirectUrl(int page, int size, String success, String keyword, String error) {
        StringBuilder sb = new StringBuilder("/admin/sizes?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (error != null && !error.isBlank()) {
            sb.append("&error=").append(java.net.URLEncoder.encode(error, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        return "redirect:" + sb.toString();
    }

    @GetMapping({ "/users", "/user.html" })
    public String users(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {
        Page<User> userPage = userService.getUsersForAdmin(page, size, keyword, role, status);
        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("role", (role != null && !role.isBlank()) ? role.trim() : "");
        model.addAttribute("status", status);
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                User u = userService.getUserById(id);
                if ("view".equalsIgnoreCase(action)) {
                    model.addAttribute("viewUser", u);
                } else if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editUser", u);
                }
            } catch (Exception ignored) {
            }
        }
        return "admin/user";
    }

    @PostMapping("/users/add")
    public String addUser(
            @ModelAttribute UserRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        try {
            userService.createUser(request);
            return buildUserRedirectUrl(page, size, "add", null, keyword, roleFilter, statusFilter);
        } catch (Exception e) {
            return buildUserRedirectUrl(page, size, null, e.getMessage(), keyword, roleFilter, statusFilter);
        }
    }

    @PostMapping("/users/edit-status")
    public String editUserStatus(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "status") Integer status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        try {
            userService.updateUserStatus(id, status);
            return buildUserRedirectUrl(page, size, "edit", null, keyword, roleFilter, statusFilter);
        } catch (Exception e) {
            return buildUserRedirectUrl(page, size, null, e.getMessage(), keyword, roleFilter, statusFilter);
        }
    }

    @PostMapping("/users/toggle-lock/{id}")
    public String toggleLockUser(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        try {
            userService.toggleLockUser(id);
            return buildUserRedirectUrl(page, size, "lock", null, keyword, roleFilter, statusFilter);
        } catch (Exception e) {
            return buildUserRedirectUrl(page, size, null, e.getMessage(), keyword, roleFilter, statusFilter);
        }
    }

    @GetMapping("/users/toggle-lock/{id}")
    public String toggleLockUserGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        return toggleLockUser(id, page, size, keyword, roleFilter, statusFilter);
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        try {
            userService.deleteUser(id);
            return buildUserRedirectUrl(page, size, "delete", null, keyword, roleFilter, statusFilter);
        } catch (Exception e) {
            return buildUserRedirectUrl(page, size, null,
                    "Không thể xóa người dùng do có dữ liệu liên kết. Bạn có thể sử dụng tính năng Khóa tài khoản!",
                    keyword, roleFilter, statusFilter);
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUserGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "roleFilter", required = false) String roleFilter,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter) {
        return deleteUser(id, page, size, keyword, roleFilter, statusFilter);
    }

    private String buildUserRedirectUrl(int page, int size, String success, String error, String keyword, String role,
            Integer status) {
        StringBuilder sb = new StringBuilder("/admin/users?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (error != null && !error.isBlank()) {
            sb.append("&error=").append(java.net.URLEncoder.encode(error, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (role != null && !role.isBlank()) {
            sb.append("&role=").append(java.net.URLEncoder.encode(role, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (status != null) {
            sb.append("&status=").append(status);
        }
        return "redirect:" + sb.toString();
    }

    @GetMapping({ "/brands", "/brand.html" })
    public String brands(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {
        Page<BrandDTO> brandPage = brandService.getBrandsForAdmin(page, size, keyword);
        model.addAttribute("brandPage", brandPage);
        model.addAttribute("brands", brandPage.getContent());
        model.addAttribute("currentPage", brandPage.getNumber());
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("totalElements", brandPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                BrandDTO b = brandService.getBrandById(id);
                if ("view".equalsIgnoreCase(action)) {
                    model.addAttribute("viewBrand", b);
                } else if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editBrand", b);
                }
            } catch (Exception ignored) {
            }
        }

        return "admin/brand";
    }

    @GetMapping("/brands/add")
    public String addBrandGet() {
        return "redirect:/admin/brands?action=add";
    }

    @GetMapping("/brands/edit")
    public String editBrandGet() {
        return "redirect:/admin/brands";
    }

    @PostMapping("/brands/add")
    public String addBrand(
            @ModelAttribute BrandRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            brandService.createBrand(request);
            return buildBrandRedirectUrl(page, size, "add", keyword, null);
        } catch (Exception e) {
            return buildBrandRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @PostMapping("/brands/edit")
    public String editBrand(
            @ModelAttribute BrandRequest request,
            @RequestParam(name = "id", required = false) Long idParam,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        if (request.getId() == null && idParam != null) {
            request.setId(idParam);
        }
        try {
            Brand existing = brandService.getBrandEntityById(request.getId());
            boolean hasChanged = false;
            if (existing != null) {
                String newName = request.getName() != null ? request.getName().trim() : "";
                String oldName = existing.getName() != null ? existing.getName().trim() : "";
                if (!newName.equals(oldName))
                    hasChanged = true;

                String newLogo = request.getLogo() != null ? request.getLogo().trim() : "";
                String oldLogo = existing.getLogo() != null ? existing.getLogo().trim() : "";
                if (!newLogo.isEmpty() && !newLogo.equals(oldLogo))
                    hasChanged = true;

                String newDesc = request.getDescription() != null ? request.getDescription().trim() : "";
                String oldDesc = existing.getDescription() != null ? existing.getDescription().trim() : "";
                if (!newDesc.equals(oldDesc))
                    hasChanged = true;
            }
            brandService.updateBrand(request);
            String success = hasChanged ? "edit" : null;
            return buildBrandRedirectUrl(page, size, success, keyword, null);
        } catch (Exception e) {
            return buildBrandRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @PostMapping("/brands/delete/{id}")
    public String deleteBrand(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            brandService.deleteBrand(id);
            return buildBrandRedirectUrl(page, size, "delete", keyword, null);
        } catch (Exception e) {
            return buildBrandRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrandGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "keyword", required = false) String keyword) {
        try {
            brandService.deleteBrand(id);
            return buildBrandRedirectUrl(page, size, "delete", keyword, null);
        } catch (Exception e) {
            return buildBrandRedirectUrl(page, size, null, keyword, e.getMessage());
        }
    }

    private String buildBrandRedirectUrl(int page, int size, String success, String keyword, String error) {
        StringBuilder sb = new StringBuilder("/admin/brands?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (error != null && !error.isBlank()) {
            sb.append("&error=").append(java.net.URLEncoder.encode(error, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        return "redirect:" + sb.toString();
    }

    @GetMapping({ "/blogs", "/blog.html" })
    public String blogs(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "id", required = false) Long id,
            Model model) {
        Page<BlogDTO> blogPage = blogService.getBlogsForAdmin(page, size, keyword, status, category);
        model.addAttribute("blogPage", blogPage);
        model.addAttribute("blogs", blogPage.getContent());
        model.addAttribute("currentPage", blogPage.getNumber());
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("totalElements", blogPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("keyword", (keyword != null && !keyword.isBlank()) ? keyword.trim() : "");
        model.addAttribute("status", status);
        model.addAttribute("category", (category != null && !category.isBlank()) ? category.trim() : "");
        model.addAttribute("action", action);

        if (action != null && id != null) {
            try {
                BlogDTO b = blogService.getBlogById(id);
                if ("view".equalsIgnoreCase(action)) {
                    model.addAttribute("viewBlog", b);
                } else if ("edit".equalsIgnoreCase(action)) {
                    model.addAttribute("editBlog", b);
                }
            } catch (Exception ignored) {
            }
        }
        return "admin/blog";
    }

    @PostMapping("/blogs/add")
    public String addBlog(
            @ModelAttribute BlogRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        try {
            blogService.createBlog(request);
            return buildBlogRedirectUrl(page, size, "add", null, keyword, statusFilter, categoryFilter);
        } catch (Exception e) {
            return buildBlogRedirectUrl(page, size, null, e.getMessage(), keyword, statusFilter, categoryFilter);
        }
    }

    @PostMapping("/blogs/edit")
    public String editBlog(
            @ModelAttribute BlogRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        try {
            blogService.updateBlog(request.getId(), request);
            return buildBlogRedirectUrl(page, size, "edit", null, keyword, statusFilter, categoryFilter);
        } catch (Exception e) {
            return buildBlogRedirectUrl(page, size, null, e.getMessage(), keyword, statusFilter, categoryFilter);
        }
    }

    @PostMapping("/blogs/toggle-status/{id}")
    public String toggleBlogStatus(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        try {
            blogService.toggleBlogStatus(id);
            return buildBlogRedirectUrl(page, size, "status", null, keyword, statusFilter, categoryFilter);
        } catch (Exception e) {
            return buildBlogRedirectUrl(page, size, null, e.getMessage(), keyword, statusFilter, categoryFilter);
        }
    }

    @GetMapping("/blogs/toggle-status/{id}")
    public String toggleBlogStatusGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        return toggleBlogStatus(id, page, size, keyword, statusFilter, categoryFilter);
    }

    @PostMapping("/blogs/delete/{id}")
    public String deleteBlog(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        try {
            blogService.deleteBlog(id);
            return buildBlogRedirectUrl(page, size, "delete", null, keyword, statusFilter, categoryFilter);
        } catch (Exception e) {
            return buildBlogRedirectUrl(page, size, null, e.getMessage(), keyword, statusFilter, categoryFilter);
        }
    }

    @GetMapping("/blogs/delete/{id}")
    public String deleteBlogGet(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusFilter", required = false) Integer statusFilter,
            @RequestParam(name = "categoryFilter", required = false) String categoryFilter) {
        return deleteBlog(id, page, size, keyword, statusFilter, categoryFilter);
    }

    private String buildBlogRedirectUrl(int page, int size, String success, String error, String keyword,
            Integer status, String category) {
        StringBuilder sb = new StringBuilder("/admin/blogs?page=").append(page).append("&size=").append(size);
        if (success != null && !success.isBlank()) {
            sb.append("&success=").append(success);
        }
        if (error != null && !error.isBlank()) {
            sb.append("&error=").append(java.net.URLEncoder.encode(error, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (keyword != null && !keyword.isBlank()) {
            sb.append("&keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (status != null) {
            sb.append("&status=").append(status);
        }
        if (category != null && !category.isBlank()) {
            sb.append("&category=")
                    .append(java.net.URLEncoder.encode(category, java.nio.charset.StandardCharsets.UTF_8));
        }
        return "redirect:" + sb.toString();
    }
}
