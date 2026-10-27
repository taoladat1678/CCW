package com.example.ShopGiayTheThao.service;

import com.example.ShopGiayTheThao.dto.AddToCartRequest;
import com.example.ShopGiayTheThao.dto.CartItemDTO;
import com.example.ShopGiayTheThao.dto.CartResponseDTO;
import com.example.ShopGiayTheThao.entity.CartItem;

public interface CartService {

    CartItem addToCart(AddToCartRequest request);

    long getCartCount(Long userId);

    CartResponseDTO getCart(Long userId);

    CartItemDTO updateQuantity(Long userId, Long cartItemId, Integer quantity);

    void removeItem(Long userId, Long cartItemId);

    CartItemDTO addToCart(Long userId, Long productId, Long sizeId, Integer quantity);
}
