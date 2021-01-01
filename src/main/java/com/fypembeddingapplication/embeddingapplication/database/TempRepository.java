package com.fypembeddingapplication.embeddingapplication.database;

import com.fypembeddingapplication.embeddingapplication.model.tempTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempRepository  extends JpaRepository<tempTable,Long> {
    Optional<tempTable> findByUserId (Long userId);
    Optional<tempTable> deleteAllByUserId (Long userId);
}
