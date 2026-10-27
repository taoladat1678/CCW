package com.example.ShopGiayTheThao.repository;


import java.math.BigDecimal;

import java.util.List;


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
    java.util.List<Product> findTop5ByOrderByCreatedAtDesc();

    java.util.List<Product> findTop5ByOrderByIdDesc();

    /**
     * Tìm kiếm và lọc sản phẩm cho trang quản trị Admin: theo từ khóa (tên/mã SP), danh mục, thương hiệu, trạng thái
     */
    @Query(value = "SELECT p FROM Product p " +
           "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR CAST(p.id AS string) LIKE CONCAT('%', :cleanKeyword, '%')) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
           "AND (" +
           "     :statusFilter IS NULL OR :statusFilter = '' " +
           "     OR (:statusFilter = '1' AND p.status = 1) " +
           "     OR (:statusFilter = '0' AND (p.status = 0 OR (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) = 0)) " +
           "     OR (:statusFilter = 'in_stock' AND p.status = 1 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) > 5) " +
           "     OR (:statusFilter = 'low_stock' AND p.status = 1 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) > 0 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) <= 5) " +
           "     OR (:statusFilter = 'out_stock' AND (p.status = 0 OR (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) = 0)) " +
           ")",
           countQuery = "SELECT COUNT(p) FROM Product p " +
           "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR CAST(p.id AS string) LIKE CONCAT('%', :cleanKeyword, '%')) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
           "AND (" +
           "     :statusFilter IS NULL OR :statusFilter = '' " +
           "     OR (:statusFilter = '1' AND p.status = 1) " +
           "     OR (:statusFilter = '0' AND (p.status = 0 OR (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) = 0)) " +
           "     OR (:statusFilter = 'in_stock' AND p.status = 1 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) > 5) " +
           "     OR (:statusFilter = 'low_stock' AND p.status = 1 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) > 0 AND (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) <= 5) " +
           "     OR (:statusFilter = 'out_stock' AND (p.status = 0 OR (SELECT COALESCE(SUM(ps2.stock), 0) FROM ProductSize ps2 WHERE ps2.product = p) = 0)) " +
           ")")
    Page<Product> filterProductsForAdmin(
        @Param("keyword") String keyword,
        @Param("cleanKeyword") String cleanKeyword,
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("statusFilter") String statusFilter,
        Pageable pageable
    );

    List<Product> findByStatusOrderByCreatedAtDesc(Integer status);
    
    List<Product> findTop6ByStatusOrderByCreatedAtDesc(Integer status);
    
    Page<Product> findByStatus(Integer status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND p.brand.id = :brandId ORDER BY p.createdAt DESC")
    List<Product> findByBrandId(Long brandId);

}
