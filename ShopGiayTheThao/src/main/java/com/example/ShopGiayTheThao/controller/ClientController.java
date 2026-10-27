package com.example.ShopGiayTheThao.controller;

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

