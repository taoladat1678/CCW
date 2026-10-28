package com.example.ShopGiayTheThao.dto;

public class BlogRequest {

    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private String authorName;
    private Long authorId;
    private Integer status;
    private String categoryTag;

    public BlogRequest() {
    }

    public BlogRequest(Long id, String title, String content, String thumbnail, String authorName, Long authorId, Integer status, String categoryTag) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.authorName = authorName;
        this.authorId = authorId;
        this.status = status;
        this.categoryTag = categoryTag;
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
    }
}
