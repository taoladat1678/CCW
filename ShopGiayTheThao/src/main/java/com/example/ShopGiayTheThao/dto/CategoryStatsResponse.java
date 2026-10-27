package com.example.ShopGiayTheThao.dto;

public class CategoryStatsResponse {

    private long totalCategories;
    private boolean success;

    public CategoryStatsResponse() {
    }

    public CategoryStatsResponse(long totalCategories, boolean success) {
        this.totalCategories = totalCategories;
        this.success = success;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
