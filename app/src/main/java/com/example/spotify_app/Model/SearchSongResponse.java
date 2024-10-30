package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchSongResponse {
    @SerializedName("error")
    Boolean error;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    ArrayList<Song> songs;

    public SearchSongResponse(Boolean error, String message, ArrayList<Song> songs) {
        this.error = error;
        this.message = message;
        this.songs = songs;
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
