package com.fypembeddingapplication.embeddingapplication.database;
import com.fypembeddingapplication.embeddingapplication.model.embeddedDetails;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmbeddedDetailsRepository extends JpaRepository<embeddedDetails,Long> {
//   Optional<embeddedDetails> findByUserIdAndOriginalImagesID(Long user_id, Long original_Image_ID);
//    @Query (value = "select DISTINCT e.embeddedKey from embeddedDetails e where e.userId = :user_id")
//    Optional<List<String>> getAllUserEmbeddedDetail (@Param("user_id") Long user_id);
//
//    @Query (value = "select  DISTINCT e.embeddedKey from embeddedDetails e where e.userId = :user_id and e.originalImagesID = : original_images_id")
//    Optional <List<String>> getEmbeddedDetailBaseOnuUserIDAndOriginalImageId (@Param("user_id") Long userId, @Param("original_images_id") Long originalImagesId);
//    Optional <List<embeddedDetails>> getAllByUserId (Long userId);
    Optional <List<embeddedDetails>> getAllByUserIdAndOriginalImagesID (Long userId, Long originalImagesID);
    @Query (value = "select * from embedded_detail  where user_id=:userId AND original_images_id = :originalImagesID AND filter=:filter" ,nativeQuery = true)
    List<embeddedDetails> getAllByUserIdAndOriginalImagesIDAndFilter (@Param("userId")Long userId, @Param("originalImagesID")Long originalImagesID,@Param("filter")String filter );
    @Query (value = "select * from embedded_detail where user_id =:userId AND filter=:filter", nativeQuery = true)
    List<embeddedDetails> getAllByUserId(@Param("userId") Long userId, @Param("filter")String filter);

}


