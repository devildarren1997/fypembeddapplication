package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "embedded_image")
public class embeddedImage implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "embedded_image_Id")
    private Long embeddedImageId;
    @Column(name = "user_Id")
    private Long userId;
    @Column(name = "name")
    private String name;
//    @Column(name = "type")
//    private String type;
    @Column(name = "filter")
    private String filter;
    @Column(name = "image_Base64" ,columnDefinition="LONGTEXT")
    private String image1Base64;
    @Column(name = "image_compressed_base64",columnDefinition="LONGTEXT")
    private String imageCompressedBase64;

    public embeddedImage() {
        super();
    }

    public embeddedImage(@NotBlank Long userId, String name,  String filter,String image1Base64) {
        this.userId = userId;
        this.name = name;
//        this.type = type;
        this.filter = filter;
        this.image1Base64 = image1Base64;
    }

    public embeddedImage(@NotBlank Long userId, String name,String filter,String image1Base64, String imageCompressedBase64) {
        this.userId = userId;
        this.name = name;
//        this.type = type;
        this.filter = filter;
        this.image1Base64 = image1Base64;
        this.imageCompressedBase64 = imageCompressedBase64;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmbeddedImageId() {
        return embeddedImageId;
    }

//    public void setEmbedded_image_Id(Long embedded_image_Id) {
//        this.embedded_image_Id = embedded_image_Id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getImage1Base64() {
        return image1Base64;
    }

    public void setImage1Base64(String image1Base64) {
        this.image1Base64 = image1Base64;
    }

    public String imageCompressedBase64() {
        return imageCompressedBase64;
    }

    public void imageCompressedBase64(String imageCompressedBase64) {
        this.imageCompressedBase64 = imageCompressedBase64;
    }
}
