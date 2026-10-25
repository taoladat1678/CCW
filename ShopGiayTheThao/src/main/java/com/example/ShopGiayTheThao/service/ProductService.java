package com.example.ShopGiayTheThao.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.SizeDto;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.ProductImage;
import com.example.ShopGiayTheThao.entity.ProductSize;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.CategoryRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.repository.SizeRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SizeRepository sizeRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            SizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.sizeRepository = sizeRepository;
    }

    /**
     * Chuyển đổi từ Entity Product sang ProductResponse DTO (Xử lý nghiệp vụ mapping dữ liệu)
     */
    public ProductResponse convertToResponse(Product p) {
        if (p == null) {
            return null;
        }

        ProductResponse dto = new ProductResponse();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setSalePrice(p.getSalePrice());
        dto.setThumbnail(p.getThumbnail());

        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }

        if (p.getBrand() != null) {
            dto.setBrandId(p.getBrand().getId());
            dto.setBrandName(p.getBrand().getName());
        }

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            for (ProductImage pi : p.getImages()) {
                if (pi.getUrl() != null && !pi.getUrl().isBlank()) {
                    dto.getImages().add(pi.getUrl());
                }
            }
        }
        if (dto.getImages().isEmpty() && p.getThumbnail() != null && !p.getThumbnail().isBlank()) {
            dto.getImages().add(p.getThumbnail());
        }

        if (p.getProductSizes() != null) {
            for (ProductSize ps : p.getProductSizes()) {
                if (ps.getSize() != null) {
                    dto.getSizes().add(new SizeDto(
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getStock()
                    ));
                }
            }
        }

        return dto;
    }

    /**
     * Lấy danh sách sản phẩm có lọc theo nhiều tiêu chí, phân trang và sắp xếp.
     */
    public Page<ProductResponse> getProducts(
            Integer page,
            Integer size,
            Long categoryId,
            Long brandId,
            Long sizeId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword,
            String sort) {

        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 9;

        // Xử lý sắp xếp
        Sort sortObj = Sort.by(Sort.Direction.DESC, "id");
        if ("price-low".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price-high".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "price");
        } else if ("newest".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);

        // Chỉ lấy sản phẩm đang hoạt động status = 1
        return productRepository.filterProducts(
                1,
                categoryId,
                brandId,
                sizeId,
                minPrice,
                maxPrice,
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                pageable
        ).map(this::convertToResponse);
    }

    /**
     * Lấy danh sách danh mục đang hoạt động
     */
    public List<Category> getAllCategories() {
        List<Category> list = categoryRepository.findByStatus(1);
        if (list.isEmpty()) {
            return categoryRepository.findAll();
        }
        return list;
    }

    /**
     * Lấy danh sách thương hiệu
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * Lấy danh sách kích thước (Size)
     */
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    /**
     * Lấy chi tiết sản phẩm theo ID
     */
    public ProductResponse getProductById(Long id) {
        if (id == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "ID sản phẩm không hợp lệ!");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));
        return convertToResponse(product);
    }

    /**
     * Lấy danh sách sản phẩm cùng danh mục
     */
    public List<ProductResponse> getRelatedProducts(Long categoryId, Long excludeId) {
        List<Product> list = java.util.Collections.emptyList();
        if (categoryId != null && excludeId != null) {
            list = productRepository.findTop4ByCategoryIdAndIdNotAndStatus(categoryId, excludeId, 1);
        }
        if (list.isEmpty() && excludeId != null) {
            list = productRepository.findTop4ByIdNotAndStatus(excludeId, 1);
        }
        return list.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());
    }
}
