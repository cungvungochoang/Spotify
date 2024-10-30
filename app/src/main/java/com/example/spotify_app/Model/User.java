package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("USER_ID")
    private int userId;

    @SerializedName("FULLNAME")
    private String fullName;

    @SerializedName("EMAIL")
    private  String email;

    @SerializedName("IMAGE")
    private String image;

    @SerializedName("ROLE_NAME")
    private String role;

    public User() {

    }

    public User(int userId, String fullName, String email, String image, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.image = image;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
