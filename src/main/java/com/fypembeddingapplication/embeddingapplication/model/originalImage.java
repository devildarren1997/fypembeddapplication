package com.fypembeddingapplication.embeddingapplication.model;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "original_image")
public class originalImage implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "original_image_Id")
    private Long originalImageId;
    @Column(name = "user_Id")
    private Long userId;
    @Column(name = "name")
    private String name;
//    @Column(name = "type")
//    private String type;
    @Column(name = "image_base64",columnDefinition="LONGTEXT")
    private String imageBase64;
    @Column(name = "image_compressed_base64",columnDefinition="LONGTEXT")
    private String imageCompressedBase64;
    public originalImage(@NotBlank Long userId, String name, String imageBase64, String imageCompressedBase64) {
        this.userId = userId;
        this.name = name;
//        this.type = type;
        this.imageBase64 = imageBase64;
        this.imageCompressedBase64 =imageCompressedBase64;
    }

    public originalImage() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOriginalImageId() {
        return originalImageId;
    }

    public void setOriginalImageId(Long originalImageId) {
        this.originalImageId = originalImageId;
    }

    public String getName() {
        return name;
    }

    public String getImageCompressedBase64() {
        return imageCompressedBase64;
    }

    public void setImageCompressedBase64(String imageCompressedBase64) {
        this.imageCompressedBase64 = imageCompressedBase64;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
