package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Album {
    @SerializedName("ALBUM_ID")
    int albumId;
    @SerializedName("TITLE")
    String title;
    @SerializedName("ARTIST_NAMES")
    String artists;
    @SerializedName("DESCRIPTION")
    String description;
    @SerializedName("IMAGE")
    String image;
    @SerializedName("RELEASE_DATE")
    Date releaseDate;
    @SerializedName("TOTAL_LISTEN")
    int totalListen;

    @SerializedName("SONGS")
    ArrayList<Song> songs;

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getTotalListen() {
        return totalListen;
    }

    public void setTotalListen(int totalListen) {
        this.totalListen = totalListen;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
