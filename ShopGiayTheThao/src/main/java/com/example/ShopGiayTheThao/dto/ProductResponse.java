package com.example.ShopGiayTheThao.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String thumbnail;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Integer status;
    private java.time.LocalDateTime createdAt;
    private Integer totalStock;
    private List<SizeDto> sizes = new ArrayList<>();
    private List<String> images = new ArrayList<>();

    public ProductResponse() {
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<SizeDto> getSizes() {
        return sizes;
    }

    public void setSizes(List<SizeDto> sizes) {
        this.sizes = sizes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalStock() {
        if (totalStock != null) {
            return totalStock;
        }
        if (sizes == null || sizes.isEmpty()) {
            return 0;
        }
        return sizes.stream().mapToInt(s -> s.getStock() != null ? s.getStock() : 0).sum();
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    /**
     * Trả về chuỗi hiển thị tên các kích thước của sản phẩm (ví dụ: 'Size 40' hoặc 'Size 39, Size 40')
     */
    public String getSizeDisplay() {
        if (sizes == null || sizes.isEmpty()) {
            return "";
        }
        return sizes.stream()
                .map(s -> {
                    String name = (s.getName() != null) ? s.getName().trim() : "";
                    if (name.toLowerCase().startsWith("size")) {
                        return name;
                    }
                    return "Size " + name;
                })
                .collect(java.util.stream.Collectors.joining(", "));
    }

    /**
     * Kiểm tra xem sản phẩm có size với ID cụ thể hay không (dùng để render checkbox checked trong form sửa)
     */
    public boolean hasSize(Long sizeId) {
        if (sizes == null || sizeId == null) {
            return false;
        }
        return sizes.stream().anyMatch(sz -> sizeId.equals(sz.getId()));
    }

    /**
     * Lấy số lượng tồn kho của 1 size cụ thể theo sizeId
     */
    public Integer getStockForSize(Long sizeId) {
        if (sizes == null || sizeId == null) {
            return null;
        }
        return sizes.stream()
                .filter(sz -> sizeId.equals(sz.getId()))
                .map(SizeDto::getStock)
                .findFirst()
                .orElse(null);
    }
}
