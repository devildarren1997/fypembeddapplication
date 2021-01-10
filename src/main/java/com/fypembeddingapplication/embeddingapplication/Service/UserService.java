package com.fypembeddingapplication.embeddingapplication.Service;

import com.fypembeddingapplication.embeddingapplication.Encryption.ASEEncryption;
import com.fypembeddingapplication.embeddingapplication.database.UserRepository;
import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import com.fypembeddingapplication.embeddingapplication.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService{
    @Autowired
    private UserRepository userRepository;
    private ConfirmationTokenService confirmationTokenService ;
    private EmailSenderService emailSenderService;


    public int signUpUser (String email,String username, String password){

        String encryptedPassword = encryptPassword(email,password);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            if (optionalUser.get().getEnabled()){
                return 1;
            }else {
                return 2;
            }
        }else {
           User user = new User(email,username,encryptedPassword);
           userRepository.save(user);
           ConfirmationToken confirmationToken = new ConfirmationToken(email,user.getId());
           confirmationTokenService.saveConfirmationToken(confirmationToken);
           int sendEmailIndicator=sendConfirmationMail(email,confirmationToken.getConfirmationToken(),1);
           if (sendEmailIndicator==1){
               return 3;
           }else return 4;
         }
    }
    public int signInUser(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()){
            String encryptedPassword = encryptPassword(email,password);
            if (optionalUser.get().getPassword().equals(encryptedPassword)){
                if (optionalUser.get().isEnabled()){
                    optionalUser.get().setToken(generateToken());
                    userRepository.save(optionalUser.get());
                    return 1;
                }else {
                    return 2;
                }
            }else {
                return 3;
            }
        }else {
            return 3;
        }
    }

    public Long getUserId(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.get().getId();
    }

   public int sendConfirmationMail(String userMail, String token,int type){
        int indicator =emailSenderService .sendEmail(userMail,token,type);
        if (indicator==1){
            return 1;
        }else {
            return 2;
        }
    }
   public int confirmUser (ConfirmationToken confirmationToken){
        try{
            String email=confirmationToken.getEmail();
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()){
                User user=optionalUser.get();
                user.setEnabled(true);
                userRepository.save(user);
                confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
                return 1;
            }else{
                return 2;
            }
        }catch (Exception e){
            return 3;
        }
    }
    public int confirmChangingPassword (ConfirmationToken confirmationToken){

            try{
                String email=confirmationToken.getEmail();
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if (optionalUser.isPresent()){
                    User user=optionalUser.get();
                    user.setChanging(true);
                    userRepository.save(user);
                    confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
                    return 1;
                }else{
                    return 2;
                }
            }catch (Exception e){
                return 3;
            }

    }
    public int forgetPassword (String email){
        Optional <User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            ConfirmationToken confirmationToken = new ConfirmationToken(optionalUser.get().getEmail(),optionalUser.get().getId());
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            int emailIndicator=sendConfirmationMail(email,confirmationToken.getConfirmationToken(),2);
            if (emailIndicator==1){
                return 1;
            }else {
                return 2;
            }
        }else {
            return 3;
        }
    }
    public int changePassword(String email,String password){
        Optional <User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            if (optionalUser.get().isChanging()){
               String encryptedPassword= encryptPassword(email,password);
                if (encryptedPassword.equals(optionalUser.get().getPassword())){
                    return 2;
                }else {
                    optionalUser.get().setPassword(encryptedPassword);
                    optionalUser.get().setChanging(false);
                    userRepository.save(optionalUser.get());
                    return 1;
                }
            }else {
                return 3;
            }
        }else {
            return 3;
        }
    }

    private String encryptPassword(String email, String password){
        ASEEncryption encryption = new ASEEncryption();
        int index = email.indexOf('@');
        String embeddedKey= email.substring(0,index);
        return encryption.encrypt(password,embeddedKey);
    }
    private String generateToken(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        int n = 17;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
    public String getToken(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.get().getToken();
    }
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron ="0/30 * * ? * *")
    public void clearExpired(){
        List<ConfirmationToken>confirmationTokens=confirmationTokenService.getAllExpired();
        System.out.println(LocalDateTime.now());
        if (confirmationTokens!=null){
            for (int i =0;i<confirmationTokens.size();i++){
                String email=confirmationTokens.get(i).getEmail();
                Long id =confirmationTokens.get(i).getId();
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if (optionalUser.isPresent()){
                    if (optionalUser.get().isEnabled()){
                        confirmationTokenService.deleteConfirmationToken(id);
                    }else if (!optionalUser.get().isEnabled()){
                        confirmationTokenService.deleteConfirmationToken(id);
                        userRepository.deleteByEmail(email);
                    }else if (optionalUser.get().isChanging()){
                        confirmationTokenService.deleteConfirmationToken(id);
                        optionalUser.get().setChanging(false);
                        userRepository.save(optionalUser.get());
                    }else if (!optionalUser.get().isChanging()){
                        confirmationTokenService.deleteConfirmationToken(id);
                    }
                }
            }
        }
    }
}
