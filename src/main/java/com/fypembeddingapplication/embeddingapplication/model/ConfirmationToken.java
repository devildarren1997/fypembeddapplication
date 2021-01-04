package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Entity

@Table(name = "confirmationtoken")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;
    @Column(name = "confirmation_token")
    private String confirmationToken;
    @Column(name = "expired_datetime")
    private LocalDateTime expiredDatetime;
    @Column(name = "email")
    private String email;

   public ConfirmationToken(String email){
        this.email =email;
        this.expiredDatetime =LocalDateTime.now().plusMinutes(5);
        this.confirmationToken = generateToken();
    }
    public ConfirmationToken(){super();}


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmatinToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getExpiredDatetime() {
        return expiredDatetime;
    }

    public void setExpiredDatetime(LocalDateTime expiredDatetime) {
        this.expiredDatetime = expiredDatetime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String userId) {
        this.email = userId;
    }

    public String generateToken(){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0 ;i <6;i++){
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
