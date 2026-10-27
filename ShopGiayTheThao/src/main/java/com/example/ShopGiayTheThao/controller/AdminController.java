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

import com.example.ShopGiayTheThao.dto.CategoryRequest;
import com.example.ShopGiayTheThao.dto.CustomerStatsResponse;
import com.example.ShopGiayTheThao.dto.ProductRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.SizeRequest;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.service.BlogService;
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

    public AdminController(UserService userService, ProductService productService, BlogService blogService,
            CategoryService categoryService, SizeService sizeService) {
        this.userService = userService;
        this.productService = productService;
        this.blogService = blogService;
        this.categoryService = categoryService;
        this.sizeService = sizeService;
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
        Page<ProductResponse> productPage = productService.getProductsForAdmin(page, size, keyword, categoryId, brandId, status);
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

    private String buildRedirectUrl(int page, int size, String success, String keyword, Long categoryId, Long brandId, String status) {
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
        if (request.getId() != null) {
            productService.updateProduct(request.getId(), request);
        }
        return buildRedirectUrl(page, size, "edit", keyword, categoryIdFilter, brandIdFilter, statusFilter);
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
        model.addAttribute("status", (effectiveStatus != null && !effectiveStatus.isBlank()) ? effectiveStatus.trim() : "");
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
            categoryService.updateCategory(request);
            return buildCategoryRedirectUrl(page, size, "edit", keyword, filter);
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
            sizeService.updateSize(request);
            return buildSizeRedirectUrl(page, size, "edit", keyword, null);
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
    public String users() {
        return "admin/user";
    }

    @GetMapping({ "/brands", "/brand.html" })
    public String brands() {
        return "admin/brand";
    }

    @GetMapping({ "/blogs", "/blog.html" })
    public String blogs() {
        return "admin/blog";
    }
}
