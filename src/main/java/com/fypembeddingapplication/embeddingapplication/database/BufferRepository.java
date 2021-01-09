package com.fypembeddingapplication.embeddingapplication.database;

import com.fypembeddingapplication.embeddingapplication.model.Buffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BufferRepository extends JpaRepository<Buffer,Long> {
    Optional<Buffer> findByUserId (Long userId);
    Optional<Buffer> deleteAllByUserId (Long userId);
}
