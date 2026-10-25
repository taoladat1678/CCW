package com.example.ShopGiayTheThao.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.AddToCartRequest;
import com.example.ShopGiayTheThao.entity.CartItem;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.repository.CartItemRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.repository.SizeRepository;
import com.example.ShopGiayTheThao.repository.UserRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final UserRepository userRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            SizeRepository sizeRepository,
            UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public CartItem addToCart(AddToCartRequest request) {
        if (request.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID sản phẩm không được để trống!");
        }

        // Bắt lỗi bắt buộc người dùng phải đăng nhập mới cho thêm giỏ hàng
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        // Lấy kích thước
        Size size = null;
        if (request.getSizeId() != null) {
            size = sizeRepository.findById(request.getSizeId()).orElse(null);
        }
        if (size == null && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
            size = product.getProductSizes().get(0).getSize();
        }
        if (size == null) {
            size = sizeRepository.findAll().stream().findFirst().orElse(null);
        }

        int addQty = (request.getQuantity() != null && request.getQuantity() > 0) ? request.getQuantity() : 1;

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + addQty);
            if (size != null) {
                cartItem.setSize(size);
            }
        } else {
            cartItem = new CartItem(user, product, size, addQty);
        }

        return cartItemRepository.save(cartItem);
    }

    /**
     * Lấy tổng số lượng món trong giỏ hàng của user
     */
    public long getCartCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return cartItemRepository.countByUserId(userId);
    }
}
