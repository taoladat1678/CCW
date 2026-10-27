package com.example.ShopGiayTheThao.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShopGiayTheThao.dto.CartItemDTO;
import com.example.ShopGiayTheThao.dto.CartResponseDTO;
import com.example.ShopGiayTheThao.dto.UpdateQuantityRequest;
import com.example.ShopGiayTheThao.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            @RequestParam(name = "userId", required = false) Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            @PathVariable Long id,
            @RequestBody(required = false) UpdateQuantityRequest request,
            @RequestParam(name = "quantity", required = false) Integer queryQuantity,
            @RequestParam(name = "userId", required = false) Long userId) {
        Integer qty = (request != null && request.getQuantity() != null) ? request.getQuantity() : queryQuantity;
        if (qty == null) {
            qty = 1;
        }
        cartService.updateQuantity(userId, id, qty);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userId) {
        cartService.removeItem(userId, id);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> addToCart(
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "sizeId", required = false) Long sizeId,
            @RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
            @RequestParam(name = "userId", required = false) Long userId) {
        cartService.addToCart(userId, productId, sizeId, quantity);
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
