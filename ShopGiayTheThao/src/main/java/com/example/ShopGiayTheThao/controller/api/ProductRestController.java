package com.example.ShopGiayTheThao.controller.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.ProductDTO;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "brand", required = false) Long brandParam,
            @RequestParam(value = "sizeId", required = false) Long sizeId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "featured") String sort,
            @RequestParam(value = "featured", required = false, defaultValue = "false") boolean featured) {

        if (featured) {
            return ResponseEntity.ok(productService.getFeaturedProducts());
        }

        Long targetBrandId = (brandId != null) ? brandId : brandParam;

        if (page != null || size != null || categoryId != null || targetBrandId != null || keyword != null || minPrice != null || maxPrice != null) {
            return ResponseEntity.ok(productService.getProducts(
                    page != null ? page : 0,
                    size != null ? size : 9,
                    categoryId,
                    targetBrandId,
                    sizeId,
                    minPrice,
                    maxPrice,
                    keyword,
                    sort
            ));
        }

        return ResponseEntity.ok(productService.getAllProductsDTO());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeatured() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
