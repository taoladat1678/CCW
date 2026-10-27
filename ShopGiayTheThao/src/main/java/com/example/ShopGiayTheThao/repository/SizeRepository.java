package com.example.ShopGiayTheThao.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query(value = "SELECT s FROM Size s WHERE " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(s.id, '') LIKE CONCAT('%', :cleanKeyword, '%'))",
           countQuery = "SELECT COUNT(s) FROM Size s WHERE " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR CONCAT(s.id, '') LIKE CONCAT('%', :cleanKeyword, '%'))")
    Page<Size> filterSizes(
            @Param("keyword") String keyword,
            @Param("cleanKeyword") String cleanKeyword,
            Pageable pageable);
}
