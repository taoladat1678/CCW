package com.example.ShopGiayTheThao.dto;

import java.math.BigDecimal;

public class ProductRequest {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String thumbnail;
    private Long categoryId;
    private Long brandId;
    private Integer status = 1;
    private Integer stock = 0;

    public ProductRequest() {
    }

    public ProductRequest(Long id, String name, String description, BigDecimal price, BigDecimal salePrice, String thumbnail, Long categoryId, Long brandId, Integer status, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.thumbnail = thumbnail;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.status = status;
        this.stock = stock;
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private Long sizeId;
    private java.util.List<Long> sizeIds = new java.util.ArrayList<>();
    private java.util.Map<String, Integer> sizeStocks = new java.util.HashMap<>();

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public java.util.List<Long> getSizeIds() {
        if ((sizeIds == null || sizeIds.isEmpty()) && sizeId != null) {
            java.util.List<Long> list = new java.util.ArrayList<>();
            list.add(sizeId);
            return list;
        }
        return sizeIds != null ? sizeIds : new java.util.ArrayList<>();
    }

    public void setSizeIds(java.util.List<Long> sizeIds) {
        this.sizeIds = sizeIds;
    }

    public java.util.Map<String, Integer> getSizeStocks() {
        return sizeStocks != null ? sizeStocks : new java.util.HashMap<>();
    }

    public void setSizeStocks(java.util.Map<String, Integer> sizeStocks) {
        this.sizeStocks = sizeStocks;
    }

    public Integer getStockForSize(Long sizeId) {
        if (sizeStocks != null && sizeId != null) {
            String strKey = String.valueOf(sizeId);
            if (sizeStocks.containsKey(strKey) && sizeStocks.get(strKey) != null) {
                return sizeStocks.get(strKey);
            }
            for (java.util.Map.Entry<?, ?> entry : sizeStocks.entrySet()) {
                if (entry.getKey() != null && String.valueOf(entry.getKey()).equals(strKey)) {
                    if (entry.getValue() != null) {
                        try {
                            return Integer.parseInt(String.valueOf(entry.getValue()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        return (stock != null && stock > 0) ? stock : 50;
    }
}
