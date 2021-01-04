package com.fypembeddingapplication.embeddingapplication.Service;

import com.fypembeddingapplication.embeddingapplication.database.ConfirmationTokenRepository;
import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private ConfirmationTokenRepository confirmationTokenRepository;
   void saveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }
    void deleteConfirmationToken(Long id){
        confirmationTokenRepository.deleteById(id);
    }
    public ConfirmationToken findConfirmationToken(String token){
        Optional<ConfirmationToken> optionalConfirmationToken= confirmationTokenRepository.findByConfirmationToken(token);
       if (optionalConfirmationToken.isPresent()){
           return optionalConfirmationToken.get();
       }else
           return null;
    }
    public List<ConfirmationToken> getAllExpired(){
        LocalDateTime now = LocalDateTime.now();
        Optional<List<ConfirmationToken>> confirmationTokens= confirmationTokenRepository.getAllExpired(now);
        if (confirmationTokens.isPresent()){
            return confirmationTokens.get();
        }else {
            return null;
        }
    }

}
