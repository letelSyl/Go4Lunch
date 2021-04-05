package com.example.go4lunch.models;

import com.example.go4lunch.models.User.User;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private String userSenderName;
    private String userSenderPic;

    public Message() { }

    public Message(String message, User userSender) {
        this.message = message;
        this.userSenderName = userSender.getUsername();
        this.userSenderPic = userSender.getUrlPicture();
    }



    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public String getUserSenderName(){return userSenderName;}
    public String getUserSenderPic(){return userSenderPic;}

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public String setUserSenderName(){return userSenderName;}
    public String setUserSenderPic(){return userSenderPic;}
}

