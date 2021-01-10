package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;

@Entity
@Table(name = "buffer")
public class Buffer {
    @Id
    @Column(name = "user_Id")
    private Long userId;
    @Column(name = "filter")
    private String filter;
    @Column(name = "encryption_key")
    private String encryptionKey;
    @Column(name = "encrypted_string")
    private String encryptedString;

    public Buffer(Long userId, String filter, String encryptionKey, String encryptedString) {
        this.userId = userId;
        this.filter = filter;
        this.encryptionKey = encryptionKey;
        this.encryptedString = encryptedString;
    }

    public Buffer() {
        super();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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
