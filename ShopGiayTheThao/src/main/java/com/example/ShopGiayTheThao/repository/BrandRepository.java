package com.example.ShopGiayTheThao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findAllByOrderByIdAsc();

    Optional<Brand> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query(value = "SELECT b FROM Brand b WHERE " +
           "(:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(b.id, '') LIKE CONCAT('%', :cleanKeyword, '%'))",
           countQuery = "SELECT COUNT(b) FROM Brand b WHERE " +
           "(:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(b.id, '') LIKE CONCAT('%', :cleanKeyword, '%'))")
    Page<Brand> filterBrands(
            @Param("keyword") String keyword,
            @Param("cleanKeyword") String cleanKeyword,
            Pageable pageable);
}
