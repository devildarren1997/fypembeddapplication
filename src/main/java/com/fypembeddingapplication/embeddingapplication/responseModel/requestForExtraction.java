package com.fypembeddingapplication.embeddingapplication.responseModel;

public class requestForExtraction {
    private String embeddedImage;
    private Long userId;
    private String filter;
    private String secondaryPassword;
    public requestForExtraction() {
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getEmbeddedImage() {
        return embeddedImage;
    }

    public void setEmbeddedImage(String embeddedImage) {
        this.embeddedImage = embeddedImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSecondaryPassword() {
        return secondaryPassword;
    }

    public void setSecondaryPassword(String secondaryPassword) {
        this.secondaryPassword = secondaryPassword;
    }
}
