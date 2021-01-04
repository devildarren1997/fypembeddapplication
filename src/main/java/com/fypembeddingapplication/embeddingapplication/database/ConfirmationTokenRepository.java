package com.fypembeddingapplication.embeddingapplication.database;

import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
    Optional<ConfirmationToken> deleteByConfirmationToken (String confirmationToken);


    @Query(value = "select * from confirmationtoken where expired_datetime<:now" ,nativeQuery = true)
    Optional<List<ConfirmationToken>> getAllExpired(@Param("now")LocalDateTime now);
}
