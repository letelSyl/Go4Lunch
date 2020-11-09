package com.example.go4lunch.models.User;

import java.util.ArrayList;

public class User {

    private String uid;
    private String username;
    private String urlPicture;
    private String restName;
    private String restId;
    private ArrayList<String> likedRestaurants;

    public User() { }




    public User(String uid, String username, String urlPicture, String restName, String restId, ArrayList<String> likedRestaurants) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restName = restName;
        this.restId = restId;
        this.likedRestaurants = likedRestaurants;


}


    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getRestName() { return restName; }
    public String getRestId() { return restId; }
    public ArrayList<String> getLikedRestaurants() { return likedRestaurants; }
    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestName(String restName) { this.restName = restName; }
    public void setRestId(String restId) { this.restId = restId; }
    public void setLikedRestaurants(ArrayList<String> likedRestaurants) { this.likedRestaurants = likedRestaurants; }
}
