package com.example.hoang.myapplication.MailBox;

public class Messages {
    private String mess;

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String user;

    public Messages(String mess, String user) {
        this.mess = mess;
        this.user = user;
    }
}
