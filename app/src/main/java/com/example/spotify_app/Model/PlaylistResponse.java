package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlaylistResponse {
    @SerializedName("error")
    Boolean error;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    ArrayList<Playlist> playlists;

    public PlaylistResponse(Boolean error, String message, ArrayList<Playlist> playlists) {
        this.error = error;
        this.message = message;
        this.playlists = playlists;
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

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
