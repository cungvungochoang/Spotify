package com.example.spotify_app.System;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.spotify_app.Model.User;

public class AccountManager {
    protected User user = null;
    static AccountManager _Ins;

    public static AccountManager Ins() {
        if(_Ins == null) {
            _Ins = new AccountManager();
        }
        return _Ins;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogin() {
        return user == null ? false : true;
    }
}
