package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("error")
    Boolean error;
    @SerializedName("message")
    String message;

    @SerializedName("data")
    User user;

    public AuthResponse(Boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
