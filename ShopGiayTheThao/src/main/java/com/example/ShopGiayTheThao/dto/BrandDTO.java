package com.example.ShopGiayTheThao.dto;

public class BrandDTO {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private String subtitle;
    private int productCount;

    public BrandDTO() {
    }

    public BrandDTO(Long id, String name, String logo, String description, String subtitle, int productCount) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.subtitle = subtitle;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return id != null ? "BRD-" + id : "";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
