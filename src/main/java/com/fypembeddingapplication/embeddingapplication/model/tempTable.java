package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;

@Entity
@Table(name = "temp")
public class tempTable {
    @Id
    @Column(name = "user_Id")
    private Long userId;
    @Column(name = "original_image_name")
    private String originalImageName;
    @Column(name = "original_image_base64",columnDefinition="LONGTEXT")
    private String originalImageBase64;
    @Column(name = "originalImage_compressed_base64",columnDefinition="LONGTEXT")
    private String originalImageCompressedBase64;
    @Column(name = "embedded_image_name")
    private String embeddedImageName;
    @Column(name = "filter")
    private String filter;
    @Column(name = "embedded_image_base64" ,columnDefinition="LONGTEXT")
    private String embeddedImage1Base64;
    @Column(name = "embedded_image_compressed_base64",columnDefinition="LONGTEXT")
    private String embeddedImageCompressedBase64;
    @Column(name = "encryption_key")
    private String encryptionKey;
    @Column(name = "encrypted_string")
    private String encryptedString;

    public tempTable(Long userId, String originalImageName, String originalImageBase64, String originalImageCompressedBase64, String embeddedImageName, String filter, String embeddedImage1Base64, String embeddedImageCompressedBase64,String encryptionKey, String encryptedString) {
        this.userId = userId;
        this.originalImageName = originalImageName;
        this.originalImageBase64 = originalImageBase64;
        this.originalImageCompressedBase64 = originalImageCompressedBase64;
        this.embeddedImageName = embeddedImageName;
        this.filter = filter;
        this.embeddedImage1Base64 = embeddedImage1Base64;
        this.embeddedImageCompressedBase64 = embeddedImageCompressedBase64;
        this.encryptionKey = encryptionKey;
        this.encryptedString = encryptedString;
    }

    public tempTable() {
        super();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginalImageName() {
        return originalImageName;
    }

    public void setOriginalImageName(String originalImageName) {
        this.originalImageName = originalImageName;
    }

    public String getOriginalImageBase64() {
        return originalImageBase64;
    }

    public void setOriginalImageBase64(String originalImageBase64) {
        this.originalImageBase64 = originalImageBase64;
    }

    public String getOriginalImageCompressedBase64() {
        return originalImageCompressedBase64;
    }

    public void setOriginalImageCompressedBase64(String originalImageCompressedBase64) {
        this.originalImageCompressedBase64 = originalImageCompressedBase64;
    }

    public String getEmbeddedImageName() {
        return embeddedImageName;
    }

    public void setEmbeddedImageName(String embeddedImageName) {
        this.embeddedImageName = embeddedImageName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getEmbeddedImage1Base64() {
        return embeddedImage1Base64;
    }

    public void setEmbeddedImage1Base64(String embeddedImage1Base64) {
        this.embeddedImage1Base64 = embeddedImage1Base64;
    }

    public String getEmbeddedImageCompressedBase64() {
        return embeddedImageCompressedBase64;
    }

    public void setEmbeddedImageCompressedBase64(String embeddedImageCompressedBase64) {
        this.embeddedImageCompressedBase64 = embeddedImageCompressedBase64;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getEncryptedString() {
        return encryptedString;
    }

    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }
}
