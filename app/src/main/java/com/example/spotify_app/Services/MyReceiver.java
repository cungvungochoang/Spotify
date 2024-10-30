package com.example.spotify_app.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.spotify_app.Services.MusicService.MusicService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("ActionMusic", 0);
        Intent returnIntent = new Intent(context, MusicService.class);
        returnIntent.putExtra("ActionMusic", action);
        context.startService(returnIntent);
    }
}
