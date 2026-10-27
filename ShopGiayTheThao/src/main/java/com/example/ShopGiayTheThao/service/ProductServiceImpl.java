package com.example.ShopGiayTheThao.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ShopGiayTheThao.dto.ProductDTO;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDTO> getFeaturedProducts() {
        return productRepository.findTop6ByStatusOrderByCreatedAtDesc(1).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByStatusOrderByCreatedAtDesc(1).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    private ProductDTO toDTO(Product p) {
        String categoryName = (p.getCategory() != null) ? p.getCategory().getName() : "Giày Thể Thao";
        String brandName = (p.getBrand() != null) ? p.getBrand().getName() : "Nike";
        Long brandId = (p.getBrand() != null) ? p.getBrand().getId() : null;

        String badge = "Mới Ra Mắt";
        if (p.getSalePrice() != null && p.getPrice() != null && p.getSalePrice().compareTo(p.getPrice()) < 0) {
            long discountPercent = Math.round((1.0 - p.getSalePrice().doubleValue() / p.getPrice().doubleValue()) * 100);
            badge = "Giảm " + discountPercent + "%";
        }

        return new ProductDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getSalePrice(),
                p.getThumbnail(),
                categoryName,
                brandName,
                brandId,
                badge
        );
    }
}
