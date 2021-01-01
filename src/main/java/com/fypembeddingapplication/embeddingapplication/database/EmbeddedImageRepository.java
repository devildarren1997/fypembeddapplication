package com.fypembeddingapplication.embeddingapplication.database;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fypembeddingapplication.embeddingapplication.model.embeddedImage;
import com.fypembeddingapplication.embeddingapplication.model.EmbeddedImagePk;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmbeddedImageRepository extends JpaRepository<embeddedImage,Long> {
//    Optional<embeddedImage> findByUser_IdAndEmbedded_image_Id(Long user_id,Long embedded_image_id );
    Optional <embeddedImage> findByEmbeddedImageId(Long embeddedImageId);
    Optional<embeddedImage> findByName(String name);

}