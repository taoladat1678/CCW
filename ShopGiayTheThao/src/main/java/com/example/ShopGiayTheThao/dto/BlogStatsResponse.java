package com.example.ShopGiayTheThao.dto;

public class BlogStatsResponse {

    private long totalBlogs;
    private boolean success;

    public BlogStatsResponse() {
    }

    public BlogStatsResponse(long totalBlogs, boolean success) {
        this.totalBlogs = totalBlogs;
        this.success = success;
    }

    public long getTotalBlogs() {
        return totalBlogs;
    }

    public void setTotalBlogs(long totalBlogs) {
        this.totalBlogs = totalBlogs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
