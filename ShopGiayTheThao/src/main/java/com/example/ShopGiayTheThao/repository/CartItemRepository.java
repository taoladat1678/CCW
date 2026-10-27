package com.example.ShopGiayTheThao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdOrderByIdAsc(Long userId);
    
    Optional<CartItem> findByUserIdAndProductIdAndSizeId(Long userId, Long productId, Long sizeId);

    void deleteByIdAndUserId(Long id, Long userId);
}
