package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;

@Entity
@Table(name = "encryption_detail")
public class encryptionDetail {
    @Id
    @GeneratedValue
    @Column(name = "encryption_detail_Id")
    private Long encryptionDetailId;
    @Column(name = "user_Id")
    private Long userId;
    @Column(name = "encryption_key")
    private String encryptionKey;
    @Column(name = "encrypted_string")
    private String encryptedString;

    public encryptionDetail() {
        super();
    }

    public encryptionDetail(Long userId, String encryptionKey, String encryptedString) {
        this.userId = userId;
        this.encryptionKey = encryptionKey;
        this.encryptedString = encryptedString;
    }

    public Long getEncryptionDetailId() {
        return encryptionDetailId;
    }

    public void setEncryptionDetailId(Long encryptionDetailId) {
        this.encryptionDetailId = encryptionDetailId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
