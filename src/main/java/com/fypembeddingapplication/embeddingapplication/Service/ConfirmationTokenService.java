package com.fypembeddingapplication.embeddingapplication.Service;

import com.fypembeddingapplication.embeddingapplication.database.ConfirmationTokenRepository;
import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
