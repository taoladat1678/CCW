package com.example.ShopGiayTheThao.service;

import java.util.List;

import com.example.ShopGiayTheThao.dto.ProductDTO;

public interface ProductService {
    List<ProductDTO> getFeaturedProducts();
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByBrand(Long brandId);
    ProductDTO getProductById(Long id);
}
