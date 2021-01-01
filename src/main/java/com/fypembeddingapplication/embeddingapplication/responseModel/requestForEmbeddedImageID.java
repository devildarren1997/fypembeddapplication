package com.fypembeddingapplication.embeddingapplication.responseModel;

public class requestForEmbeddedImageID {
    private Long userId;
    private String imageBase64;
    private String name;
    private String filter;
    private String secondaryPassword;
    private String embedText;
    public requestForEmbeddedImageID() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSecondaryPassword() {
        return secondaryPassword;
    }

    public void setSecondaryPassword(String secondaryPassword) {
        this.secondaryPassword = secondaryPassword;
    }

    public String getEmbedText() {
        return embedText;
    }

    public void setEmbedText(String embedText) {
        this.embedText = embedText;
    }
}
