package com.example.ShopGiayTheThao.dto;

public class CustomerStatsResponse {

    private long totalCustomers;
    private boolean success;

    public CustomerStatsResponse() {
    }

    public CustomerStatsResponse(long totalCustomers, boolean success) {
        this.totalCustomers = totalCustomers;
        this.success = success;
    }

    // Getters and Setters
    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
