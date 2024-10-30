package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

public class AlbumReponse {

    @SerializedName("error")
    Boolean error;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    Album album;

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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public AlbumReponse() {
    }

    public AlbumReponse(Boolean error, String message, Album album) {
        this.error = error;
        this.message = message;
        this.album = album;
    }
}
