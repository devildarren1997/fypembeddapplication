package com.fypembeddingapplication.embeddingapplication.model;

import javax.persistence.*;
import java.time.LocalDate;
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
    @Column(name = "created_date")
    private LocalDate createdDate;
    @OneToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(nullable = false,name = "id")
    private User user;

   public ConfirmationToken(User user){
        this.user =user;
        this.createdDate =LocalDate.now();
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
