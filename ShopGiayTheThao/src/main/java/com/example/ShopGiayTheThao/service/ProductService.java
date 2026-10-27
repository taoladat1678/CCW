package com.example.ShopGiayTheThao.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.BrandStatsResponse;
import com.example.ShopGiayTheThao.dto.CategoryStatsResponse;
import com.example.ShopGiayTheThao.dto.ProductRequest;
import com.example.ShopGiayTheThao.dto.ProductResponse;
import com.example.ShopGiayTheThao.dto.ProductStatsResponse;
import com.example.ShopGiayTheThao.dto.SizeDto;
import com.example.ShopGiayTheThao.entity.Brand;
import com.example.ShopGiayTheThao.entity.Category;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.ProductImage;
import com.example.ShopGiayTheThao.entity.ProductSize;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.repository.BrandRepository;
import com.example.ShopGiayTheThao.repository.CartItemRepository;
import com.example.ShopGiayTheThao.repository.CategoryRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.repository.SizeRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SizeRepository sizeRepository;
    private final CartItemRepository cartItemRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            SizeRepository sizeRepository,
            CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.sizeRepository = sizeRepository;
        this.cartItemRepository = cartItemRepository;
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

        int totalStock = 0;
        if (p.getProductSizes() != null) {
            for (ProductSize ps : p.getProductSizes()) {
                if (ps.getSize() != null) {
                    dto.getSizes().add(new SizeDto(
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getStock()
                    ));
                    if (ps.getStock() != null) {
                        totalStock += ps.getStock();
                    }
                }
            }
        }
        dto.setTotalStock(totalStock);

        dto.setStatus(p.getStatus());
        dto.setCreatedAt(p.getCreatedAt());

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

    public long getTotalProducts() {
        return productRepository.count();
    }

    public ProductStatsResponse getProductStats() {
        long total = getTotalProducts();
        return new ProductStatsResponse(total, true);
    }

    public long getTotalCategories() {
        return categoryRepository.count();
    }

    public CategoryStatsResponse getCategoryStats() {
        long total = getTotalCategories();
        return new CategoryStatsResponse(total, true);
    }

    public long getTotalBrands() {
        return brandRepository.count();
    }

    public BrandStatsResponse getBrandStats() {
        long total = getTotalBrands();
        return new BrandStatsResponse(total, true);
    }

    public List<ProductResponse> getRecentProducts() {
        List<Product> list = productRepository.findTop5ByOrderByIdDesc();
        return list.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> list = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return list.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Lấy danh sách sản phẩm cho Admin có hỗ trợ tìm kiếm theo từ khóa (tên / mã SP),
     * lọc theo danh mục, lọc theo thương hiệu, lọc theo trạng thái và phân trang
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsForAdmin(
            int page,
            int size,
            String keyword,
            Long categoryId,
            Long brandId,
            String status) {
        int pageNumber = Math.max(page, 0);
        int pageSize = (size > 0) ? size : 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKw = kw;
        if (cleanKw != null && cleanKw.toLowerCase().startsWith("sp-")) {
            cleanKw = cleanKw.substring(3).trim();
        } else if (cleanKw != null && cleanKw.toLowerCase().startsWith("sp")) {
            cleanKw = cleanKw.substring(2).trim();
        }

        String st = (status != null && !status.trim().isEmpty()) ? status.trim() : null;

        return productRepository.filterProductsForAdmin(kw, cleanKw, categoryId, brandId, st, pageable)
                .map(this::convertToResponse);
    }

    public Page<ProductResponse> getProductsForAdmin(int page, int size) {
        return getProductsForAdmin(page, size, null, null, null, null);
    }


    /**
     * Thêm mới sản phẩm từ ProductRequest
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên sản phẩm không được để trống!");
        }
        if (request.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá sản phẩm không được để trống!");
        }

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setThumbnail((request.getThumbnail() != null && !request.getThumbnail().isBlank()) ? request.getThumbnail().trim() : null);
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        product.setCreatedAt(java.time.LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục!"));
            product.setCategory(category);
        } else {
            categoryRepository.findAll().stream().findFirst().ifPresent(product::setCategory);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
            product.setBrand(brand);
        } else {
            brandRepository.findAll().stream().findFirst().ifPresent(product::setBrand);
        }

        Product saved = productRepository.save(product);

        List<Long> selectedSizeIds = request.getSizeIds();

        if (selectedSizeIds != null && !selectedSizeIds.isEmpty()) {
            for (Long sId : selectedSizeIds) {
                Size s = sizeRepository.findById(sId).orElse(null);
                if (s != null) {
                    int sizeStock = request.getStockForSize(sId);
                    ProductSize ps = new ProductSize(saved, s, sizeStock);
                    saved.getProductSizes().add(ps);
                }
            }
            saved = productRepository.save(saved);
        } else {
            List<Size> allSizes = sizeRepository.findAll();
            if (!allSizes.isEmpty()) {
                int defaultStock = (request.getStock() != null && request.getStock() > 0) ? request.getStock() : 50;
                ProductSize ps = new ProductSize(saved, allSizes.get(0), defaultStock);
                saved.getProductSizes().add(ps);
                saved = productRepository.save(saved);
            }
        }

        return convertToResponse(saved);
    }

    /**
     * Cập nhật thông tin sản phẩm từ ProductRequest (hỗ trợ chọn danh sách kích thước hiện có và số lượng từng size)
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        product.setSalePrice(request.getSalePrice());
        if (request.getThumbnail() != null && !request.getThumbnail().isBlank()) {
            product.setThumbnail(request.getThumbnail().trim());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục!"));
            product.setCategory(category);
        }
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
            product.setBrand(brand);
        }

        List<Long> targetSizeIds = request.getSizeIds();

        if (targetSizeIds != null && !targetSizeIds.isEmpty()) {
            // 1. Xóa các size không còn được chọn
            product.getProductSizes().removeIf(ps -> ps.getSize() == null || !targetSizeIds.contains(ps.getSize().getId()));

            // 2. Thêm mới các size được chọn thêm hoặc cập nhật tồn kho riêng cho từng size
            for (Long sId : targetSizeIds) {
                int sizeStock = request.getStockForSize(sId);
                boolean exists = false;
                for (ProductSize ps : product.getProductSizes()) {
                    if (ps.getSize() != null && ps.getSize().getId().equals(sId)) {
                        exists = true;
                        ps.setStock(sizeStock);
                        break;
                    }
                }
                if (!exists) {
                    Size s = sizeRepository.findById(sId).orElse(null);
                    if (s != null) {
                        product.getProductSizes().add(new ProductSize(product, s, sizeStock));
                    }
                }
            }
        } else if (request.getStock() != null && request.getStock() > 0) {
            for (ProductSize ps : product.getProductSizes()) {
                ps.setStock(request.getStock());
            }
        }

        Product updated = productRepository.save(product);
        return convertToResponse(updated);
    }

    /**
     * Xóa sản phẩm theo ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm để xóa với ID: " + id);
        }
        cartItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}
