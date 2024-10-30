package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

public class PlaylistItemResponse {
    @SerializedName("error")
    Boolean error;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    Playlist playlist;

    public PlaylistItemResponse(Boolean error, String message, Playlist playlist) {
        this.error = error;
        this.message = message;
        this.playlist = playlist;
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

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
