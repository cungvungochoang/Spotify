package com.example.spotify_app.Services.MusicService;

import static com.example.spotify_app.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.spotify_app.Const.MusicServiceConst;
import com.example.spotify_app.Model.Song;
import com.example.spotify_app.Model.TypeSong;
import com.example.spotify_app.MusicPlayer;
import com.example.spotify_app.R;
import com.example.spotify_app.Services.MyReceiver;

import java.io.IOException;

public class MusicService extends Service {


    public static final int LOAD_DATA = -1;
    public static final int ACTION_PLAY = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_BACK = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_CLEAR = 6;
    public static final int ACTION_UPDATE = 7;


    private ImageView img;

    Song song = null;

    @Override
    public void onCreate() {
        super.onCreate();
        img = new ImageView(getBaseContext());
        Log.e("Music service", "Music service was created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("Music service", "Music service on start command");
        Bundle bundle = intent.getExtras();
        int actionMusic = intent.getIntExtra(MusicServiceConst.ACTION_MUSIC, 0);
        if(bundle != null)
        {
            Song temp = (Song) bundle.get(MusicServiceConst.DATA_SONG);
            if(temp != null)
                song = temp;
        }

        // Send notification
        sendNotificationMedia(song, actionMusic);

        // Handle action music
        handleActionMusic(actionMusic);


        Log.e("Music service", String.valueOf(song.getTITLE()) + " - Action: " + String.valueOf(actionMusic));



        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void sendNotificationMedia(Song song, int actionMusic) {
        Notification notification = createNotification(song, actionMusic);
        startForeground(1, notification);
    }

    public void handleActionMusic(int actionMusic)
    {

        switch (actionMusic)
        {
            case ACTION_PLAY:
            {
                playAudio();
                sendActionToFragment(actionMusic);
                break;
            }
            case ACTION_PAUSE:
            {
                pauseAudio();
                sendActionToFragment(actionMusic);
                break;
            }
            case ACTION_BACK:
            {
                backAudio();
                song.loadThumbnailToTarget(getContentResolver(), img, new Size(350,350));
                prepareSong(ACTION_BACK);
                sendActionToFragment(actionMusic);
                sendNotificationMedia(song, ACTION_PLAY);
                break;
            }
            case ACTION_NEXT:
            {

                nextAudio();
                song.loadThumbnailToTarget(getContentResolver(), img, new Size(350,350));
                prepareSong(ACTION_NEXT);
                sendActionToFragment(actionMusic);
                sendNotificationMedia(song, ACTION_PLAY);
                break;
            }
            case ACTION_CLEAR:
            {
                sendNotificationMedia(song, ACTION_PAUSE);
                pauseAudio();
                sendActionToFragment(actionMusic);
                stopService();
                break;
            }
            case ACTION_UPDATE:
            {
                sendActionToFragment(actionMusic);
            }
        }

    }

    public void playAudio()
    {
        MusicPlayer.Ins().playAudio();
    }

    public void pauseAudio()
    {
        MusicPlayer.Ins().pauseAudio();
    }

    public void backAudio()
    {
        MusicPlayer.Ins().goToThePreviousSong();
        song = MusicPlayer.Ins().getCurSong();
    }

    public void nextAudio()
    {
        MusicPlayer.Ins().goToTheNextSong();
        song = MusicPlayer.Ins().getCurSong();
    }

    public void stopService()
    {
        stopSelf();
    }

    public void prepareSong(int actionMusic) {

        Song precentlySong = MusicPlayer.Ins().getPrecentlySong();
        Song curSong = MusicPlayer.Ins().getCurSong();
        if (!curSong.checkSimilar(precentlySong)) {
            MediaPlayer newMedia = new MediaPlayer();
            MusicPlayer.Ins().setMedia(newMedia);

            try {

                String audioUrl = curSong.getSONG_URL();
                newMedia.setDataSource(audioUrl);
                newMedia.prepareAsync();
                newMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        sendActionToFragment(actionMusic);

                        switch (actionMusic)
                        {
//                            case ACTION_PLAY:
//                            {
//
//                                playAudio();
//                                break;
//                            }
                            case ACTION_BACK:
                            {
                                sendActionToFragment(LOAD_DATA);
                                playAudio();
                                break;
                            }
                            case ACTION_NEXT:
                            {
                                sendActionToFragment(LOAD_DATA);
                                playAudio();
                                break;
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(MusicPlayer.Ins().getLstSong().size() == 1)
            {
                if(actionMusic == ACTION_BACK || actionMusic == ACTION_NEXT)
                {
                    sendActionToFragment(actionMusic);
                    sendActionToFragment(LOAD_DATA);
                }
            }
        }
    }

    public Notification createNotification(Song song)
    {
//        Bitmap bitmap = song.getImgBitmap(getApplication().getContentResolver(), new Size(777,777));
        Bitmap bitmap = TypeSong.imgBitmap;
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Media session");

        boolean isPlaying = MusicPlayer.Ins().isPlayingMusic();
        int iconButtonPlay = R.drawable.ic_smallplay;
        int actionButtonPlay = ACTION_PLAY;
        String titleButtonPlay = "Play";
        if(isPlaying)
        {
            iconButtonPlay = R.drawable.ic_smallpause;
            actionButtonPlay = ACTION_PAUSE;
            titleButtonPlay = "Pause";
        }

        return new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.default_logo)
                .setLargeIcon(bitmap)
                .setContentTitle(song.getTITLE())
                .setContentText(song.getARTIST_NAMES())
                .addAction(R.drawable.ic_backsong, "Previous", null)
                .addAction(iconButtonPlay, "Play", getPendingIntent(this, actionButtonPlay))
                .addAction(R.drawable.ic_nextsong, titleButtonPlay, null)
                .addAction(R.drawable.ic_nextsong, "Close", getPendingIntent(this, ACTION_CLEAR))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .build();
    }

    public Notification createNotification(Song song, int actionMusic)
    {
//        Bitmap bitmap = song.getImgBitmap(getApplication().getContentResolver(), new Size(777,777));
        Bitmap bitmap = null;
        if(song.getType() == TypeSong.Local)
        {
            bitmap = song.getImgBitmap();
        }
        else
        {
            bitmap = TypeSong.imgBitmap;
        }
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Media session");


        int iconButtonPlay = R.drawable.ic_smallplay;
        int actionButtonPlay = ACTION_PLAY;
        String titleButtonPlay = "Play";
        if(actionMusic == ACTION_PLAY)
        {
            iconButtonPlay = R.drawable.ic_smallpause;
            actionButtonPlay = ACTION_PAUSE;
            titleButtonPlay = "Pause";
        }

        Uri soundNotificationUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blank_sound);

        return new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.default_logo)
                .setLargeIcon(bitmap)
                .setContentTitle(song.getTITLE())
                .setContentText(song.getARTIST_NAMES())
                .setSound(soundNotificationUri)
                // Button back song
                .addAction(R.drawable.ic_backsong, "Previous", getPendingIntent(this, ACTION_BACK))
                // Button play song
                .addAction(iconButtonPlay, "Play", getPendingIntent(this, actionButtonPlay))
                // Button next song
                .addAction(R.drawable.ic_nextsong, titleButtonPlay, getPendingIntent(this, ACTION_NEXT))
                // Button close notification
                .addAction(R.drawable.ic_close, "Close", getPendingIntent(this, ACTION_CLEAR))

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .build();
    }

    public PendingIntent getPendingIntent(Context context, int action)
    {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra(MusicServiceConst.ACTION_MUSIC, action);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_MUTABLE);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void sendActionToFragment(int actionMusic)
    {
        Intent intent = new Intent(MusicServiceConst.SEND_DATA_TO_FRAGMENT);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicServiceConst.ACTION_MUSIC, actionMusic);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
