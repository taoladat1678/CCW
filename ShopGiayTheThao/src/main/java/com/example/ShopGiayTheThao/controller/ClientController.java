package com.example.ShopGiayTheThao.controller;


import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.UserRequest;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.service.CartService;
import com.example.ShopGiayTheThao.service.ProductService;
import com.example.ShopGiayTheThao.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ShopGiayTheThao.dto.BlogDTO;
import com.example.ShopGiayTheThao.dto.CartResponseDTO;
import com.example.ShopGiayTheThao.service.BlogService;
import com.example.ShopGiayTheThao.service.BrandService;
import com.example.ShopGiayTheThao.service.CartService;
import com.example.ShopGiayTheThao.service.ProductService;


@Controller
public class ClientController {

    private final ProductService productService;

    private final CartService cartService;
    private final UserService userService;

    public ClientController(
            ProductService productService,
            CartService cartService,
            UserService userService) {
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;
    }

    /**
     * Tự động truyền currentUser vào TẤT CẢ các template Thymeleaf
     */
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    /**
     * Tự động truyền số lượng giỏ hàng vào TẤT CẢ các template Thymeleaf
     */
    @ModelAttribute("cartCount")
    public long getCartCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return cartService.getCartCount(currentUser.getId());
        }
        return 0;
    }

    @GetMapping({ "/", "/index", "/index.html" })
    public String index() {
        return "client/index";
    }

    /**
     * Danh sách sản phẩm (Render 100% bằng Thymeleaf)
     */
    @GetMapping({ "/products", "/product.html" })
    public String products(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "sizeId", required = false) Long sizeId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "featured") String sort,
            Model model) {

        Page<ProductResponse> productPage = productService.getProducts(
                page, size, categoryId, brandId, sizeId, minPrice, maxPrice, keyword, sort
        );

        model.addAttribute("products", productPage);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        model.addAttribute("sizes", productService.getAllSizes());

        model.addAttribute("currentPage", page);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("selectedSizeId", sizeId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSort", sort);

        return "client/product";
    }

    @GetMapping({ "/product-detail/{id}", "/product-detail", "/product-detail.html" })
    public String productDetail(
            @org.springframework.web.bind.annotation.PathVariable(value = "id", required = false) Long pathId,
            @RequestParam(value = "id", required = false) Long queryId,
            Model model) {
        Long productId = (pathId != null) ? pathId : queryId;
        if (productId == null) {
            Page<ProductResponse> firstPage = productService.getProducts(0, 1, null, null, null, null, null, null, null);
            if (firstPage.hasContent()) {
                productId = firstPage.getContent().get(0).getId();
            } else {
                return "redirect:/products";
            }
        }

        try {
            ProductResponse product = productService.getProductById(productId);
            java.util.List<ProductResponse> relatedProducts = productService.getRelatedProducts(product.getCategoryId(), product.getId());

            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            return "client/product-detail";
        } catch (Exception e) {
            return "redirect:/products";
        }
    }

    @GetMapping({ "/cart", "/cart.html" })
    public String cart(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        return "client/cart";
    }

    @GetMapping({ "/brand", "/brand.html" })
    public String brand() {
        return "client/brand";
    }

    @GetMapping({ "/blog", "/blog.html" })
    public String blog() {
        return "client/blog";
    }

    // ==========================================
    // ĐĂNG NHẬP (LOGIN)
    // ==========================================

    @GetMapping({ "/login", "/login.html" })
    public String loginPage(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "client/login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } catch (Throwable e) {
            model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
            model.addAttribute("email", email);
            return "client/login";
        }
    }

    // ==========================================
    // ĐĂNG KÝ (REGISTER)
    // ==========================================

    @GetMapping({ "/register", "/register.html" })
    public String registerPage(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "client/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            HttpSession session,
            Model model) {
        try {
            if (userService.existsByEmail(email)) {
                model.addAttribute("error", "Email này đã được sử dụng!");
                model.addAttribute("fullName", fullName);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("address", address);
                return "client/register";
            }

            UserRequest req = new UserRequest(fullName, email, password, phone, address, "CUSTOMER", 1);
            User newUser = userService.createUser(req);
            session.setAttribute("currentUser", newUser);
            // Đăng ký xong thì chuyển hướng về trang chủ
            return "redirect:/";
        } catch (Throwable e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            return "client/register";
        }
    }

    // ==========================================
    // ĐĂNG XUẤT (LOGOUT)
    // ==========================================

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.invalidate();
        return "redirect:/";
    }

    // ==========================================
    // THÔNG TIN TÀI KHOẢN (USER PROFILE)
    // ==========================================

    @GetMapping({ "/user", "/user.html" })
    public String userPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy thông tin mới nhất từ database
        User freshUser = userService.getUserById(currentUser.getId());
        model.addAttribute("user", freshUser);
        return "client/user";
    }

    @PostMapping("/user/update")
    public String updateUserProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "password", required = false) String password,
            HttpSession session,
            Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        UserRequest req = new UserRequest();
        req.setFullName(fullName);
        req.setPhone(phone);
        req.setAddress(address);
        if (password != null && !password.isBlank()) {
            req.setPassword(password);
        }

        User updatedUser = userService.updateUser(currentUser.getId(), req);
        session.setAttribute("currentUser", updatedUser);
        model.addAttribute("user", updatedUser);
        model.addAttribute("success", "Cập nhật thông tin thành công!");

    private final BrandService brandService;
    private final BlogService blogService;
    private final CartService cartService;

    public ClientController(ProductService productService,
                            BrandService brandService,
                            BlogService blogService,
                            CartService cartService) {
        this.productService = productService;
        this.brandService = brandService;
        this.blogService = blogService;
        this.cartService = cartService;
    }

    private void addCommonAttributes(Model model) {
        try {
            CartResponseDTO cart = cartService.getCart(null);
            model.addAttribute("cart", cart);
        } catch (Exception e) {
            model.addAttribute("cart", new CartResponseDTO());
        }
    }

    @GetMapping({"/", "/index", "/index.html"})
    public String index(Model model) {
        addCommonAttributes(model);
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("latestBlogs", blogService.getLatestBlogs());
        return "client/index";
    }

    @GetMapping({"/products", "/product.html"})
    public String products(Model model) {
        addCommonAttributes(model);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("brands", brandService.getAllBrands());
        return "client/product";
    }

    @GetMapping({"/product-detail", "/product-detail.html"})
    public String productDetail(@RequestParam(name = "id", required = false) Long id, Model model) {
        addCommonAttributes(model);
        if (id != null) {
            model.addAttribute("product", productService.getProductById(id));
        }
        return "client/product-detail";
    }

    @GetMapping({"/cart", "/cart.html"})
    public String cart(Model model) {
        addCommonAttributes(model);
        return "client/cart";
    }

    @GetMapping({"/brand", "/brand.html"})
    public String brand(Model model) {
        addCommonAttributes(model);
        model.addAttribute("brands", brandService.getAllBrands());
        return "client/brand";
    }

    @GetMapping({"/blog", "/blog.html"})
    public String blog(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "category", required = false) String category,
            Model model) {
        addCommonAttributes(model);
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        String cleanCategory = (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category.trim()))
                ? category.trim()
                : null;
                
        Page<BlogDTO> blogPage = blogService.getBlogsPaginated(pageable, cleanCategory);

        model.addAttribute("blogPage", blogPage);
        model.addAttribute("blogs", blogPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("selectedCategory", (cleanCategory != null) ? cleanCategory : "ALL");
        return "client/blog";
    }

    @GetMapping({"/login", "/login.html"})
    public String login(Model model) {
        addCommonAttributes(model);
        return "client/login";
    }

    @GetMapping({"/register", "/register.html"})
    public String register(Model model) {
        addCommonAttributes(model);
        return "client/register";
    }

    @GetMapping({"/user", "/user.html"})
    public String user(Model model) {
        addCommonAttributes(model);

        return "client/user";
    }
}

