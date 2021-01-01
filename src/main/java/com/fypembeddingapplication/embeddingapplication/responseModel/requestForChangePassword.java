package com.fypembeddingapplication.embeddingapplication.responseModel;

public class requestForChangePassword {
    private String email;
    private String password;

    public requestForChangePassword() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
