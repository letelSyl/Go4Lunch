package com.example.go4lunch.models.User;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String name;
    private String urlPicture;

    public User() { }

    public User(String uid, String name, String urlPicture) {
        this.uid = uid;
        this.name = name;
        this.urlPicture = urlPicture;

}


    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return name; }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setUsername(String username) { this.name = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
}
