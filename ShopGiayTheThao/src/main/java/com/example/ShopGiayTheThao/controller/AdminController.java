package com.example.ShopGiayTheThao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({ "", "/", "/dashboard", "/dashboard.html" })
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping({ "/products", "/product.html" })
    public String products() {
        return "admin/product";
    }

    @GetMapping({ "/categories", "/cate.html" })
    public String categories() {
        return "admin/cate";
    }

    @GetMapping({ "/sizes", "/size.html" })
    public String sizes() {
        return "admin/size";
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
