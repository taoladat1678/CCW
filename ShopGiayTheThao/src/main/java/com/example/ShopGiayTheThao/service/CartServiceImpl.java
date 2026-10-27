package com.example.ShopGiayTheThao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.ShopGiayTheThao.dto.AddToCartRequest;
import com.example.ShopGiayTheThao.dto.CartItemDTO;
import com.example.ShopGiayTheThao.dto.CartResponseDTO;
import com.example.ShopGiayTheThao.entity.CartItem;
import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.Size;
import com.example.ShopGiayTheThao.entity.User;
import com.example.ShopGiayTheThao.repository.CartItemRepository;
import com.example.ShopGiayTheThao.repository.ProductRepository;
import com.example.ShopGiayTheThao.repository.SizeRepository;
import com.example.ShopGiayTheThao.repository.UserRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           SizeRepository sizeRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
    }

    @Override
    public CartItem addToCart(AddToCartRequest request) {
        if (request.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID sản phẩm không được để trống!");
        }

        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

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

    @Override
    @Transactional(readOnly = true)
    public long getCartCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return cartItemRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {
        Long targetUserId = resolveUserId(userId);
        List<CartItem> items = cartItemRepository.findByUserIdOrderByIdAsc(targetUserId);

        List<CartItemDTO> dtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : items) {
            CartItemDTO dto = toDTO(item);
            dtos.add(dto);
            subtotal = subtotal.add(dto.getItemTotal());
            totalQuantity += dto.getQuantity();
        }

        BigDecimal shippingFee = BigDecimal.ZERO; // Free shipping for members
        BigDecimal discount = calculateDiscount(subtotal);
        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        return new CartResponseDTO(
                dtos,
                subtotal,
                shippingFee,
                discount,
                total,
                dtos.size(),
                totalQuantity
        );
    }

    @Override
    public CartItemDTO updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        Long targetUserId = resolveUserId(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));

        if (!item.getUser().getId().equals(targetUserId)) {
            if (targetUserId != null) {
                userRepository.findById(targetUserId).ifPresent(item::setUser);
            }
        }

        if (quantity == null || quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(Math.min(quantity, 99));
        CartItem saved = cartItemRepository.save(item);
        return toDTO(saved);
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        cartItemRepository.findById(cartItemId).ifPresent(cartItemRepository::delete);
    }

    @Override
    public CartItemDTO addToCart(Long userId, Long productId, Long sizeId, Integer quantity) {
        Long targetUserId = resolveUserId(userId);
        User user = userRepository.findById(targetUserId)
                .orElseGet(() -> userRepository.findFirstByOrderByIdAsc().orElse(null));

        if (user == null) {
            user = new User("Khách Hàng Nike", "customer@nike.vn", "123456", "0901234567", "Hà Nội", "CUSTOMER", 1);
            user = userRepository.save(user);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Size size = (sizeId != null) ? sizeRepository.findById(sizeId).orElse(null) : null;
        if (size == null) {
            size = sizeRepository.findAll().stream().findFirst().orElseGet(() -> sizeRepository.save(new Size("EU 40.5")));
        }

        int addQty = (quantity != null && quantity > 0) ? quantity : 1;

        CartItem existing = cartItemRepository.findByUserIdAndProductIdAndSizeId(user.getId(), product.getId(), size.getId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + addQty);
            return toDTO(cartItemRepository.save(existing));
        } else {
            CartItem newItem = new CartItem(user, product, size, addQty);
            return toDTO(cartItemRepository.save(newItem));
        }
    }

    private Long resolveUserId(Long userId) {
        if (userId != null) return userId;
        return userRepository.findFirstByOrderByIdAsc()
                .map(User::getId)
                .orElse(1L);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("10000000")) >= 0) {
            return new BigDecimal("900000");
        } else if (subtotal.compareTo(new BigDecimal("5000000")) >= 0) {
            return new BigDecimal("450000");
        }
        return BigDecimal.ZERO;
    }

    private CartItemDTO toDTO(CartItem item) {
        Product p = item.getProduct();
        String productName = (p != null) ? p.getName() : "Sản Phẩm";
        String thumbnail = (p != null) ? p.getThumbnail() : "";
        String categoryName = (p != null && p.getCategory() != null) ? p.getCategory().getName() : "Giày Nam Thể Thao";
        String sizeName = (item.getSize() != null) ? item.getSize().getName() : "EU 40.5";

        BigDecimal price = (p != null && p.getPrice() != null) ? p.getPrice() : BigDecimal.ZERO;
        BigDecimal salePrice = (p != null) ? p.getSalePrice() : null;

        BigDecimal activePrice = (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0) ? salePrice : price;
        BigDecimal itemTotal = activePrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemDTO(
                item.getId(),
                (p != null) ? p.getId() : null,
                productName,
                thumbnail,
                categoryName,
                sizeName,
                price,
                salePrice,
                item.getQuantity(),
                itemTotal
        );
    }
}
