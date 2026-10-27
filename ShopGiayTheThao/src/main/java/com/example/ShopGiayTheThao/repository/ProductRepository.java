package com.example.ShopGiayTheThao.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderByCreatedAtDesc(Integer status);
    
    List<Product> findTop6ByStatusOrderByCreatedAtDesc(Integer status);
    
    Page<Product> findByStatus(Integer status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND p.brand.id = :brandId ORDER BY p.createdAt DESC")
    List<Product> findByBrandId(Long brandId);
}
