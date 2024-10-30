package com.example.spotify_app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;


// Create channel Android api >= 26
public class MyApplication extends Application {

    public static final String CHANNEL_ID = "CHANNEL_SPOTIFY";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri soundNotificationUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blank_sound);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Channel Music", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(soundNotificationUri, audioAttributes);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null)
            {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
