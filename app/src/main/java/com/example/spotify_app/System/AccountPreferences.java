package com.example.spotify_app.System;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spotify_app.Model.User;

public class AccountPreferences {
    private static final String PREF_NAME = "AccountPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_ROLE = "role";

    public static void getPreferences(Context context) {
        SharedPreferences sharedPreferences =   context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        if(userId == -1) {
            AccountManager.Ins().setUser(null);
            return;
        }
        String fullName = sharedPreferences.getString(KEY_FULL_NAME, "");
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String image = sharedPreferences.getString(KEY_IMAGE, "");
        String role = sharedPreferences.getString(KEY_ROLE, "");

        User user = new User(userId, fullName, email, image, role);

        AccountManager.Ins().setUser(user);
    }

    public static void setPreferences(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_IMAGE, user.getImage());
        editor.putString(KEY_ROLE, user.getRole());

        editor.apply();
    }

    public static void removePreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_FULL_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_IMAGE);
        editor.remove(KEY_ROLE);
        editor.apply();

        AccountManager._Ins.setUser(null);
    }
}
