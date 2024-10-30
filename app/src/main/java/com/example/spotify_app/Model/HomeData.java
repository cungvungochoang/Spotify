package com.example.spotify_app.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HomeData {
    @SerializedName("banner")
    ArrayList<Banner> banners;
    @SerializedName("albums")
    ArrayList<Album> albums;
    @SerializedName("songs")
    ArrayList<Song> songs;

    public ArrayList<Banner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<Banner> banners) {
        this.banners = banners;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
