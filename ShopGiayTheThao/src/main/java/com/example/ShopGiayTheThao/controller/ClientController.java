package com.example.ShopGiayTheThao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

    @GetMapping({ "/", "/index", "/index.html" })
    public String index() {
        return "client/index";
    }

    @GetMapping({ "/products", "/product.html" })
    public String products() {
        return "client/product";
    }

    @GetMapping({ "/product-detail", "/product-detail.html" })
    public String productDetail() {
        return "client/product-detail";
    }

    @GetMapping({ "/cart", "/cart.html" })
    public String cart() {
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

    @GetMapping({ "/login", "/login.html" })
    public String login() {
        return "client/login";
    }

    @GetMapping({ "/register", "/register.html" })
    public String register() {
        return "client/register";
    }

    @GetMapping({ "/user", "/user.html" })
    public String user() {
        return "client/user";
    }
}
