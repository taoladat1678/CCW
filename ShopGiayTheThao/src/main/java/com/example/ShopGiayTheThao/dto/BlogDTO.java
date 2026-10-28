package com.example.ShopGiayTheThao.dto;

import java.time.LocalDateTime;

public class BlogDTO {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private String authorName;
    private LocalDateTime createdAt;
    private String categoryTag;
    private Integer status = 1;

    public BlogDTO() {
    }

    public BlogDTO(Long id, String title, String content, String thumbnail, String authorName,
                   LocalDateTime createdAt, String categoryTag) {
        this(id, title, content, thumbnail, authorName, createdAt, categoryTag, 1);
    }

    public BlogDTO(Long id, String title, String content, String thumbnail, String authorName,
                   LocalDateTime createdAt, String categoryTag, Integer status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.categoryTag = categoryTag;
        this.status = (status != null) ? status : 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
