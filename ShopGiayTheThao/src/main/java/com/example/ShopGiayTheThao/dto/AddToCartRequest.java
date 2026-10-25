package com.example.ShopGiayTheThao.dto;

public class AddToCartRequest {

    private Long userId;
    private Long productId;
    private Long sizeId;
    private Integer quantity = 1;

    public AddToCartRequest() {
    }

    public AddToCartRequest(Long userId, Long productId, Long sizeId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = (quantity != null && quantity > 0) ? quantity : 1;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
