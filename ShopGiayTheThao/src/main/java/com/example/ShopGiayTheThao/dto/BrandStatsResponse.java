package com.example.ShopGiayTheThao.dto;

public class BrandStatsResponse {

    private long totalBrands;
    private boolean success;

    public BrandStatsResponse() {
    }

    public BrandStatsResponse(long totalBrands, boolean success) {
        this.totalBrands = totalBrands;
        this.success = success;
    }

    public long getTotalBrands() {
        return totalBrands;
    }

    public void setTotalBrands(long totalBrands) {
        this.totalBrands = totalBrands;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
