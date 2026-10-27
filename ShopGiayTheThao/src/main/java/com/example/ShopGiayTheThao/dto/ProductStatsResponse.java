package com.example.ShopGiayTheThao.dto;

public class ProductStatsResponse {

    private long totalProducts;
    private boolean success;

    public ProductStatsResponse() {
    }

    public ProductStatsResponse(long totalProducts, boolean success) {
        this.totalProducts = totalProducts;
        this.success = success;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
