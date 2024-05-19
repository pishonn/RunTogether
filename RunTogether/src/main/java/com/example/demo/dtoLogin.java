package com.example.demo;

public class dtoLogin {
    
    public String userId;
    public String pw;

    public dtoLogin(String userId, String pw) {
        super();
        this.userId = userId;
        this.pw = pw;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    
}
