package com.fypembeddingapplication.embeddingapplication.database;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fypembeddingapplication.embeddingapplication.model.encryptionDetail;

import java.util.List;
import java.util.Optional;
public interface EncryptionDetailsRepository extends JpaRepository<encryptionDetail,Long>{

    Optional <List<encryptionDetail>> findByUserIdAndEncryptedString(Long userId, String encryptedString);
}
