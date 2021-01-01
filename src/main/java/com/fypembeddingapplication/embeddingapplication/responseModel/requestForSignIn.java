package com.fypembeddingapplication.embeddingapplication.responseModel;

public class requestForSignIn {
    private String email;
    private String password;

    public requestForSignIn() {
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
