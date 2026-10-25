package com.example.ShopGiayTheThao.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lọc sản phẩm kết hợp nhiều tiêu chí: danh mục, thương hiệu, size, khoảng giá, từ khóa
     * Hỗ trợ phân trang và sắp xếp thông qua Pageable
     */
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN p.productSizes ps " +
           "WHERE (:status IS NULL OR p.status = :status) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
           "AND (:sizeId IS NULL OR ps.size.id = :sizeId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> filterProducts(
        @Param("status") Integer status,
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("sizeId") Long sizeId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    /**
     * Lấy các sản phẩm cùng danh mục (loại trừ sản phẩm hiện tại)
     */
    java.util.List<Product> findTop4ByCategoryIdAndIdNotAndStatus(Long categoryId, Long id, Integer status);

    /**
     * Fallback lấy các sản phẩm khác (loại trừ sản phẩm hiện tại)
     */
    java.util.List<Product> findTop4ByIdNotAndStatus(Long id, Integer status);
}
