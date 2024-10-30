package com.example.spotify_app.Model;

public class FavoriteSong {

    int USER_ID;
    int SONG_ID;

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public int getSONG_ID() {
        return SONG_ID;
    }

    public void setSONG_ID(int SONG_ID) {
        this.SONG_ID = SONG_ID;
    }



    public FavoriteSong(int userID, int songID) {
        this.USER_ID = userID;
        this.SONG_ID = songID;
    }
}
