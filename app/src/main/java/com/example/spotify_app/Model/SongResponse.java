package com.example.spotify_app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SongResponse {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("data")
    @Expose
    private ArrayList<Song> data = null;
    public SongResponse(boolean error, Object message, ArrayList<Song> data) {
        super();
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public ArrayList<Song> getData() {
        return data;
    }

    public void setData(ArrayList<Song> data) {
        this.data = data;
    }
}
