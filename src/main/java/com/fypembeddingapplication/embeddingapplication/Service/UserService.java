package com.fypembeddingapplication.embeddingapplication.Service;

import com.fypembeddingapplication.embeddingapplication.Encryption.ASEEncryption;
import com.fypembeddingapplication.embeddingapplication.database.UserRepository;
import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import com.fypembeddingapplication.embeddingapplication.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

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
           User user = new User(email,username,encryptedPassword,username);
           userRepository.save(user);
           ConfirmationToken confirmationToken = new ConfirmationToken(user);
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
   public void confirmUser (ConfirmationToken confirmationToken){
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
    }
    public void confirmChangingPassword (ConfirmationToken confirmationToken){
        User user = confirmationToken.getUser();
        user.setChanging(true);
        userRepository.save(user);
        confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
    }
    public int forgetPassword (String email){
        Optional <User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            ConfirmationToken confirmationToken = new ConfirmationToken(optionalUser.get());
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
}
