package com.example.ShopGiayTheThao.service;

import com.example.ShopGiayTheThao.dto.CartItemDTO;
import com.example.ShopGiayTheThao.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCart(Long userId);
    CartItemDTO updateQuantity(Long userId, Long cartItemId, Integer quantity);
    void removeItem(Long userId, Long cartItemId);
    CartItemDTO addToCart(Long userId, Long productId, Long sizeId, Integer quantity);
}
