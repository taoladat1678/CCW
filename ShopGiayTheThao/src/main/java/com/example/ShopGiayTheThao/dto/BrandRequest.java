package com.example.ShopGiayTheThao.dto;

public class BrandRequest {

    private Long id;
    private String name;
    private String logo;
    private String description;

    public BrandRequest() {
    }

    public BrandRequest(String name, String logo, String description) {
        this.name = name;
        this.logo = logo;
        this.description = description;
    }

    public BrandRequest(Long id, String name, String logo, String description) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.description = description;
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
}
