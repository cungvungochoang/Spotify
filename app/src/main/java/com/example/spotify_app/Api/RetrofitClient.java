package com.example.spotify_app.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    Retrofit retrofit;
//    private static String BASE_URL = "http://192.168.222.200:5000/api/";
    private static String BASE_URL = "https://spotify-api-roan.vercel.app/api/";
    public RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
