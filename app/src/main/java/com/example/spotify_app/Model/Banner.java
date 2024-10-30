package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("IMAGE")
    String imageUrl;

    public Banner(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
