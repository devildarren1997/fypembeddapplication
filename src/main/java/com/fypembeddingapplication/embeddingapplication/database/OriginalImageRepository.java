package com.fypembeddingapplication.embeddingapplication.database;

import com.fypembeddingapplication.embeddingapplication.model.originalImage;
import com.fypembeddingapplication.embeddingapplication.model.OriginalImagePk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OriginalImageRepository extends JpaRepository<originalImage,Long> {
//    Optional<originalImage> findByUser_IdAndOriginal_image_Id(Long user_id,Long original_image_id);
    Optional<originalImage> findByName(String name);
    Optional <List<originalImage>> findByUserId (Long id);
}