package com.example.ShopGiayTheThao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ShopGiayTheThao.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Lấy giỏ hàng theo user ID
    List<CartItem> findByUserId(Long userId);

    // Tìm món hàng theo user và sản phẩm
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    // Tìm món hàng theo user, sản phẩm và kích thước
    Optional<CartItem> findByUserIdAndProductIdAndSizeId(Long userId, Long productId, Long sizeId);

    // Đếm tổng số lượng món trong giỏ của user
    long countByUserId(Long userId);

    // Xóa tất cả món hàng liên quan đến sản phẩm
    void deleteByProductId(Long productId);
}
