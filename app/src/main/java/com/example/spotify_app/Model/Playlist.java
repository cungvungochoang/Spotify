package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Playlist {
    @SerializedName("PLAYLIST_ID")
    private int playlistId;

    @SerializedName("TITLE")
    private String title;

    @SerializedName("IMAGE")
    private String image;

    @SerializedName("SONGS")
    private ArrayList<Song> songs;

    @SerializedName("TOTAL_SONG")
    private int totalSong;

    public Playlist(int playlistId, String title, String image, ArrayList<Song> songs, int totalSong) {
        this.playlistId = playlistId;
        this.title = title;
        this.image = image;
        this.songs = songs;
        this.totalSong = totalSong;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public int getTotalSong() {
        return totalSong;
    }

    public void setTotalSong(int totalSong) {
        this.totalSong = totalSong;
    }
}
