package com.fypembeddingapplication.embeddingapplication.Service;

import com.fypembeddingapplication.embeddingapplication.database.ConfirmationTokenRepository;
import com.fypembeddingapplication.embeddingapplication.database.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class EmailSenderService {
    private JavaMailSender javaMailSender;
    private UserRepository userRepository;
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Async
    public int sendEmail(String userMail, String token,int type){
        //type 1 for sign up, type 2 for forget password.
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
            messageHelper.setTo(userMail);
            messageHelper.setSubject("Mail Confirmation Link!");
            messageHelper.setText("Thank you for registering. Please enter below token in the application to activate your account:" + token );
            javaMailSender.send(mailMessage);
            return 1;
        }catch (MessagingException e){
            e.printStackTrace();
            if (type==1){
                userRepository.deleteByEmail(userMail);
                confirmationTokenRepository.deleteByConfirmationToken(token);
            }

            return 2;
        }catch (MailException e){
            e.printStackTrace();
            if (type==1){
                userRepository.deleteByEmail(userMail);
                confirmationTokenRepository.deleteByConfirmationToken(token);
            }
            return 2;
        }

    }
}
