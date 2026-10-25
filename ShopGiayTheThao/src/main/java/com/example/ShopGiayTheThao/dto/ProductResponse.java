package com.example.ShopGiayTheThao.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.ShopGiayTheThao.entity.Product;
import com.example.ShopGiayTheThao.entity.ProductSize;

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
    private List<SizeDto> sizes = new ArrayList<>();
    private List<String> images = new ArrayList<>();

    public ProductResponse() {
    }

    public static ProductResponse fromEntity(Product p) {
        ProductResponse dto = new ProductResponse();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setSalePrice(p.getSalePrice());
        dto.setThumbnail(p.getThumbnail());

        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }

        if (p.getBrand() != null) {
            dto.setBrandId(p.getBrand().getId());
            dto.setBrandName(p.getBrand().getName());
        }

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            for (com.example.ShopGiayTheThao.entity.ProductImage pi : p.getImages()) {
                if (pi.getUrl() != null && !pi.getUrl().isBlank()) {
                    dto.getImages().add(pi.getUrl());
                }
            }
        }
        if (dto.getImages().isEmpty() && p.getThumbnail() != null && !p.getThumbnail().isBlank()) {
            dto.getImages().add(p.getThumbnail());
        }

        if (p.getProductSizes() != null) {
            for (ProductSize ps : p.getProductSizes()) {
                if (ps.getSize() != null) {
                    dto.getSizes().add(new SizeDto(
                        ps.getSize().getId(),
                        ps.getSize().getName(),
                        ps.getStock()
                    ));
                }
            }
        }

        return dto;
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
}
